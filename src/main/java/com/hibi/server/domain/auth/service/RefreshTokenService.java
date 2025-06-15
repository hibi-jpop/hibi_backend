package com.hibi.server.domain.auth.service;

import com.hibi.server.domain.auth.dto.response.ReissueResponse;
import com.hibi.server.domain.auth.entity.RefreshToken;
import com.hibi.server.domain.auth.jwt.JwtUtils;
import com.hibi.server.domain.auth.repository.RefreshTokenRepository;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository; // 사용자 정보 확인용 (필요하다면)
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createAndSaveRefreshToken(Long memberId, Authentication authentication) {
        List<RefreshToken> existingTokens = refreshTokenRepository.findByMemberId(memberId);
        existingTokens.forEach(RefreshToken::revoke); // RefreshToken::revoke는 토큰의 상태를 'revoked'로 변경하는 메서드라고 가정
        refreshTokenRepository.saveAll(existingTokens);

        String newRefreshTokenValue = jwtUtils.generateRefreshToken(authentication);
        String hashedNewRefreshToken = passwordEncoder.encode(newRefreshTokenValue); // 해싱

        RefreshToken newRefreshToken = RefreshToken.of(
                memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.BAD_CREDENTIALS)),
                hashedNewRefreshToken,
                jwtUtils.getRefreshTokenExpiryDate(),
                LocalDateTime.now()
        );
        refreshTokenRepository.save(newRefreshToken);
        return newRefreshTokenValue;
    }

    @Transactional
    public ReissueResponse reissueTokens(String submittedRefreshToken) {
        // 1. 제출된 Refresh Token의 유효성 (JWT 형식, 만료 여부 등) 1차 검증
        if (!jwtUtils.validateJwtToken(submittedRefreshToken)) {
            throw new CustomException("유효하지 않거나 만료된 Refresh Token입니다.", ErrorCode.TOKEN_EXPIRED);
        }

        // 2. 제출된 Refresh Token에서 사용자 정보 추출
        Long memberId = jwtUtils.getMemberIdFromJwtToken(submittedRefreshToken); // JWT에서 memberId 추출 (jwtUtils에 해당 메서드 필요)
        if (memberId == null) {
            throw new CustomException("토큰에서 사용자 정보를 추출할 수 없습니다.", ErrorCode.AUTHENTICATION_FAILED);
        }

        String hashedSubmittedRefreshToken = passwordEncoder.encode(submittedRefreshToken);

        // 3. DB에서 현재 Refresh Token 조회 (memberId와 token_value 매칭)
        Optional<RefreshToken> currentTokenRecordOpt = refreshTokenRepository.findByMemberIdAndTokenValueAndRevokedFalse(memberId, hashedSubmittedRefreshToken);

        if (currentTokenRecordOpt.isPresent()) {
            // 시나리오 1: 정상적인 Refresh Token Rotation
            // 제출된 토큰이 현재 유효한 토큰(token_value)과 일치함
            RefreshToken currentTokenRecord = currentTokenRecordOpt.get();

            // 새로운 Access Token 및 Refresh Token 생성
            String newAccessToken = jwtUtils.generateAccessToken(memberId); // memberId 기반 Access Token 생성
            String newRefreshTokenValue = UUID.randomUUID().toString(); // 새로운 Refresh Token 값
            String hashedNewRefreshToken = passwordEncoder.encode(newRefreshTokenValue);

            // DB 업데이트: 기존 레코드를 새 토큰 정보로 업데이트
            currentTokenRecord.updateToken(
                    hashedNewRefreshToken,
                    hashedSubmittedRefreshToken, // 제출된 토큰을 previousTokenValue로 기록
                    jwtUtils.getRefreshTokenExpiryDate(),
                    LocalDateTime.now()
            );
            refreshTokenRepository.save(currentTokenRecord);

            return ReissueResponse.from(newAccessToken); // Access Token만 반환하도록 ReissueResponse 수정 필요 (Refresh Token도 함께 보내야 함)

        } else {
            // 시나리오 2: Replay Attack 또는 기타 유효하지 않은 Refresh Token
            // 제출된 토큰이 현재 token_value와 일치하지 않으므로, previous_token_value에 있는지 확인
            Optional<RefreshToken> previousTokenRecordOpt = refreshTokenRepository.findByMemberIdAndPreviousTokenValueAndRevokedFalse(memberId, hashedSubmittedRefreshToken);

            if (previousTokenRecordOpt.isPresent()) {
                // 시나리오 2a: Replay Attack 탐지! (이전 Refresh Token이 다시 사용됨)
                // 해당 사용자의 모든 Refresh Token 패밀리 즉시 무효화
                invalidateAllRefreshTokensForMember(memberId);
                throw new CustomException("재사용된 Refresh Token 감지. 모든 세션을 만료합니다.", ErrorCode.AUTHENTICATION_FAILED); // 또는 REPLAY_ATTACK_DETECTED 같은 새로운 에러코드
            } else {
                // 시나리오 2b: 유효하지 않거나 찾을 수 없는 Refresh Token
                // (만료되었거나, 위변조되었거나, 이미 revoke 되었거나)
                throw new CustomException("유효하지 않은 Refresh Token입니다.", ErrorCode.BAD_CREDENTIALS);
            }
        }
    }


    /**
     * 특정 회원의 모든 유효한 Refresh Token을 무효화합니다 (로그아웃, 비밀번호 변경 등).
     *
     * @param memberId 무효화할 회원의 ID
     */
    @Transactional
    public void invalidateAllRefreshTokensForMember(Long memberId) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByMemberIdAndRevokedFalse(memberId);
        activeTokens.forEach(RefreshToken::revoke); // 각 토큰의 revoked 상태를 true로 변경
        refreshTokenRepository.saveAll(activeTokens); // 변경사항 DB에 반영
    }

    /**
     * 만료된 Refresh Token들을 데이터베이스에서 주기적으로 삭제합니다.
     * 이 메서드는 스케줄링된 작업 (예: @Scheduled)을 통해 호출될 수 있습니다.
     */
    @Transactional
    public void cleanUpExpiredRefreshTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        refreshTokenRepository.deleteAll(expiredTokens);
        // 또는 deleteByExpiryDateBefore(LocalDateTime.now()); 와 같은 JPA 메소드 활용 가능
    }
}
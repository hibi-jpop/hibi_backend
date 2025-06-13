package com.hibi.server.domain.auth.service;

import com.hibi.server.domain.auth.dto.CustomUserDetails;
import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.dto.response.ReissueResponse;
import com.hibi.server.domain.auth.dto.response.SignInResponse;
import com.hibi.server.domain.auth.jwt.JwtUtils;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.AuthException;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hibi.server.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.hibi.server.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new AuthException(EMAIL_ALREADY_EXISTS);
        }
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new AuthException(NICKNAME_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        return authenticateUser(request.email(), request.password());
    }

    @Transactional
    public void signOut(Long memberId) {
        // Redis 기반 토큰 저장소를 사용한다면 여기서 토큰 무효화 처리
        // 현재는 클라이언트에서 토큰 삭제만 수행하도록 함
    }

    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public ReissueResponse reissueTokens(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new CustomException("유효하지 않은 리프레시 토큰입니다.", ErrorCode.BAD_CREDENTIALS);
        }

        Member member = memberRepository.findByEmail(jwtUtils.getEmailFromJwtToken(refreshToken))
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", ErrorCode.BAD_CREDENTIALS));

        CustomUserDetails userDetails = new CustomUserDetails(member); // CustomUserDetails 생성자가 Member를 받도록 수정 필요

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
//      String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        return ReissueResponse.from(newAccessToken);
    }

    private SignInResponse authenticateUser(String email, String password) {
        // Spring Security 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // 사용자 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", ErrorCode.BAD_CREDENTIALS));

        // 응답 생성
        return SignInResponse.of(accessToken, refreshToken);
    }
}
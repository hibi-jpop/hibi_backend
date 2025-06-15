package com.hibi.server.domain.auth.service;

import com.hibi.server.domain.auth.dto.CustomUserDetails;
import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.dto.response.ReissueResponse;
import com.hibi.server.domain.auth.dto.response.SignInResponse;
import com.hibi.server.domain.auth.jwt.JwtUtils;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.entity.ProviderType;
import com.hibi.server.domain.member.entity.UserRoleType;
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

        Member member = Member.of(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname(),
                ProviderType.NATIVE,
                null,
                null,
                UserRoleType.USER
        );

        memberRepository.save(member);
    }

    @Transactional
    public SignInResponse signIn(SignInRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        return SignInResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public ReissueResponse reissueTokens(String refreshToken) {
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new CustomException(ErrorCode.JWT_INVALID_TOKEN);
        }

        Member member = memberRepository.findByEmail(jwtUtils.getEmailFromJwtToken(refreshToken))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));

        CustomUserDetails userDetails = new CustomUserDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        return ReissueResponse.of(newAccessToken, newRefreshToken);
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


}
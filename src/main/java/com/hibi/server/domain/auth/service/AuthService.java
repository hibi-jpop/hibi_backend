package com.hibi.server.domain.auth.service;

import com.hibi.server.domain.auth.dto.CustomUserDetails;
import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.dto.response.SignInResponse;
import com.hibi.server.domain.auth.jwt.JwtUtils;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.entity.ProviderType;
import com.hibi.server.domain.member.entity.UserRoleType;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hibi.server.global.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static com.hibi.server.global.exception.ErrorCode.NICKNAME_ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

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
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        long memberId = customUserDetails.getId();
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(memberId, authentication);

        return SignInResponse.of(accessToken, refreshToken);
    }

    @Transactional
    public void signOut(Long memberId) {
        refreshTokenService.invalidateAllRefreshTokensForMember(memberId);
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
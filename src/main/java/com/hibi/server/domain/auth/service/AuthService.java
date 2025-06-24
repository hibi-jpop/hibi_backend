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
import com.hibi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.hibi.server.global.exception.ErrorCode.*;

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
        String email = request.email();
        String password = request.password();
        String nickname = request.nickname();

        // 이메일 검사
        if (email == null || email.isBlank()) {
            throw new CustomException("이메일은 필수 입력 값입니다.", INVALID_INPUT_VALUE);
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            throw new CustomException("유효하지 않은 이메일 형식입니다.", INVALID_INPUT_VALUE);
        }

        if (memberRepository.existsByEmail(email)) {
            throw new AuthException(EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 검사
        if (password == null || password.isBlank()) {
            throw new CustomException("비밀번호는 필수 입력 값입니다.", INVALID_INPUT_VALUE);
        }

        if (password.length() < 8) {
            throw new CustomException("비밀번호는 최소 8자 이상이어야 합니다.", INVALID_INPUT_VALUE);
        }

        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~]).{8,}$";
        if (!password.matches(passwordRegex)) {
            throw new AuthException(INVALID_PASSWORD_PATTERN);
        }

        // 닉네임 검사
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException("닉네임은 필수 입력 값입니다.", INVALID_INPUT_VALUE);
        }

        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new CustomException("닉네임은 최소 2글자에서 최대 20자까지 입력 가능합니다.", INVALID_INPUT_VALUE);
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new AuthException(NICKNAME_ALREADY_EXISTS);
        }

        // 회원 생성
        Member member = Member.of(
                email,
                passwordEncoder.encode(password),
                nickname,
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
}
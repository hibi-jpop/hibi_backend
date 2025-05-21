package com.hibi.server.domain.auth.service;

import com.hibi.server.domain.auth.dto.request.SignInRequest;
import com.hibi.server.domain.auth.dto.request.SignUpRequest;
import com.hibi.server.domain.auth.jwt.JwtUtils;
import com.hibi.server.domain.auth.jwt.UserDetailsImpl;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 회원가입 처리
     */
    @Transactional
    public SigninResponse signUp(SignUpRequest request) {
        // 이메일 중복 확인
        if (memberRepository.existsByEmail(request.email())) {
            throw new CustomException("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        }

        // 닉네임 중복 확인
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new CustomException("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
        }

        // 회원 엔티티 생성 및 저장
        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        memberRepository.save(member);

        // 가입 후 자동 로그인 처리
        return authenticateUser(request.email(), request.password());
    }

    /**
     * 로그인 처리
     */
    @Transactional
    public SigninResponse signIn(SignInRequest request) {
        return authenticateUser(request.email(), request.password());
    }

    /**
     * 로그아웃 처리
     */
    @Transactional
    public void signOut(Long memberId) {
        // Redis 기반 토큰 저장소를 사용한다면 여기서 토큰 무효화 처리
        // 현재는 클라이언트에서 토큰 삭제만 수행하도록 함
    }

    /**
     * 이메일 사용 가능 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    /**
     * 닉네임 사용 가능 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    /**
     * 인증 처리 및 토큰 발급 공통 메서드
     */
    private SigninResponse authenticateUser(String email, String password) {
        // Spring Security 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 토큰 생성
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 사용자 정보 조회
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Member member = memberRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        // 토큰 만료 시간 계산
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000); // 1시간 (밀리초)
        long expiresIn = (expiryDate.getTime() - now.getTime()) / 1000; // 초 단위로 변환

        // 응답 생성
        return new SigninResponse(
                jwt,                   // 접근 토큰
                "Bearer",              // 토큰 타입
                expiresIn,             // 만료 시간(초)
                member.getId(),        // 회원 ID
                member.getNickname(),  // 닉네임
                member.getEmail(),     // 이메일
                member.getProfileUrl() // 프로필 이미지 URL
        );
    }
}
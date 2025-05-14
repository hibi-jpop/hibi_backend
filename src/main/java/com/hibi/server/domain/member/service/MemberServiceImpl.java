package com.hibi.server.domain.member.service;

import com.hibi.server.domain.member.dto.MemberResponse;
import com.hibi.server.domain.member.dto.MemberSignInRequest;
import com.hibi.server.domain.member.dto.MemberSignUpRequest;
import com.hibi.server.domain.member.dto.MemberUpdateRequest;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.entity.RoleType;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.common.AuthResponse;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public MemberResponse signUp(MemberSignUpRequest request) {
        // 이메일 중복 확인
        if (!isEmailAvailable(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 닉네임 중복 확인
        if (!isNicknameAvailable(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 회원 엔티티 생성
        Member member = Member.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .role(RoleType.USER)
                .build();

        // 저장
        Member savedMember = memberRepository.save(member);

        // 응답 DTO 변환 후 반환
        return MemberResponse.of(savedMember);
    }

    @Override
    public AuthResponse signIn(MemberSignInRequest request) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // JWT 토큰 생성 (실제 구현은 별도 서비스 클래스에서 처리할 수 있음)
        String accessToken = generateAccessToken(member);
        String refreshToken = generateRefreshToken(member);

        // 토큰 정보를 포함한 응답 반환
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .member(MemberResponse.of(member))
                .build();
    }

    @Override
    @Transactional
    public void signOut(Long memberId) {
        // 실제 로그아웃 처리는 Spring Security 설정에 따라 달라질 수 있음
        // 예: 리프레시 토큰 블랙리스트 처리 등
    }

    @Override
    public MemberResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        return MemberResponse.of(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMemberInfo(Long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 닉네임 변경 요청이 있고, 현재 닉네임과 다를 경우에만 중복 체크
        if (request.getNickname() != null && !request.getNickname().equals(member.getNickname())) {
            if (!isNicknameAvailable(request.getNickname())) {
                throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }
            member.updateNickname(request.getNickname());
        }
        
        // 프로필 이미지 변경 요청이 있을 경우
        if (request.getProfileImage() != null) {
            member.updateProfileImage(request.getProfileImage());
        }
        
        // 비밀번호 변경 요청이 있을 경우
        if (request.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            member.updatePassword(encodedPassword);
        }
        
        return MemberResponse.of(member);
    }

    @Override
    @Transactional
    public void withdrawMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 소프트 삭제로 처리하거나 실제 삭제할 수 있음
        member.setDeleted(true); // 소프트 삭제 예시
        // memberRepository.delete(member); // 실제 삭제 예시
    }

    @Override
    public List<MemberResponse> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::of)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !memberRepository.existsByEmail(email);
    }

    @Override
    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }
    
//    private String generateAccessToken(Member member) {
//        // JWT 액세스 토큰 생성 로직
//        return "sample.access.token";
//    }
//
//    private String generateRefreshToken(Member member) {
//        // JWT 리프레시 토큰 생성 로직
//        return "sample.refresh.token";
//    }
}
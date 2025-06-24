package com.hibi.server.domain.member.service;


import com.hibi.server.domain.auth.service.RefreshTokenService;
import com.hibi.server.domain.member.dto.request.MemberUpdateRequest;
import com.hibi.server.domain.member.dto.response.MemberProfileResponse;
import com.hibi.server.domain.member.entity.Member;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public MemberProfileResponse getMemberProfileById(long id) {
        return memberRepository.findById(id)
                .map(MemberProfileResponse::from)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
    }

    @Transactional
    public MemberProfileResponse updateMemberInfo(long memberId, MemberUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        // 닉네임 검사
        String nickname = request.nickname();
        if (nickname == null || nickname.isBlank()) {
            throw new CustomException("닉네임은 필수 입력 값입니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new CustomException("닉네임은 2자 이상 20자 이하로 입력해주세요.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!member.getNickname().equals(nickname)) {
            if (memberRepository.existsByNickname(nickname)) {
                throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }
            member.updateNickname(nickname);
        }

        // 비밀번호 검사
        String password = request.password();
        if (password == null || password.isBlank()) {
            throw new CustomException("비밀번호는 필수 입력 값입니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (password.length() < 8) {
            throw new CustomException("비밀번호는 최소 8자 이상이어야 합니다.", ErrorCode.INVALID_INPUT_VALUE);
        }

        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~]).{8,}$";
        if (!password.matches(passwordPattern)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_PATTERN);
        }

        String encodedNewPassword = passwordEncoder.encode(password);
        member.updatePasswordHash(encodedNewPassword);

        return MemberProfileResponse.from(member);
    }

    @Transactional
    public void withdrawMember(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
        member.softDelete(LocalDateTime.now());
        refreshTokenService.invalidateAllRefreshTokensForMember(memberId);
    }

    public List<MemberProfileResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberProfileResponse::from)
                .collect(Collectors.toList());
    }

    // TODO: 이메일 체크하는 로직 필요
    public boolean checkEmailAvailability(String email) {
        return !memberRepository.existsByEmail(email);
    }

    public boolean checkNicknameAvailability(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }
}
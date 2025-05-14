package com.hibi.server.domain.member.service;

import com.hibi.server.domain.member.dto.MemberResponse;
import com.hibi.server.domain.member.dto.MemberSignInRequest;
import com.hibi.server.domain.member.dto.MemberSignUpRequest;
import com.hibi.server.domain.member.dto.MemberUpdateRequest;
import com.hibi.server.global.common.AuthResponse;

import java.util.List;

public interface MemberService {
    MemberResponse signUp(MemberSignUpRequest request);
    AuthResponse signIn(MemberSignInRequest request);
    void signOut(Long memberId);
    MemberResponse getMemberInfo(Long memberId);
    MemberResponse updateMemberInfo(Long memberId, MemberUpdateRequest request);
    void withdrawMember(Long memberId);
    List<MemberResponse> getAllMembers();
    boolean isEmailAvailable(String email);
    boolean isNicknameAvailable(String nickname);
}
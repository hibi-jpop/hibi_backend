package com.hibi.server.domain.member.service;


import com.hibi.server.domain.member.dto.response.MemberProfileResponse;
import com.hibi.server.domain.member.repository.MemberRepository;
import com.hibi.server.global.exception.CustomException;
import com.hibi.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberProfileResponse getMemberProfileById(long id) {
        return memberRepository.findById(id)
                .map(member -> MemberProfileResponse.of(
                        member.getEmail(),
                        member.getNickname(),
                        member.getCreatedAt()
                ))
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));
    }

}
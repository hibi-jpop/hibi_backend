package com.hibi.server.domain.member.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Builder(access = PRIVATE)
public record MemberProfileResponse(
        @NotNull String email,
        @NotNull String nickname,
        @NotNull LocalDateTime createdAt
) {
    public static MemberProfileResponse of(
            final String email,
            final String nickname,
            final LocalDateTime createdAt
    ) {
        return new MemberProfileResponse(email, nickname, createdAt);
    }
}

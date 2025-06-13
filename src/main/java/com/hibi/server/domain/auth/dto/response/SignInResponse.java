package com.hibi.server.domain.auth.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static lombok.AccessLevel.PRIVATE;

@Builder(access = PRIVATE)
public record SignInResponse(
        @NotNull String accessToken,
        @NotNull String refreshToken
) {

    public static SignInResponse of(final String accessToken, final String refreshToken) {
        return SignInResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
}
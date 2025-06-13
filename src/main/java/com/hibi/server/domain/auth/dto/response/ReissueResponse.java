package com.hibi.server.domain.auth.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import static lombok.AccessLevel.PRIVATE;

@Builder(access = PRIVATE)
public record ReissueResponse(
        @NotNull String accessToken
) {

    public static ReissueResponse from(final String accessToken) {
        return ReissueResponse.builder().accessToken(accessToken).build();
    }
}
package com.hibi.server.global.response;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ErrorResponse(
        @NotNull boolean success,
        @NotNull String message
) implements ApiResponse {

    public static ErrorResponse of(final String message) {
        return ErrorResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
package com.hibi.server.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record SignInRequest(
        @NotNull String email,
        @NotNull String password
) {}

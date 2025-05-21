package com.hibi.server.domain.auth.dto.request;

import jakarta.validation.constraints.*;

public record SignUpRequest(
    @NotNull String email,
    @NotNull String password,
    @NotNull String nickname
) {}
package com.hibi.server.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효하지 않은 이메일 형식입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~])[a-zA-Z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~]{8,}$",
                message = "비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.")
        String password,

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Size(max = 20, message = "닉네임은 최대 20자까지 입력 가능합니다.")
        String nickname
) {
}
package com.hibi.server.global.exception;

import lombok.Getter;

@Getter
public class AuthException extends CustomException {
    private final ErrorCode errorCode;

    public AuthException(final ErrorCode errorCode) {
        super("[Auth Exception] " + errorCode.getMessage(), errorCode);
        this.errorCode = errorCode;
    }
}

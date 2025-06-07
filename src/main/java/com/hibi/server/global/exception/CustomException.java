package com.hibi.server.global.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final HttpStatus status;

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.status = errorCode.getHttpStatus();
    }

}

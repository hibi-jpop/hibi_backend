package com.hibi.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // --- 인증/인가 (Authentication/Authorization) 관련 에러 ---
    // JWT 토큰 관련
    JWT_INVALID_TOKEN("A001", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_EXPIRED_TOKEN("A002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    JWT_UNSUPPORTED_TOKEN("A003", "지원하지 않는 토큰 형식입니다.", HttpStatus.UNAUTHORIZED),
    JWT_SIGNATURE_INVALID("A004", "토큰 서명이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED),
    JWT_MISSING_TOKEN("A005", "토큰이 누락되었습니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("A006", "리프레시 토큰을 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("A007", "유효하지 않은 리프레시 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // 인증 과정 관련
    AUTHENTICATION_FAILED("A010", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_ACCESS("A011", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN), // 권한 부족
    BAD_CREDENTIALS("A012", "아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 회원 가입 관련
    EMAIL_ALREADY_EXISTS("A020", "이미 등록된 이메일입니다.", HttpStatus.CONFLICT), // 409 Conflict
    NICKNAME_ALREADY_EXISTS("A021", "이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD_PATTERN("A022", "비밀번호 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST), // 400 Bad Request

    // --- 일반적인 에러 코드 ---
    INVALID_INPUT_VALUE("C001", "잘못된 입력 값입니다.", HttpStatus.BAD_REQUEST), // 400 Bad Request
    ENTITY_NOT_FOUND("C002", "요청하신 자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404 Not Found
    INTERNAL_SERVER_ERROR("S001", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
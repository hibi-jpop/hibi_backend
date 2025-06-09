package com.hibi.server.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${api.base-url")
    private String apiBaseUrl;


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleCustomException(CustomException ex) {
        log.error("CustomException occurred: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatus(), ex.getMessage());
        problemDetail.setTitle(ex.getStatus().getReasonPhrase());
        problemDetail.setType(URI.create(apiBaseUrl + getProblemTypePath(ex.getErrorCode())));

    }

    // @Valid 검증 실패 시 발생하는 MethodArgumentNotValidException 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {


    }

    // 그 외 예상치 못한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {

    }

    // ProblemDetail의 type URI 경로를 생성하는 헬퍼 메서드
    private String getProblemTypePath(ErrorCode errorCode) {
        // 기본값

//        if (errorCode != null && errorCode.getCode() != null && !errorCode.getCode().isEmpty()) {
//            char category = errorCode.getCode().charAt(0);
//            path = switch (category) {
//                case 'A' -> "/problems/auth-error"; // 인증/인가 오류
//                case 'C' -> "/problems/common-error"; // 일반 클라이언트 오류
//                case 'S' -> "/problems/server-error"; // 서버 오류
//                default -> "/problems/other-error";
//            };
//        }
        //TODO: swagger 문서 작성 완료 시 index가 아닌 특정 항목으로 이동할 수 있게 변경
//        return ":8080/swagger-ui/index.html#/problems/" + errorCode.getCode();
        return ":8080/swagger-ui/index.html#/";
    }
}
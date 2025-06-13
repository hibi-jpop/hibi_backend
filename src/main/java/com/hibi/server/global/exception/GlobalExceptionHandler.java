package com.hibi.server.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${api.base-url}")
    private String apiBaseUrl;

    // --- 공통 ProblemDetail 생성 헬퍼 메서드 ---
    private ProblemDetail createProblemDetail(HttpStatus status, String detailMessage, ErrorCode errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detailMessage);
        problemDetail.setTitle(status.getReasonPhrase()); // HTTP 상태 코드의 이유 문구 사용
        problemDetail.setType(URI.create(apiBaseUrl + getProblemTypePath(errorCode)));

        try {
            problemDetail.setInstance(URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString()));
        } catch (IllegalStateException e) {
            log.warn("요청 경로 요청 중 오류가 발생했습니다. {}", e.getMessage());
            problemDetail.setInstance(URI.create(apiBaseUrl + "/errors/unknown-instance"));
        }

        problemDetail.setProperty("errorCode", errorCode.getCode());
        return problemDetail;
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ProblemDetail> handleCustomException(CustomException ex) {
        log.error(ex.getMessage());
        ProblemDetail problemDetail = createProblemDetail(ex.getStatus(), ex.getMessage(), ex.getErrorCode());
        return ResponseEntity.status(ex.getStatus()).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        log.error("[MethodArgumentNotValidException] : 유효성 검사 실패 : {}", ex.getMessage());
        ProblemDetail problemDetail = createProblemDetail(status, "입력 값 유효성 검사 실패", ErrorCode.INVALID_INPUT_VALUE);
        return ResponseEntity.status(status).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String defaultDetailMessage = "서버 내부 오류가 발생했습니다.";
        log.error("[Internal Server Error] : {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = createProblemDetail(status, defaultDetailMessage, ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(status).body(problemDetail);
    }

    // ProblemDetail의 type URI 경로를 생성하는 헬퍼 메서드
    private String getProblemTypePath(ErrorCode errorCode) {
        // 기본값

//        if (errorCode != null && errorCode.getCode() != null && !errorCode.getCode().isEmpty()) {
//            char category = errorCode.getCode().charAt(0);
//            path = switch (category) {
//                case 'A' -> "/problems/auth-error"; // 인증/인가 오류
//                case 'C' -> "/problems/common-error"; // 일반 클라이언트 오류
//                case 'S' -> "/problems/serv   er-error"; // 서버 오류
//                default -> "/problems/other-error";
//            };
//        }
        //TODO: swagger 문서 작성 완료 시 index가 아닌 특정 항목으로 이동할 수 있게 변경
//        return ":8080/swagger-ui/index.html#/problems/" + errorCode.getCode();
        return ":8080/swagger-ui/index.html#/";
    }
}
package com.example.musicbot.exception;

import com.example.musicbot.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse response = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .internalServerError()
                .body(response);
    }
}

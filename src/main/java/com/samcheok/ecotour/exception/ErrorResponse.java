package com.samcheok.ecotour.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

/**
 * 표준 오류 응답 포맷.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
    }
}

package com.samcheok.ecotour.exception;

/**
 * 중복 리소스(예: 이미 등록된 이메일, 이미 찍은 스탬프) 생성 시도 시 발생.
 * GlobalExceptionHandler 에서 409 Conflict 로 변환.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

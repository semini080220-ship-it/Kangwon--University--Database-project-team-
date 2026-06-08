package com.samcheok.ecotour.exception;

/**
 * 요청한 리소스를 찾을 수 없을 때 발생. GlobalExceptionHandler 에서 404 로 변환.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, Long id) {
        return new ResourceNotFoundException(entity + "(id=" + id + ")를 찾을 수 없습니다.");
    }
}

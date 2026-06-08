package com.samcheok.ecotour.dto;

/**
 * 사용자 보유 스탬프 수 응답.
 */
public record StampCountResponse(
        Long userId,
        long stampCount
) {
}

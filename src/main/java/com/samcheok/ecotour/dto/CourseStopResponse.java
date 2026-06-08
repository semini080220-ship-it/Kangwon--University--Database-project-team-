package com.samcheok.ecotour.dto;

/**
 * 코스 방문지 응답(순서 + 관광지 정보).
 */
public record CourseStopResponse(
        int visitOrder,
        Long attractionId,
        String attractionName
) {
}

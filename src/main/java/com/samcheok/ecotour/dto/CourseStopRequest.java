package com.samcheok.ecotour.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 코스 내 방문지 한 곳(관광지 + 방문 순서) 요청.
 */
public record CourseStopRequest(

        @NotNull(message = "관광지 ID는 필수입니다.")
        Long attractionId,

        @Positive(message = "방문 순서는 1 이상이어야 합니다.")
        int visitOrder
) {
}

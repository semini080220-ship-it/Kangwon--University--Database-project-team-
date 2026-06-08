package com.samcheok.ecotour.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 리뷰 작성 요청 DTO. 평점은 1~5.
 */
public record ReviewCreateRequest(

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "관광지 ID는 필수입니다.")
        Long attractionId,

        @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5 이하여야 합니다.")
        int rating,

        @Size(max = 1000, message = "내용은 1000자 이하여야 합니다.")
        String content
) {
}

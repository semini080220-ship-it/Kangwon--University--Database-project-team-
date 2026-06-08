package com.samcheok.ecotour.dto;

/**
 * 관광지 평점 요약(평균 + 리뷰 수). 분산 관광지 신뢰도 노출용.
 */
public record AttractionRatingResponse(
        Long attractionId,
        double averageRating,
        long reviewCount
) {
}

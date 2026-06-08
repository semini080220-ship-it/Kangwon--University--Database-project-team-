package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.Review;
import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO.
 */
public record ReviewResponse(
        Long id,
        Long userId,
        String nickname,
        Long attractionId,
        int rating,
        String content,
        LocalDateTime createdAt
) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getNickname(),
                r.getAttraction().getId(),
                r.getRating(),
                r.getContent(),
                r.getCreatedAt()
        );
    }
}

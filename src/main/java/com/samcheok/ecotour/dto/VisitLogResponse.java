package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.VisitLog;
import com.samcheok.ecotour.domain.VisitType;
import java.time.LocalDateTime;

/**
 * 방문/인증 기록 응답 DTO.
 */
public record VisitLogResponse(
        Long id,
        Long userId,
        Long attractionId,
        String attractionName,
        VisitType visitType,
        String note,
        LocalDateTime visitedAt
) {
    public static VisitLogResponse from(VisitLog v) {
        return new VisitLogResponse(
                v.getId(),
                v.getUser().getId(),
                v.getAttraction().getId(),
                v.getAttraction().getName(),
                v.getVisitType(),
                v.getNote(),
                v.getVisitedAt()
        );
    }
}

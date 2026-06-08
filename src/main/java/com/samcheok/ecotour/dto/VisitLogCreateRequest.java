package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.VisitType;
import jakarta.validation.constraints.NotNull;

/**
 * 방문/인증 생성 요청 DTO.
 */
public record VisitLogCreateRequest(

        @NotNull(message = "사용자 ID는 필수입니다.")
        Long userId,

        @NotNull(message = "관광지 ID는 필수입니다.")
        Long attractionId,

        @NotNull(message = "인증 유형은 필수입니다.")
        VisitType visitType,

        String note
) {
}

package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.CongestionLevel;
import jakarta.validation.constraints.NotNull;

/**
 * 실시간 혼잡도 갱신 요청 DTO.
 */
public record CongestionUpdateRequest(
        @NotNull(message = "혼잡도는 필수입니다.")
        CongestionLevel congestionLevel
) {
}

package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 관광지 등록 요청 DTO. Bean Validation 으로 입력 검증.
 */
public record AttractionCreateRequest(

        @NotBlank(message = "관광지 이름은 필수입니다.")
        @Size(max = 100, message = "관광지 이름은 100자 이하여야 합니다.")
        String name,

        String description,

        @NotNull(message = "카테고리는 필수입니다.")
        Category category,

        Double latitude,

        Double longitude,

        String address,

        CongestionLevel congestionLevel,

        boolean localGem
) {
}

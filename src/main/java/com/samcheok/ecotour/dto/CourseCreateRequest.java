package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.TourTheme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 스탬프 코스 생성 요청 DTO.
 */
public record CourseCreateRequest(

        @NotBlank(message = "코스 이름은 필수입니다.")
        String name,

        String description,

        @NotNull(message = "테마는 필수입니다.")
        TourTheme theme,

        @NotEmpty(message = "코스에는 최소 1개의 방문지가 필요합니다.")
        @Valid
        List<CourseStopRequest> stops
) {
}

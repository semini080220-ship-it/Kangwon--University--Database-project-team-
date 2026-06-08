package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.Course;
import com.samcheok.ecotour.domain.TourTheme;
import java.util.List;

/**
 * 코스 응답 DTO (방문 순서대로 정렬된 방문지 목록 포함).
 */
public record CourseResponse(
        Long id,
        String name,
        String description,
        TourTheme theme,
        List<CourseStopResponse> stops
) {
    public static CourseResponse from(Course course) {
        List<CourseStopResponse> stops = course.getDetails().stream()
                .map(d -> new CourseStopResponse(
                        d.getVisitOrder(),
                        d.getAttraction().getId(),
                        d.getAttraction().getName()))
                .toList();
        return new CourseResponse(course.getId(), course.getName(),
                course.getDescription(), course.getTheme(), stops);
    }
}

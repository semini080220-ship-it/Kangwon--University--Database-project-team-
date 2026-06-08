package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Course;
import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.dto.CourseCreateRequest;
import com.samcheok.ecotour.dto.CourseResponse;
import com.samcheok.ecotour.dto.CourseStopRequest;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.CourseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스탬프 코스 비즈니스 로직. 방문지(관광지) 존재 검증 + 순서 지정.
 */
@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final AttractionRepository attractionRepository;

    public CourseService(CourseRepository courseRepository, AttractionRepository attractionRepository) {
        this.courseRepository = courseRepository;
        this.attractionRepository = attractionRepository;
    }

    @Transactional
    public CourseResponse create(CourseCreateRequest request) {
        Course course = new Course(request.name(), request.description(), request.theme());
        for (CourseStopRequest stop : request.stops()) {
            Attraction attraction = attractionRepository.findById(stop.attractionId())
                    .orElseThrow(() -> ResourceNotFoundException.of("관광지", stop.attractionId()));
            course.addAttraction(attraction, stop.visitOrder());
        }
        return CourseResponse.from(courseRepository.save(course));
    }

    public CourseResponse getById(Long id) {
        return CourseResponse.from(courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("코스", id)));
    }

    public List<CourseResponse> getAll() {
        return courseRepository.findAll().stream().map(CourseResponse::from).toList();
    }

    public List<CourseResponse> getByTheme(TourTheme theme) {
        return courseRepository.findByTheme(theme).stream().map(CourseResponse::from).toList();
    }
}

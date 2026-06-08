package com.samcheok.ecotour.repository;

import com.samcheok.ecotour.domain.Course;
import com.samcheok.ecotour.domain.TourTheme;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTheme(TourTheme theme);
}

package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.domain.Course;
import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.dto.CourseCreateRequest;
import com.samcheok.ecotour.dto.CourseResponse;
import com.samcheok.ecotour.dto.CourseStopRequest;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.CourseRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService 단위 테스트")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private AttractionRepository attractionRepository;
    @InjectMocks
    private CourseService courseService;

    private Attraction attraction(Long id, String name) {
        Attraction a = new Attraction(name, "d", Category.COAST, 37.0, 129.0, "삼척", CongestionLevel.LOW, true);
        ReflectionTestUtils.setField(a, "id", id);
        return a;
    }

    @Test
    @DisplayName("코스를 방문 순서대로 생성한다")
    void create_success() {
        CourseCreateRequest req = new CourseCreateRequest("힐링 해안 코스", "한적한 해안", TourTheme.HEALING,
                List.of(new CourseStopRequest(10L, 1), new CourseStopRequest(20L, 2)));
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(attractionRepository.findById(20L)).thenReturn(Optional.of(attraction(20L, "덕봉산")));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 1L);
            return c;
        });

        CourseResponse res = courseService.create(req);

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.stops()).hasSize(2);
        assertThat(res.stops()).extracting("attractionName").containsExactly("초곡", "덕봉산");
        assertThat(res.stops()).extracting("visitOrder").containsExactly(1, 2);
    }

    @Test
    @DisplayName("존재하지 않는 관광지를 코스에 넣으면 ResourceNotFoundException")
    void create_attractionNotFound() {
        CourseCreateRequest req = new CourseCreateRequest("코스", "d", TourTheme.ECO,
                List.of(new CourseStopRequest(999L, 1)));
        when(attractionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.create(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("코스를 조회한다")
    void getById_found() {
        Course course = new Course("코스", "d", TourTheme.ECO);
        course.addAttraction(attraction(10L, "초곡"), 1);
        ReflectionTestUtils.setField(course, "id", 1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        CourseResponse res = courseService.getById(1L);

        assertThat(res.name()).isEqualTo("코스");
        assertThat(res.stops()).extracting("attractionName").containsExactly("초곡");
    }

    @Test
    @DisplayName("없는 코스 조회 시 ResourceNotFoundException")
    void getById_notFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> courseService.getById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }
}

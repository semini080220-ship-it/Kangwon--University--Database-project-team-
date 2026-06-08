package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.domain.Review;
import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.domain.User;
import com.samcheok.ecotour.dto.AttractionRatingResponse;
import com.samcheok.ecotour.dto.ReviewCreateRequest;
import com.samcheok.ecotour.dto.ReviewResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.ReviewRepository;
import com.samcheok.ecotour.repository.UserRepository;
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
@DisplayName("ReviewService 단위 테스트")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AttractionRepository attractionRepository;
    @InjectMocks
    private ReviewService reviewService;

    private User user(Long id) {
        User u = new User("a@test.com", "관광객", TourTheme.ECO);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    private Attraction attraction(Long id, String name) {
        Attraction a = new Attraction(name, "d", Category.COAST, 37.0, 129.0, "삼척", CongestionLevel.LOW, true);
        ReflectionTestUtils.setField(a, "id", id);
        return a;
    }

    @Test
    @DisplayName("리뷰 작성 성공")
    void create_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            ReflectionTestUtils.setField(r, "id", 1L);
            return r;
        });

        ReviewResponse res = reviewService.create(new ReviewCreateRequest(1L, 10L, 5, "최고"));

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.rating()).isEqualTo(5);
        assertThat(res.nickname()).isEqualTo("관광객");
    }

    @Test
    @DisplayName("없는 사용자면 ResourceNotFoundException")
    void create_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.create(new ReviewCreateRequest(99L, 10L, 5, "x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("없는 관광지면 ResourceNotFoundException")
    void create_attractionNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.create(new ReviewCreateRequest(1L, 99L, 5, "x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("평균 평점과 리뷰 수를 집계한다")
    void getAverageRating() {
        when(reviewRepository.findAverageRatingByAttractionId(10L)).thenReturn(4.5);
        when(reviewRepository.countByAttractionId(10L)).thenReturn(2L);

        AttractionRatingResponse res = reviewService.getAverageRating(10L);

        assertThat(res.averageRating()).isEqualTo(4.5);
        assertThat(res.reviewCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("리뷰가 없으면 평균 0.0")
    void getAverageRating_noReviews() {
        when(reviewRepository.findAverageRatingByAttractionId(10L)).thenReturn(null);
        when(reviewRepository.countByAttractionId(10L)).thenReturn(0L);

        AttractionRatingResponse res = reviewService.getAverageRating(10L);

        assertThat(res.averageRating()).isEqualTo(0.0);
        assertThat(res.reviewCount()).isZero();
    }

    @Test
    @DisplayName("관광지별 리뷰를 조회한다")
    void getByAttraction() {
        Review r = new Review(user(1L), attraction(10L, "초곡"), 5, "좋아요");
        ReflectionTestUtils.setField(r, "id", 1L);
        when(reviewRepository.findByAttractionId(10L)).thenReturn(List.of(r));

        assertThat(reviewService.getByAttraction(10L)).hasSize(1)
                .extracting(ReviewResponse::rating).containsExactly(5);
    }
}

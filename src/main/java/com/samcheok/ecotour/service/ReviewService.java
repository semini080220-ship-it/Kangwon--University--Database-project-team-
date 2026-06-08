package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Review;
import com.samcheok.ecotour.domain.User;
import com.samcheok.ecotour.dto.AttractionRatingResponse;
import com.samcheok.ecotour.dto.ReviewCreateRequest;
import com.samcheok.ecotour.dto.ReviewResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.ReviewRepository;
import com.samcheok.ecotour.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리뷰/평점 비즈니스 로직. 평균 평점 집계로 분산 관광지 신뢰도를 제공.
 */
@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AttractionRepository attractionRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         AttractionRepository attractionRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.attractionRepository = attractionRepository;
    }

    @Transactional
    public ReviewResponse create(ReviewCreateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> ResourceNotFoundException.of("사용자", request.userId()));
        Attraction attraction = attractionRepository.findById(request.attractionId())
                .orElseThrow(() -> ResourceNotFoundException.of("관광지", request.attractionId()));
        Review saved = reviewRepository.save(
                new Review(user, attraction, request.rating(), request.content()));
        return ReviewResponse.from(saved);
    }

    public List<ReviewResponse> getByAttraction(Long attractionId) {
        return reviewRepository.findByAttractionId(attractionId).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public AttractionRatingResponse getAverageRating(Long attractionId) {
        Double average = reviewRepository.findAverageRatingByAttractionId(attractionId);
        long count = reviewRepository.countByAttractionId(attractionId);
        double rounded = (average == null) ? 0.0 : Math.round(average * 10.0) / 10.0;
        return new AttractionRatingResponse(attractionId, rounded, count);
    }
}

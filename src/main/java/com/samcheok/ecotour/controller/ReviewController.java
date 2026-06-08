package com.samcheok.ecotour.controller;

import com.samcheok.ecotour.dto.AttractionRatingResponse;
import com.samcheok.ecotour.dto.ReviewCreateRequest;
import com.samcheok.ecotour.dto.ReviewResponse;
import com.samcheok.ecotour.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 리뷰/평점 REST API.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(request));
    }

    @GetMapping
    public List<ReviewResponse> listByAttraction(@RequestParam Long attractionId) {
        return reviewService.getByAttraction(attractionId);
    }

    @GetMapping("/average")
    public AttractionRatingResponse average(@RequestParam Long attractionId) {
        return reviewService.getAverageRating(attractionId);
    }
}

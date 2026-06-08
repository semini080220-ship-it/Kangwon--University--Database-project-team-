package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.dto.AttractionRatingResponse;
import com.samcheok.ecotour.dto.ReviewCreateRequest;
import com.samcheok.ecotour.dto.ReviewResponse;
import com.samcheok.ecotour.service.ReviewService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@DisplayName("ReviewController 슬라이스 테스트")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ReviewService reviewService;

    @Test
    @DisplayName("POST 유효 -> 201")
    void create_valid() throws Exception {
        when(reviewService.create(any())).thenReturn(new ReviewResponse(
                1L, 1L, "관광객", 10L, 5, "최고", LocalDateTime.now()));

        mockMvc.perform(post("/api/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewCreateRequest(1L, 10L, 5, "최고"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    @DisplayName("POST 평점 범위 초과(6) -> 400")
    void create_invalidRating() throws Exception {
        mockMvc.perform(post("/api/reviews").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReviewCreateRequest(1L, 10L, 6, "x"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 평균 평점 -> 200")
    void average() throws Exception {
        when(reviewService.getAverageRating(10L)).thenReturn(new AttractionRatingResponse(10L, 4.5, 2L));
        mockMvc.perform(get("/api/reviews/average").param("attractionId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.5))
                .andExpect(jsonPath("$.reviewCount").value(2));
    }
}

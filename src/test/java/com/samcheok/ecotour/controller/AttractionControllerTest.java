package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.dto.AttractionCreateRequest;
import com.samcheok.ecotour.dto.AttractionResponse;
import com.samcheok.ecotour.dto.CongestionUpdateRequest;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.service.AttractionService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AttractionController.class)
@DisplayName("AttractionController 슬라이스 테스트")
class AttractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttractionService attractionService;

    @Test
    @DisplayName("POST 유효 요청 -> 201 Created")
    void create_valid() throws Exception {
        AttractionCreateRequest req = new AttractionCreateRequest(
                "초곡", "산책로", Category.COAST, 37.36, 129.26, "근덕면", CongestionLevel.LOW, true);
        when(attractionService.create(any())).thenReturn(
                new AttractionResponse(10L, "초곡", "산책로", Category.COAST, 37.36, 129.26, "근덕면", CongestionLevel.LOW, true));

        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("초곡"));
    }

    @Test
    @DisplayName("POST 이름 누락 -> 400 Bad Request (검증)")
    void create_blankName() throws Exception {
        AttractionCreateRequest req = new AttractionCreateRequest(
                "   ", null, Category.COAST, null, null, null, null, false);

        mockMvc.perform(post("/api/attractions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 존재 -> 200")
    void getById_found() throws Exception {
        when(attractionService.getById(1L)).thenReturn(
                new AttractionResponse(1L, "쏠비치", null, Category.COAST, null, null, null, CongestionLevel.HIGH, false));

        mockMvc.perform(get("/api/attractions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("쏠비치"));
    }

    @Test
    @DisplayName("GET 없음 -> 404 Not Found")
    void getById_missing() throws Exception {
        when(attractionService.getById(99L)).thenThrow(new ResourceNotFoundException("not found"));

        mockMvc.perform(get("/api/attractions/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET 대안 추천 -> 200 + 목록")
    void alternatives() throws Exception {
        when(attractionService.recommendAlternatives(1L)).thenReturn(List.of(
                new AttractionResponse(3L, "초곡", null, Category.COAST, null, null, null, CongestionLevel.LOW, true)));

        mockMvc.perform(get("/api/attractions/1/alternatives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("초곡"));
    }

    @Test
    @DisplayName("PATCH 혼잡도 갱신 -> 200")
    void updateCongestion() throws Exception {
        CongestionUpdateRequest req = new CongestionUpdateRequest(CongestionLevel.LOW);
        when(attractionService.updateCongestion(eq(1L), eq(CongestionLevel.LOW))).thenReturn(
                new AttractionResponse(1L, "쏠비치", null, Category.COAST, null, null, null, CongestionLevel.LOW, false));

        mockMvc.perform(patch("/api/attractions/1/congestion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.congestionLevel").value("LOW"));
    }
}

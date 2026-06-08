package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.domain.VisitType;
import com.samcheok.ecotour.dto.VisitLogCreateRequest;
import com.samcheok.ecotour.dto.VisitLogResponse;
import com.samcheok.ecotour.exception.DuplicateResourceException;
import com.samcheok.ecotour.service.VisitLogService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VisitLogController.class)
@DisplayName("VisitLogController 슬라이스 테스트")
class VisitLogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private VisitLogService visitLogService;

    @Test
    @DisplayName("POST 유효 -> 201")
    void create_valid() throws Exception {
        when(visitLogService.create(any())).thenReturn(new VisitLogResponse(
                100L, 1L, 10L, "초곡", VisitType.STAMP, "첫 방문", LocalDateTime.now()));

        mockMvc.perform(post("/api/visits").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VisitLogCreateRequest(1L, 10L, VisitType.STAMP, "첫 방문"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attractionName").value("초곡"));
    }

    @Test
    @DisplayName("POST 필수값 누락 -> 400")
    void create_missingField() throws Exception {
        mockMvc.perform(post("/api/visits").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VisitLogCreateRequest(null, 10L, VisitType.STAMP, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 스탬프 중복 -> 409")
    void create_duplicate() throws Exception {
        when(visitLogService.create(any())).thenThrow(new DuplicateResourceException("중복"));
        mockMvc.perform(post("/api/visits").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VisitLogCreateRequest(1L, 10L, VisitType.STAMP, null))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET 사용자 방문기록 -> 200")
    void listByUser() throws Exception {
        when(visitLogService.getByUser(1L)).thenReturn(List.of(new VisitLogResponse(
                100L, 1L, 10L, "초곡", VisitType.STAMP, "n", LocalDateTime.now())));
        mockMvc.perform(get("/api/visits").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].attractionName").value("초곡"));
    }
}

package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.dto.CourseCreateRequest;
import com.samcheok.ecotour.dto.CourseResponse;
import com.samcheok.ecotour.dto.CourseStopRequest;
import com.samcheok.ecotour.dto.CourseStopResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.service.CourseService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CourseController.class)
@DisplayName("CourseController 슬라이스 테스트")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CourseService courseService;

    @Test
    @DisplayName("POST 유효 -> 201")
    void create_valid() throws Exception {
        CourseCreateRequest req = new CourseCreateRequest("힐링 코스", "desc", TourTheme.HEALING,
                List.of(new CourseStopRequest(10L, 1)));
        when(courseService.create(any())).thenReturn(new CourseResponse(1L, "힐링 코스", "desc", TourTheme.HEALING,
                List.of(new CourseStopResponse(1, 10L, "초곡"))));

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stops[0].attractionName").value("초곡"));
    }

    @Test
    @DisplayName("POST 방문지 없음(빈 stops) -> 400")
    void create_emptyStops() throws Exception {
        CourseCreateRequest req = new CourseCreateRequest("코스", "desc", TourTheme.ECO, List.of());

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 없음 -> 404")
    void getById_notFound() throws Exception {
        when(courseService.getById(99L)).thenThrow(new ResourceNotFoundException("없음"));
        mockMvc.perform(get("/api/courses/99")).andExpect(status().isNotFound());
    }
}

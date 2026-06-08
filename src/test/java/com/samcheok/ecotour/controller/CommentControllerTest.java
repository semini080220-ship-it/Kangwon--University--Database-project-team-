package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.dto.CommentCreateRequest;
import com.samcheok.ecotour.dto.CommentResponse;
import com.samcheok.ecotour.service.CommentService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CommentController.class)
@DisplayName("CommentController 슬라이스 테스트")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("POST 유효 -> 201, 작성자·내용 반환")
    void create_valid() throws Exception {
        when(commentService.create(eq(10L), any())).thenReturn(new CommentResponse(
                1L, 10L, "영희", "노을이 예술이에요", LocalDateTime.now()));

        mockMvc.perform(post("/api/attractions/10/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("영희", "노을이 예술이에요"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("영희"))
                .andExpect(jsonPath("$.content").value("노을이 예술이에요"));
    }

    @Test
    @DisplayName("POST 작성자 공백 -> 400")
    void create_blankAuthor() throws Exception {
        mockMvc.perform(post("/api/attractions/10/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("  ", "내용 있음"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 내용 공백 -> 400")
    void create_blankContent() throws Exception {
        mockMvc.perform(post("/api/attractions/10/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("영희", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 작성자 50자 초과 -> 400")
    void create_authorTooLong() throws Exception {
        String longAuthor = "가".repeat(51);
        mockMvc.perform(post("/api/attractions/10/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest(longAuthor, "내용"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 내용 1000자 초과 -> 400")
    void create_contentTooLong() throws Exception {
        String longContent = "x".repeat(1001);
        mockMvc.perform(post("/api/attractions/10/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("작성자", longContent))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 관광지 댓글 목록 -> 200")
    void list() throws Exception {
        when(commentService.getByAttraction(10L)).thenReturn(List.of(
                new CommentResponse(1L, 10L, "영희", "좋아요", LocalDateTime.now())));

        mockMvc.perform(get("/api/attractions/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("영희"));
    }
}

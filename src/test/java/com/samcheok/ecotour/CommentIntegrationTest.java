package com.samcheok.ecotour;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.Comment;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.dto.CommentCreateRequest;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.CommentRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글 기능 풀스택 통합 테스트 — 컨트롤러→서비스→JPA→H2 전 계층을 실제로 관통한다.
 * "댓글이 DB에 실제로 추가된다"를 저장소 재조회로 직접 증명한다.
 * 테스트 프로파일은 시드(data.sql)를 끄므로, 검증할 관광지를 테스트가 직접 만든다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("댓글 기능 통합 테스트")
class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AttractionRepository attractionRepository;

    private Attraction newAttraction() {
        return attractionRepository.save(new Attraction("초곡용굴촛대바위길", "한적한 해안 산책로",
                Category.COAST, 37.36, 129.26, "강원 삼척시 근덕면", CongestionLevel.LOW, true));
    }

    @Test
    @DisplayName("댓글을 작성하면 DB에 저장되고 목록 조회로 다시 읽힌다")
    void postThenPersistedAndReadBack() throws Exception {
        Long attractionId = newAttraction().getId();

        mockMvc.perform(post("/api/attractions/" + attractionId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CommentCreateRequest("통합테스트", "E2E로 DB 저장을 검증합니다"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.attractionId").value(attractionId))
                .andExpect(jsonPath("$.author").value("통합테스트"));

        // 1) 저장소로 직접 재조회 — DB에 실제로 들어갔는지 증명
        List<Comment> persisted = commentRepository.findByAttractionIdOrderByCreatedAtDesc(attractionId);
        assertThat(persisted).extracting(Comment::getAuthor).contains("통합테스트");
        assertThat(persisted).extracting(Comment::getContent).contains("E2E로 DB 저장을 검증합니다");

        // 2) GET API로도 같은 댓글이 최신순 맨 앞에 읽힌다
        mockMvc.perform(get("/api/attractions/" + attractionId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("통합테스트"));
    }

    @Test
    @DisplayName("관광지별 댓글은 서로 격리된다 — A 목록에 B의 댓글이 섞이지 않는다")
    void commentsIsolatedByAttraction() throws Exception {
        Long a = newAttraction().getId();
        Long b = attractionRepository.save(new Attraction("죽서루", "관동팔경 누각",
                Category.HISTORY, 37.44, 129.16, "강원 삼척시 죽서루길", CongestionLevel.MEDIUM, false)).getId();

        mockMvc.perform(post("/api/attractions/" + a + "/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("사용자A", "A 관광지 댓글"))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/attractions/" + b + "/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest("사용자B", "B 관광지 댓글"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/attractions/" + a + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("A 관광지 댓글"));
        mockMvc.perform(get("/api/attractions/" + b + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("B 관광지 댓글"));
    }

    @Test
    @DisplayName("없는 관광지에 댓글을 작성하면 404")
    void postToMissingAttraction_404() throws Exception {
        mockMvc.perform(post("/api/attractions/999999/comments").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CommentCreateRequest("아무개", "없는 곳"))))
                .andExpect(status().isNotFound());
    }
}

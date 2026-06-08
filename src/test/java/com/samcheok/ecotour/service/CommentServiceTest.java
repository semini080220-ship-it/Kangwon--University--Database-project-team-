package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.Comment;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.dto.CommentCreateRequest;
import com.samcheok.ecotour.dto.CommentResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.CommentRepository;
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
@DisplayName("CommentService 단위 테스트")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AttractionRepository attractionRepository;
    @InjectMocks
    private CommentService commentService;

    private Attraction attraction(Long id, String name) {
        Attraction a = new Attraction(name, "d", Category.COAST, 37.0, 129.0, "삼척", CongestionLevel.LOW, true);
        ReflectionTestUtils.setField(a, "id", id);
        return a;
    }

    @Test
    @DisplayName("댓글 작성 성공 — 닉네임·내용·관광지ID가 응답에 담긴다")
    void create_success() {
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 1L);
            return c;
        });

        CommentResponse res = commentService.create(10L, new CommentCreateRequest("영희", "노을이 예술이에요"));

        assertThat(res.id()).isEqualTo(1L);
        assertThat(res.author()).isEqualTo("영희");
        assertThat(res.content()).isEqualTo("노을이 예술이에요");
        assertThat(res.attractionId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("작성자·내용의 앞뒤 공백은 제거하고 저장한다")
    void create_trimsWhitespace() {
        when(attractionRepository.findById(10L)).thenReturn(Optional.of(attraction(10L, "초곡")));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> {
            Comment c = inv.getArgument(0);
            ReflectionTestUtils.setField(c, "id", 1L);
            return c;
        });

        CommentResponse res = commentService.create(10L, new CommentCreateRequest("  영희  ", "  노을이 예술  "));

        assertThat(res.author()).isEqualTo("영희");
        assertThat(res.content()).isEqualTo("노을이 예술");
    }

    @Test
    @DisplayName("없는 관광지에 작성하면 ResourceNotFoundException")
    void create_attractionNotFound() {
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(99L, new CommentCreateRequest("영희", "x")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("관광지별 댓글을 조회한다")
    void getByAttraction() {
        Comment c = new Comment(attraction(10L, "초곡"), "영희", "좋아요");
        ReflectionTestUtils.setField(c, "id", 1L);
        when(commentRepository.findByAttractionIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(c));

        assertThat(commentService.getByAttraction(10L)).hasSize(1)
                .extracting(CommentResponse::author).containsExactly("영희");
    }
}

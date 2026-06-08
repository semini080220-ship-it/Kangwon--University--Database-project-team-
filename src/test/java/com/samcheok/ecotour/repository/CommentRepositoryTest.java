package com.samcheok.ecotour.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.Comment;
import com.samcheok.ecotour.domain.CongestionLevel;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@DisplayName("CommentRepository 슬라이스 테스트")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AttractionRepository attractionRepository;

    private Attraction choco;
    private Attraction jukseoru;

    @BeforeEach
    void setUp() {
        choco = attractionRepository.save(new Attraction("초곡용굴촛대바위길", "산책로",
                Category.COAST, 37.36, 129.26, "근덕면", CongestionLevel.LOW, true));
        jukseoru = attractionRepository.save(new Attraction("죽서루", "역사",
                Category.HISTORY, 37.44, 129.16, "삼척", CongestionLevel.MEDIUM, false));
    }

    private Comment commentAt(Attraction attraction, String author, String content, LocalDateTime when) {
        Comment c = new Comment(attraction, author, content);
        ReflectionTestUtils.setField(c, "createdAt", when);
        return c;
    }

    @Test
    @DisplayName("관광지별 댓글을 최신순(작성시각 내림차순)으로 조회한다")
    void findByAttraction_newestFirst() {
        commentRepository.save(commentAt(choco, "철수", "한적해서 좋아요",
                LocalDateTime.of(2026, 1, 1, 10, 0)));
        commentRepository.save(commentAt(choco, "영희", "노을이 예술이에요",
                LocalDateTime.of(2026, 1, 2, 10, 0)));

        List<Comment> result = commentRepository.findByAttractionIdOrderByCreatedAtDesc(choco.getId());

        assertThat(result).extracting(Comment::getAuthor)
                .containsExactly("영희", "철수");
    }

    @Test
    @DisplayName("다른 관광지의 댓글은 제외하고 해당 관광지의 댓글만 조회한다")
    void findByAttraction_onlyMatching() {
        commentRepository.save(commentAt(choco, "철수", "초곡 댓글",
                LocalDateTime.of(2026, 1, 1, 10, 0)));
        commentRepository.save(commentAt(jukseoru, "민수", "죽서루 댓글",
                LocalDateTime.of(2026, 1, 1, 11, 0)));

        List<Comment> result = commentRepository.findByAttractionIdOrderByCreatedAtDesc(choco.getId());

        assertThat(result).extracting(Comment::getContent)
                .containsExactly("초곡 댓글");
    }

    @Test
    @DisplayName("댓글이 없는 관광지는 빈 목록을 반환한다")
    void findByAttraction_empty() {
        assertThat(commentRepository.findByAttractionIdOrderByCreatedAtDesc(jukseoru.getId()))
                .isEmpty();
    }
}

package com.samcheok.ecotour.repository;

import com.samcheok.ecotour.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /** 관광지별 댓글을 최신순(작성시각 내림차순)으로 조회. */
    List<Comment> findByAttractionIdOrderByCreatedAtDesc(Long attractionId);
}

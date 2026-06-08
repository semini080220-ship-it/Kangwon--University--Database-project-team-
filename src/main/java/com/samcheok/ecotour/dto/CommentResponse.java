package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.Comment;
import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO.
 */
public record CommentResponse(
        Long id,
        Long attractionId,
        String author,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getAttraction().getId(),
                c.getAuthor(),
                c.getContent(),
                c.getCreatedAt()
        );
    }
}

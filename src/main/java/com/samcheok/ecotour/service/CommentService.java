package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Comment;
import com.samcheok.ecotour.dto.CommentCreateRequest;
import com.samcheok.ecotour.dto.CommentResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import com.samcheok.ecotour.repository.CommentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글 비즈니스 로직. 로그인 없이 관광지에 닉네임·내용으로 댓글을 남긴다.
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final AttractionRepository attractionRepository;

    public CommentService(CommentRepository commentRepository,
                          AttractionRepository attractionRepository) {
        this.commentRepository = commentRepository;
        this.attractionRepository = attractionRepository;
    }

    @Transactional
    public CommentResponse create(Long attractionId, CommentCreateRequest request) {
        Attraction attraction = attractionRepository.findById(attractionId)
                .orElseThrow(() -> ResourceNotFoundException.of("관광지", attractionId));
        Comment saved = commentRepository.save(
                new Comment(attraction, request.author().trim(), request.content().trim()));
        return CommentResponse.from(saved);
    }

    public List<CommentResponse> getByAttraction(Long attractionId) {
        return commentRepository.findByAttractionIdOrderByCreatedAtDesc(attractionId).stream()
                .map(CommentResponse::from)
                .toList();
    }
}

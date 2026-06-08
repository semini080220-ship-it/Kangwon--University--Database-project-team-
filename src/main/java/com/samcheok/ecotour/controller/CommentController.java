package com.samcheok.ecotour.controller;

import com.samcheok.ecotour.dto.CommentCreateRequest;
import com.samcheok.ecotour.dto.CommentResponse;
import com.samcheok.ecotour.service.CommentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 댓글 REST API. 각 관광지에 종속된 하위 리소스(/api/attractions/{id}/comments).
 */
@RestController
@RequestMapping("/api/attractions/{attractionId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 댓글 작성. */
    @PostMapping
    public ResponseEntity<CommentResponse> create(@PathVariable Long attractionId,
                                                  @Valid @RequestBody CommentCreateRequest request) {
        CommentResponse created = commentService.create(attractionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 관광지별 댓글 목록 (최신순). */
    @GetMapping
    public List<CommentResponse> list(@PathVariable Long attractionId) {
        return commentService.getByAttraction(attractionId);
    }
}

package com.samcheok.ecotour.controller;

import com.samcheok.ecotour.dto.StampCountResponse;
import com.samcheok.ecotour.dto.VisitLogCreateRequest;
import com.samcheok.ecotour.dto.VisitLogResponse;
import com.samcheok.ecotour.service.VisitLogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 방문/인증 REST API.
 */
@RestController
@RequestMapping("/api/visits")
public class VisitLogController {

    private final VisitLogService visitLogService;

    public VisitLogController(VisitLogService visitLogService) {
        this.visitLogService = visitLogService;
    }

    @PostMapping
    public ResponseEntity<VisitLogResponse> create(@Valid @RequestBody VisitLogCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitLogService.create(request));
    }

    @GetMapping
    public List<VisitLogResponse> listByUser(@RequestParam Long userId) {
        return visitLogService.getByUser(userId);
    }

    @GetMapping("/stamp-count")
    public StampCountResponse stampCount(@RequestParam Long userId) {
        return new StampCountResponse(userId, visitLogService.countStamps(userId));
    }
}

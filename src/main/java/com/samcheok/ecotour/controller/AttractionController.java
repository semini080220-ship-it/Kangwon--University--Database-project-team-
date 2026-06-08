package com.samcheok.ecotour.controller;

import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.dto.AttractionCreateRequest;
import com.samcheok.ecotour.dto.AttractionResponse;
import com.samcheok.ecotour.dto.CongestionUpdateRequest;
import com.samcheok.ecotour.service.AttractionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관광지 REST API (프레젠테이션 계층).
 */
@RestController
@RequestMapping("/api/attractions")
public class AttractionController {

    private final AttractionService attractionService;

    public AttractionController(AttractionService attractionService) {
        this.attractionService = attractionService;
    }

    /** 관광지 등록. */
    @PostMapping
    public ResponseEntity<AttractionResponse> create(@Valid @RequestBody AttractionCreateRequest request) {
        AttractionResponse created = attractionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 관광지 단건 조회. */
    @GetMapping("/{id}")
    public AttractionResponse getById(@PathVariable Long id) {
        return attractionService.getById(id);
    }

    /** 전체 조회 또는 카테고리별 조회(?category=COAST). */
    @GetMapping
    public List<AttractionResponse> list(@RequestParam(required = false) Category category) {
        return (category != null)
                ? attractionService.getByCategory(category)
                : attractionService.getAll();
    }

    /** 실시간 혼잡도 갱신. */
    @PatchMapping("/{id}/congestion")
    public AttractionResponse updateCongestion(@PathVariable Long id,
                                               @Valid @RequestBody CongestionUpdateRequest request) {
        return attractionService.updateCongestion(id, request.congestionLevel());
    }

    /** 분산 관광 대안 추천. */
    @GetMapping("/{id}/alternatives")
    public List<AttractionResponse> alternatives(@PathVariable Long id) {
        return attractionService.recommendAlternatives(id);
    }
}

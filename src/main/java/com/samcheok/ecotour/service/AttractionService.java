package com.samcheok.ecotour.service;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.dto.AttractionCreateRequest;
import com.samcheok.ecotour.dto.AttractionResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관광지 비즈니스 로직 (서비스 계층).
 * 핵심: 분산 관광 추천 — 붐비는 명소 대신 한적한/숨겨진 로컬 명소로 관광객을 유도한다.
 */
@Service
@Transactional(readOnly = true)
public class AttractionService {

    /** 추천 최대 개수. */
    private static final int MAX_ALTERNATIVES = 3;

    private final AttractionRepository attractionRepository;

    public AttractionService(AttractionRepository attractionRepository) {
        this.attractionRepository = attractionRepository;
    }

    @Transactional
    public AttractionResponse create(AttractionCreateRequest request) {
        Attraction attraction = new Attraction(
                request.name(),
                request.description(),
                request.category(),
                request.latitude(),
                request.longitude(),
                request.address(),
                request.congestionLevel() != null ? request.congestionLevel() : CongestionLevel.LOW,
                request.localGem()
        );
        return AttractionResponse.from(attractionRepository.save(attraction));
    }

    public AttractionResponse getById(Long id) {
        return AttractionResponse.from(findOrThrow(id));
    }

    public List<AttractionResponse> getAll() {
        return attractionRepository.findAll().stream()
                .map(AttractionResponse::from)
                .toList();
    }

    public List<AttractionResponse> getByCategory(Category category) {
        return attractionRepository.findByCategory(category).stream()
                .map(AttractionResponse::from)
                .toList();
    }

    @Transactional
    public AttractionResponse updateCongestion(Long id, CongestionLevel level) {
        Attraction attraction = findOrThrow(id);
        attraction.updateCongestion(level); // 변경 감지(dirty checking)로 자동 반영
        return AttractionResponse.from(attraction);
    }

    /**
     * 분산 관광 추천.
     * <p>같은 카테고리의 다른 관광지 중에서,
     * (1) 기준 관광지보다 덜 붐비거나 (2) 숨겨진 로컬 명소인 곳을 골라
     * 로컬 명소 우선 → 혼잡도 낮은 순 → 이름 순으로 정렬해 최대 {@value #MAX_ALTERNATIVES}곳을 추천한다.
     */
    public List<AttractionResponse> recommendAlternatives(Long attractionId) {
        Attraction base = findOrThrow(attractionId);

        return attractionRepository.findByCategoryAndIdNot(base.getCategory(), base.getId()).stream()
                .filter(candidate ->
                        candidate.getCongestionLevel().ordinal() < base.getCongestionLevel().ordinal()
                                || candidate.isLocalGem())
                .sorted(Comparator.comparing(Attraction::isLocalGem).reversed()
                        .thenComparingInt(a -> a.getCongestionLevel().ordinal())
                        .thenComparing(Attraction::getName))
                .limit(MAX_ALTERNATIVES)
                .map(AttractionResponse::from)
                .toList();
    }

    private Attraction findOrThrow(Long id) {
        return attractionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("관광지", id));
    }
}

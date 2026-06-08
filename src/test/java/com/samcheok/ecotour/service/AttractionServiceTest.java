package com.samcheok.ecotour.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import com.samcheok.ecotour.dto.AttractionCreateRequest;
import com.samcheok.ecotour.dto.AttractionResponse;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.repository.AttractionRepository;
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
@DisplayName("AttractionService 단위 테스트")
class AttractionServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @InjectMocks
    private AttractionService attractionService;

    private Attraction attraction(Long id, String name, Category c, CongestionLevel cl, boolean gem) {
        Attraction a = new Attraction(name, "desc", c, 37.0, 129.0, "삼척", cl, gem);
        ReflectionTestUtils.setField(a, "id", id);
        return a;
    }

    @Test
    @DisplayName("관광지를 등록하면 저장 후 응답 DTO를 반환한다")
    void create() {
        AttractionCreateRequest req = new AttractionCreateRequest(
                "초곡", "산책로", Category.COAST, 37.36, 129.26, "근덕면", CongestionLevel.LOW, true);
        when(attractionRepository.save(any(Attraction.class))).thenAnswer(inv -> {
            Attraction saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 10L);
            return saved;
        });

        AttractionResponse res = attractionService.create(req);

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.name()).isEqualTo("초곡");
        assertThat(res.localGem()).isTrue();
        verify(attractionRepository).save(any(Attraction.class));
    }

    @Test
    @DisplayName("존재하는 관광지를 ID로 조회한다")
    void getById_found() {
        when(attractionRepository.findById(1L))
                .thenReturn(Optional.of(attraction(1L, "쏠비치", Category.COAST, CongestionLevel.HIGH, false)));
        assertThat(attractionService.getById(1L).name()).isEqualTo("쏠비치");
    }

    @Test
    @DisplayName("없는 관광지를 조회하면 ResourceNotFoundException")
    void getById_missing() {
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> attractionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("혼잡도를 갱신한다")
    void updateCongestion() {
        Attraction a = attraction(1L, "쏠비치", Category.COAST, CongestionLevel.HIGH, false);
        when(attractionRepository.findById(1L)).thenReturn(Optional.of(a));

        AttractionResponse res = attractionService.updateCongestion(1L, CongestionLevel.LOW);

        assertThat(res.congestionLevel()).isEqualTo(CongestionLevel.LOW);
        assertThat(a.getCongestionLevel()).isEqualTo(CongestionLevel.LOW);
    }

    @Test
    @DisplayName("분산 관광 추천: 붐비는 비(非)로컬명소·자기 자신은 제외하고 한적한 로컬 명소를 추천한다")
    void recommendAlternatives() {
        Attraction base = attraction(1L, "쏠비치", Category.COAST, CongestionLevel.HIGH, false);
        Attraction busyNonGem = attraction(2L, "장호항", Category.COAST, CongestionLevel.HIGH, false);
        Attraction gem1 = attraction(3L, "초곡", Category.COAST, CongestionLevel.LOW, true);
        Attraction gem2 = attraction(4L, "덕봉산", Category.COAST, CongestionLevel.LOW, true);
        when(attractionRepository.findById(1L)).thenReturn(Optional.of(base));
        when(attractionRepository.findByCategoryAndIdNot(Category.COAST, 1L))
                .thenReturn(List.of(busyNonGem, gem1, gem2));

        List<AttractionResponse> result = attractionService.recommendAlternatives(1L);

        assertThat(result).extracting(AttractionResponse::name)
                .containsExactlyInAnyOrder("초곡", "덕봉산");
        assertThat(result).extracting(AttractionResponse::name)
                .doesNotContain("장호항", "쏠비치");
    }

    @Test
    @DisplayName("추천 대상 관광지가 없으면 ResourceNotFoundException")
    void recommendAlternatives_baseMissing() {
        when(attractionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> attractionService.recommendAlternatives(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

package com.samcheok.ecotour.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("AttractionRepository 슬라이스 테스트")
class AttractionRepositoryTest {

    @Autowired
    private AttractionRepository repository;

    private Attraction crowded;

    @BeforeEach
    void setUp() {
        crowded = repository.save(new Attraction("쏠비치", "리조트", Category.COAST,
                37.46, 129.17, "삼척", CongestionLevel.HIGH, false));
        repository.save(new Attraction("초곡", "산책로", Category.COAST,
                37.36, 129.26, "근덕면", CongestionLevel.LOW, true));
        repository.save(new Attraction("죽서루", "역사", Category.HISTORY,
                37.44, 129.16, "삼척", CongestionLevel.MEDIUM, false));
    }

    @Test
    @DisplayName("카테고리로 조회하면 해당 카테고리만 반환한다")
    void findByCategory() {
        List<Attraction> coast = repository.findByCategory(Category.COAST);
        assertThat(coast).extracting(Attraction::getName)
                .containsExactlyInAnyOrder("쏠비치", "초곡");
    }

    @Test
    @DisplayName("같은 카테고리에서 자기 자신을 제외하고 조회한다")
    void findByCategoryAndIdNot() {
        List<Attraction> others = repository.findByCategoryAndIdNot(Category.COAST, crowded.getId());
        assertThat(others).extracting(Attraction::getName).containsExactly("초곡");
    }

    @Test
    @DisplayName("혼잡도로 조회한다")
    void findByCongestionLevel() {
        assertThat(repository.findByCongestionLevel(CongestionLevel.HIGH))
                .extracting(Attraction::getName).containsExactly("쏠비치");
    }

    @Test
    @DisplayName("숨겨진 로컬 명소만 조회한다")
    void findByLocalGemTrue() {
        assertThat(repository.findByLocalGemTrue())
                .extracting(Attraction::getName).containsExactly("초곡");
    }
}

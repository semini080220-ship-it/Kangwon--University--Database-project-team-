package com.samcheok.ecotour.repository;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    List<Attraction> findByCategory(Category category);

    List<Attraction> findByCongestionLevel(CongestionLevel congestionLevel);

    /** 분산 관광 추천: 같은 카테고리의 다른 관광지(자기 자신 제외) 후보. */
    List<Attraction> findByCategoryAndIdNot(Category category, Long id);

    /** 숨겨진 로컬 명소 목록. */
    List<Attraction> findByLocalGemTrue();
}

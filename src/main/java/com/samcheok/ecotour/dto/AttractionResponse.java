package com.samcheok.ecotour.dto;

import com.samcheok.ecotour.domain.Attraction;
import com.samcheok.ecotour.domain.Category;
import com.samcheok.ecotour.domain.CongestionLevel;

/**
 * 관광지 응답 DTO. 엔티티를 외부로 직접 노출하지 않기 위한 변환 계층.
 */
public record AttractionResponse(
        Long id,
        String name,
        String description,
        Category category,
        Double latitude,
        Double longitude,
        String address,
        CongestionLevel congestionLevel,
        boolean localGem
) {
    public static AttractionResponse from(Attraction a) {
        return new AttractionResponse(
                a.getId(),
                a.getName(),
                a.getDescription(),
                a.getCategory(),
                a.getLatitude(),
                a.getLongitude(),
                a.getAddress(),
                a.getCongestionLevel(),
                a.isLocalGem()
        );
    }
}

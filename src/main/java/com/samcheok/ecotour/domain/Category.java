package com.samcheok.ecotour.domain;

/**
 * 관광지 카테고리. 삼척의 자연/문화 자원을 분류하여 분산 관광 추천에 활용한다.
 */
public enum Category {
    COAST,        // 해안 (예: 장호항, 쏠비치)
    MOUNTAIN,     // 산간/계곡 (예: 무건리 이끼폭포)
    HISTORY,      // 역사 (예: 죽서루)
    MINE_CULTURE, // 폐광지 문화공간 (예: 도계 폐광촌)
    CAVE,         // 동굴 (예: 환선굴)
    OTHER
}

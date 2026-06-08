package com.samcheok.ecotour.domain;

/**
 * 실시간 혼잡도. 분산 관광 로직(붐비는 곳 대신 한산한 대안 추천)의 핵심 기준.
 */
public enum CongestionLevel {
    LOW,
    MEDIUM,
    HIGH
}

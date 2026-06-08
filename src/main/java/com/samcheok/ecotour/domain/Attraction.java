package com.samcheok.ecotour.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 관광지(Attraction). 삼척의 명소 정보와 실시간 혼잡도를 관리한다.
 */
@Entity
@Table(name = "attraction")
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    private Double latitude;

    private Double longitude;

    @Column(length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CongestionLevel congestionLevel = CongestionLevel.LOW;

    /** 숨겨진 로컬 명소 여부 — 분산 관광 유도 추천의 우선 대상. */
    @Column(nullable = false)
    private boolean localGem = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Attraction() {
    }

    public Attraction(String name, String description, Category category,
                      Double latitude, Double longitude, String address,
                      CongestionLevel congestionLevel, boolean localGem) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.congestionLevel = (congestionLevel != null) ? congestionLevel : CongestionLevel.LOW;
        this.localGem = localGem;
        this.createdAt = LocalDateTime.now();
    }

    /** 실시간 혼잡도 갱신 (도메인 로직). */
    public void updateCongestion(CongestionLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("혼잡도는 null일 수 없습니다.");
        }
        this.congestionLevel = level;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CongestionLevel getCongestionLevel() {
        return congestionLevel;
    }

    public boolean isLocalGem() {
        return localGem;
    }

    public void setLocalGem(boolean localGem) {
        this.localGem = localGem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

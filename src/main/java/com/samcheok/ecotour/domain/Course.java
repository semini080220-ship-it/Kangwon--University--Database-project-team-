package com.samcheok.ecotour.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * 스탬프 코스(Course). 친환경/체험형 분산 관광을 유도하는 테마별 추천 경로.
 */
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TourTheme theme;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("visitOrder ASC")
    private List<CourseDetail> details = new ArrayList<>();

    protected Course() {
    }

    public Course(String name, String description, TourTheme theme) {
        this.name = name;
        this.description = description;
        this.theme = theme;
    }

    /** 코스에 방문지를 순서대로 추가하는 연관관계 편의 메서드. */
    public CourseDetail addAttraction(Attraction attraction, int visitOrder) {
        CourseDetail detail = new CourseDetail(this, attraction, visitOrder);
        this.details.add(detail);
        return detail;
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

    public TourTheme getTheme() {
        return theme;
    }

    public void setTheme(TourTheme theme) {
        this.theme = theme;
    }

    public List<CourseDetail> getDetails() {
        return details;
    }
}

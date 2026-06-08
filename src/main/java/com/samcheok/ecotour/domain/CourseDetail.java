package com.samcheok.ecotour.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 스탬프 코스 상세(Course_Detail). 코스 내 방문지와 방문 순서를 지정한다.
 * (course_id, visit_order) 조합은 유일 — 한 코스에서 순서 중복 방지.
 */
@Entity
@Table(name = "course_detail",
        uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "visit_order"}))
public class CourseDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    @Column(name = "visit_order", nullable = false)
    private int visitOrder;

    protected CourseDetail() {
    }

    public CourseDetail(Course course, Attraction attraction, int visitOrder) {
        this.course = course;
        this.attraction = attraction;
        this.visitOrder = visitOrder;
    }

    public Long getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public int getVisitOrder() {
        return visitOrder;
    }

    public void setVisitOrder(int visitOrder) {
        this.visitOrder = visitOrder;
    }
}

package com.samcheok.ecotour.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * 방문 및 인증 기록(Visit_Log). 스탬프 인증 또는 친환경 활동 인증 이력.
 */
@Entity
@Table(name = "visit_log")
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VisitType visitType;

    @Column(length = 500)
    private String note;

    @Column(nullable = false)
    private LocalDateTime visitedAt = LocalDateTime.now();

    protected VisitLog() {
    }

    public VisitLog(User user, Attraction attraction, VisitType visitType, String note) {
        this.user = user;
        this.attraction = attraction;
        this.visitType = visitType;
        this.note = note;
        this.visitedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public VisitType getVisitType() {
        return visitType;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getVisitedAt() {
        return visitedAt;
    }
}

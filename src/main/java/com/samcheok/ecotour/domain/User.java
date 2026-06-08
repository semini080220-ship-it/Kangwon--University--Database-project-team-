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
 * 사용자(관광객). 회원 정보와 선호 테마를 관리한다.
 * 획득 스탬프 수는 VisitLog 로부터 파생 계산한다(중복 저장 방지·정규화).
 */
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TourTheme preferredTheme;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected User() {
    }

    public User(String email, String nickname, TourTheme preferredTheme) {
        this.email = email;
        this.nickname = nickname;
        this.preferredTheme = preferredTheme;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public TourTheme getPreferredTheme() {
        return preferredTheme;
    }

    public void setPreferredTheme(TourTheme preferredTheme) {
        this.preferredTheme = preferredTheme;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

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
import java.time.LocalDateTime;

/**
 * 댓글(Comment). 각 관광지에 로그인 없이 남기는 가벼운 방문 후기.
 * 회원/평점이 필수인 {@link Review} 와 달리, 닉네임(author)과 내용만으로 작성된다.
 */
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attraction_id", nullable = false)
    private Attraction attraction;

    /** 작성자 닉네임 (자유 입력, 로그인 불필요). */
    @Column(nullable = false, length = 50)
    private String author;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Comment() {
    }

    public Comment(Attraction attraction, String author, String content) {
        this.attraction = attraction;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

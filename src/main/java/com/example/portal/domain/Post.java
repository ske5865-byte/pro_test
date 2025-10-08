package com.example.portal.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    protected Post() {}

    public Post(String title, String content, Member author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Member getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters for updates
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

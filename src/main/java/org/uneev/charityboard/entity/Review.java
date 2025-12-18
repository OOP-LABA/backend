package org.uneev.charityboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(
        name = "reviews",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "post_id"})
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private UserProfile reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private UserProfile reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
    }
}


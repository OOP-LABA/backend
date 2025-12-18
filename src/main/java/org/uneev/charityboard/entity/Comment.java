package org.uneev.charityboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserProfile author;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "created_at")
    private Date createdAt;

    public Comment() {
        createdAt = new Date();
    }
}

package org.uneev.charityboard.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private UserProfile executor;

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<Image> images;

    @Column(name = "avatar_url")
    private URL avatar;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "avatar_data", columnDefinition = "bytea")
    private byte[] avatarData;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @Column(name = "avatar_filename")
    private String avatarFilename;

    @Column(name = "goal")
    private Long goal;

    @Column(name = "raised", nullable = false)
    private Long raised;

    @Column(name = "account_details")
    private String accountDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @Column(name = "deposit_amount", nullable = false)
    private Long depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_status", nullable = false)
    private DepositStatus depositStatus;

    public Post() {
        this.createdAt = new Date();
        this.raised = 0L;
        this.status = PostStatus.OPEN;
        this.depositAmount = 0L;
        this.depositStatus = DepositStatus.NONE;
    }
}

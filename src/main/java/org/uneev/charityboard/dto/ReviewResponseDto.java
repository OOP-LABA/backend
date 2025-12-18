package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewResponseDto {
    private long id;
    private int rating;
    private String content;
    private Date createdAt;

    private long postId;

    private String reviewerUsername;
    private String reviewerFirstName;
    private String reviewerSecondName;
}


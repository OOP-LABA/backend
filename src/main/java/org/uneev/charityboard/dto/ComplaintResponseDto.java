package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ComplaintResponseDto {
    private long id;
    private String reason;
    private String status;
    private Date createdAt;

    private String reporterUsername;
    private String targetUsername;
    private Long targetPostId;

    private String adminNote;
    private Date resolvedAt;
    private String resolvedByUsername;
}


package org.uneev.charityboard.dto;

import lombok.Data;

@Data
public class ComplaintCreationDto {
    private String reason;
    private String targetUsername;
    private Long targetPostId;
}


package org.uneev.charityboard.dto;

import lombok.Data;
import org.uneev.charityboard.entity.ComplaintStatus;

@Data
public class ComplaintResolveDto {
    private ComplaintStatus status;
    private String adminNote;
    private boolean banUser;
    private String banReason;
}


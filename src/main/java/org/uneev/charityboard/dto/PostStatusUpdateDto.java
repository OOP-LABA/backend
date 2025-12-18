package org.uneev.charityboard.dto;

import lombok.Data;
import org.uneev.charityboard.entity.PostStatus;

@Data
public class PostStatusUpdateDto {
    private PostStatus status;
}


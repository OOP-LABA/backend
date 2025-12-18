package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentResponseDto {

    private long id;
    private String content;
    private String username;
    private String firstName;
    private String secondName;
    private Date createdAt;
}

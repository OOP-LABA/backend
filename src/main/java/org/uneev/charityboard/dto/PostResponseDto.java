package org.uneev.charityboard.dto;

import lombok.Data;

import java.net.URL;
import java.util.Date;
import java.util.List;

@Data
public class PostResponseDto {

    private long id;
    private String title;
    private String content;
    private String category;
    private Date createdAt;
    private List<CommentResponseDto> comments;
    private URL avatar;
    private List<URL> images;
    private long goal;
    private long raised;
    private String accountDetails;

    private String authorUsername;
    private String authorFirstName;
    private String authorSecondName;
    private String authorCity;

    private String status;
    private String executorUsername;
    private String executorFirstName;
    private String executorSecondName;
    private long depositAmount;
    private String depositStatus;
}

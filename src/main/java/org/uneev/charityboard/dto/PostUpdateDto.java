package org.uneev.charityboard.dto;

import lombok.Data;

import java.net.URL;

@Data
public class PostUpdateDto {
    private String title;
    private String content;
    private String category;
    private URL avatar;
    private Long goal;
    private String accountDetails;
}


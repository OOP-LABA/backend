package org.uneev.charityboard.dto;

import lombok.Data;

import java.net.URL;

@Data
public class PostCreationDto {

    private String title;

    private String content;

    private String category;

    private URL avatar;

    private long goal;

    private String accountDetails;
}

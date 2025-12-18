package org.uneev.charityboard.dto;

import lombok.Data;

@Data
public class UserProfileResponseDto {
    private String username;
    private String firstName;
    private String secondName;
    private String city;

    private String headline;
    private String about;
    private String skills;
    private String portfolio;
    private String contacts;

    private Double ratingAverage;
    private long ratingCount;
}


package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseDto {

    private String username;

    private String email;

    private String firstName;

    private String secondName;

    private String city;

//    private List<PostDto> posts;
}

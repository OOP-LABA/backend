package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AdminUserResponseDto {
    private long id;
    private String username;
    private String email;
    private boolean banned;
    private String banReason;
    private Date bannedAt;
    private List<String> roles;

    private String firstName;
    private String secondName;
    private String city;
}


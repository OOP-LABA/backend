package org.uneev.charityboard.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {

    private String username;
    private String password;
    private String email;

    private String firstName;
    private String secondName;
    private String city;
}

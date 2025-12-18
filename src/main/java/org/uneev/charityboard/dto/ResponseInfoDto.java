package org.uneev.charityboard.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ResponseInfoDto {
    private int status;
    private String message;
    private Date timestamp;

    public ResponseInfoDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}

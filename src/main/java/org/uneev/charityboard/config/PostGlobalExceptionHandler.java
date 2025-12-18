package org.uneev.charityboard.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.exception.NoSuchPostException;

@RestControllerAdvice
public class PostGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(NoSuchPostException exception) {
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.NOT_FOUND.value(), exception.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}

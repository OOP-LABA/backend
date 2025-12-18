package org.uneev.charityboard.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.exception.ForbiddenActionException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(ForbiddenActionException exception) {
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.FORBIDDEN.value(), exception.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(NoSuchElementException exception) {
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.NOT_FOUND.value(), "Resource not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(DataIntegrityViolationException exception) {
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.BAD_REQUEST.value(), "Invalid request"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(IllegalArgumentException exception) {
        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.BAD_REQUEST.value(), exception.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}

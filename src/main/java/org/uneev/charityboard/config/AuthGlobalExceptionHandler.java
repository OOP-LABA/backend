package org.uneev.charityboard.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.exception.UsernameAlreadyExistException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class AuthGlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(BadCredentialsException exception) {

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.BAD_REQUEST.value(), "Invalid username or password"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(ExpiredJwtException exception) {

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.UNAUTHORIZED.value(), "Token has been expired"),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(SignatureException exception) {

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.UNAUTHORIZED.value(), "Invalid token signature"),
                HttpStatus.UNAUTHORIZED
        );
    }

//    @ExceptionHandler
//    public ResponseEntity<ResponseInfoDto> handleException(NoSuchElementException exception) {
//
//        return new ResponseEntity<>(
//                new ResponseInfoDto(HttpStatus.UNAUTHORIZED.value(), "Invalid token"),
//                HttpStatus.UNAUTHORIZED
//        );
//    }

    @ExceptionHandler
    public ResponseEntity<ResponseInfoDto> handleException(UsernameAlreadyExistException exception) {

        return new ResponseEntity<>(
                new ResponseInfoDto(HttpStatus.BAD_REQUEST.value(),  exception.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}

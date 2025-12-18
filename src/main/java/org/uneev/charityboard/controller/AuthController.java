package org.uneev.charityboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uneev.charityboard.dto.JwtResponseDto;
import org.uneev.charityboard.dto.ResponseInfoDto;
import org.uneev.charityboard.dto.UserLoginDto;
import org.uneev.charityboard.dto.UserRegistrationDto;
import org.uneev.charityboard.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDto userLoginDto) {
        String token = authService.createToken(userLoginDto.getUsername(), userLoginDto.getPassword());

        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDto userRegistrationDto) {
        authService.createUser(userRegistrationDto);

        String token = authService.createToken(
                userRegistrationDto.getUsername(),
                userRegistrationDto.getPassword()
        );

        return new ResponseEntity<>(
                new JwtResponseDto(token),
                HttpStatus.CREATED
        );
    }
}

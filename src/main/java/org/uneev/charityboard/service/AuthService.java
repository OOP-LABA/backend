package org.uneev.charityboard.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.uneev.charityboard.dto.UserRegistrationDto;
import org.uneev.charityboard.exception.ForbiddenActionException;
import org.uneev.charityboard.exception.UsernameAlreadyExistException;
import org.uneev.charityboard.utils.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public String createToken(String username, String password) {
        userService.getByUsername(username).ifPresent(user -> {
            if (user.isBanned()) {
                throw new ForbiddenActionException("User is banned");
            }
        });

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password
        ));

        UserDetails userDetails = userService.loadUserByUsername(username);

        return jwtTokenUtils.generateToken(userDetails);
    }

    public void createUser(UserRegistrationDto userRegistrationDto) {
        if (userService.getByUsername(userRegistrationDto.getUsername()).isPresent())
            throw new UsernameAlreadyExistException(
                    String.format("Username '%s' is already taken", userRegistrationDto.getUsername())
            );

        userService.createUser(userRegistrationDto);
    }
}

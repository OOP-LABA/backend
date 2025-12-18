package org.uneev.charityboard.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.uneev.charityboard.service.UserService;
import org.uneev.charityboard.utils.JwtTokenUtils;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        Optional<String> jwt = extractTokenFromRequest(request);

        if (jwt.isPresent()) {
            String token = jwt.get();
            String username = jwtTokenUtils.getUsernameFromToken(token);

            if (userService.isBanned(username)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User is banned");
                return;
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRolesFromToken(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> extractTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token) && token.startsWith("Bearer "))
            return Optional.of(token.substring(7));

        return Optional.empty();
    }
}

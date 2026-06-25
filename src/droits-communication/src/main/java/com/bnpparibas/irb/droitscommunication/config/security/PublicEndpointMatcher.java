package com.bnpparibas.irb.droitscommunication.config.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * Centralise la liste des endpoints publics (déclarés dans {@code qf.security.public-endpoints}) :
 * sert à la fois à la {@code SecurityFilterChain} ({@link #patterns()}) et aux filtres custom
 * ({@link #matches(HttpServletRequest)}) pour court-circuiter l'authentification.
 */
@Component
@RequiredArgsConstructor
public class PublicEndpointMatcher {

    private final QfSecurityProperties properties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public String[] patterns() {
        return properties.publicEndpoints().toArray(String[]::new);
    }

    public boolean matches(HttpServletRequest request) {
        String path = request.getServletPath();
        return properties.publicEndpoints().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }
}

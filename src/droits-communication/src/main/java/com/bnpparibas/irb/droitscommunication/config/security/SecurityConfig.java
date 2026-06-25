package com.bnpparibas.irb.droitscommunication.config.security;

import com.bnpparibas.irb.droitscommunication.config.security.mockauth.QfSimplifiedAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

/**
 * Configuration de sécurité — chaîne STATELESS calquée sur le projet frère qlickflow-wfdtrp.
 *
 * <p>Les endpoints publics ({@code qf.security.public-endpoints}) sont ouverts ; tout le reste
 * exige une authentification. En mode {@code mock} (dev), c'est le {@link QfSimplifiedAuthFilter}
 * qui parse le JWT et alimente le contexte ; en mode {@code oauth} (prod), on branchera un
 * resource server JWT classique.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final QfSimplifiedAuthFilter simplifiedAuthFilter;
    private final CorrelationIdFilter correlationIdFilter;
    private final PublicEndpointMatcher publicEndpointMatcher;

    @Bean
    public SecurityFilterChain simpleAuthBasedSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // Autorise l'affichage de la console H2 en dev (frames same-origin).
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicEndpointMatcher.patterns()).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(correlationIdFilter, SecurityContextHolderFilter.class)
                .addFilterBefore(simplifiedAuthFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }
}

package com.bnpparibas.irb.droitscommunication.config.security.mockauth;

import com.bnpparibas.irb.droitscommunication.config.security.PublicEndpointMatcher;
import com.bnpparibas.irb.droitscommunication.user.ProfileEnum;
import com.bnpparibas.irb.droitscommunication.user.UserDetailsService;
import com.bnpparibas.irb.droitscommunication.user.UserService;
import com.bnpparibas.irb.droitscommunication.user.dto.InternalUserDTO;
import com.bnpparibas.irb.droitscommunication.user.dto.RoleDTO;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Filtre d'authentification <b>simplifiée</b> (mode {@code mock}, dev) calqué sur le projet
 * frère qlickflow-wfdtrp.
 *
 * <p>Il <b>parse</b> le JWT du header {@code Authorization: Bearer} (sans vérifier la signature),
 * en extrait l'{@code uid}, récupère l'utilisateur via {@link UserDetailsService}, contrôle son
 * état (actif) et son profil ({@link ProfileEnum}), puis alimente le {@code SecurityContext}
 * avec les autorités dérivées de ses permissions.
 *
 * <p>À remplacer / compléter par un vrai resource server JWT quand {@code qf.security.auth-mode=oauth}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QfSimplifiedAuthFilter extends OncePerRequestFilter {

    /** Préfixe technique des codes de profil/permission propres au module Droits de Communication. */
    private static final String DC_PERMISSION_PREFIX = "DC_";

    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PublicEndpointMatcher publicEndpointMatcher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return publicEndpointMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            log.warn("Missing Authorization Bearer token for path:{}", request.getServletPath());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization Bearer token");
            return;
        }

        var token = authorization.substring("Bearer ".length());
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            var uid = resolveUid(claims);
            if (StringUtils.isNotBlank(uid)) {
                var userDetails = userDetailsService.fetchUser(uid);
                if (userDetails == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                    return;
                }
                if (Boolean.FALSE.equals(userDetails.getEnabled())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "User disabled");
                    return;
                }
                String profileCode = userDetails.getProfile().getCode();
                if (!ProfileEnum.exists(profileCode.replace(DC_PERMISSION_PREFIX, ""))) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                            "User profile '" + profileCode + "' is not authorized to request droits-communication");
                    return;
                }

                userService.findOrSyncUserByUid(userDetails.getUid(), userDetails);

                var authorities = getAuthorities(userDetails);
                var authentication = new QfAuthenticationToken(uid, userDetails, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Mocked authentication success for uid={}", uid);
                filterChain.doFilter(request, response);
            }
        } catch (Exception ex) {
            log.error("Authentication failed", ex);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String resolveUid(JWTClaimsSet claims) {
        try {
            String uid = claims.getStringClaim("uid");
            if (StringUtils.isNoneBlank(uid)) {
                return uid;
            }
        } catch (Exception ignored) {
            // claim "uid" absent : on retombe sur le subject du jeton
        }
        return claims.getSubject();
    }

    private static List<SimpleGrantedAuthority> getAuthorities(InternalUserDTO userDetails) {
        var profile = userDetails.getProfile();
        if (profile == null || CollectionUtils.isEmpty(profile.getRoles())) {
            return List.of();
        }
        return profile.getRoles().stream()
                .map(RoleDTO::getPermissions)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .map(permission -> permission.getCode().replaceAll(DC_PERMISSION_PREFIX, ""))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}

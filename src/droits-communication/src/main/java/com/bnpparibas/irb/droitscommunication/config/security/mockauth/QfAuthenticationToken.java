package com.bnpparibas.irb.droitscommunication.config.security.mockauth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Jeton d'authentification interne : porte l'{@code uid} (identifiant utilisateur) et le
 * {@code principal} (détails utilisateur) avec ses autorités. Construit par le
 * {@link QfSimplifiedAuthFilter} une fois le JWT validé.
 */
public class QfAuthenticationToken extends AbstractAuthenticationToken {

    private final String uid;
    private final transient Object principal;

    public QfAuthenticationToken(String uid, Object principal,
                                 Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.uid = uid;
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal != null ? principal : uid;
    }

    public String getUid() {
        return uid;
    }
}

package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * HYPOTHESES :
 *  - QfOauthUtils.resolveUid(Authentication) static
 *  - si OAuth2AuthenticationToken : lit attribut "preferred_username", sinon "sub",
 *    sinon principal.getName(); puis fallback authentication.getName()
 */

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QfOauthUtilsTest {

    private OAuth2AuthenticationToken oauthTokenWith(Map<String, Object> attrs, String nameKey) {
        OAuth2User user = new DefaultOAuth2User(
                List.of(() -> "ROLE_USER"), attrs, nameKey);
        return new OAuth2AuthenticationToken(user, user.getAuthorities(), "reg-id");
    }

    @Test
    void resolveUid_usesPreferredUsername_whenPresent() {
        var auth = oauthTokenWith(Map.of("preferred_username", "john", "sub", "sub-1"), "preferred_username");
        assertThat(QfOauthUtils.resolveUid(auth)).isEqualTo("john");
    }

    @Test
    void resolveUid_fallsBackToSub_whenNoPreferredUsername() {
        var auth = oauthTokenWith(Map.of("sub", "sub-1"), "sub");
        assertThat(QfOauthUtils.resolveUid(auth)).isEqualTo("sub-1");
    }

    @Test
    void resolveUid_fallsBackToName_whenNoAttributes() {
        var auth = oauthTokenWith(Map.of("name", "the-name"), "name");
        assertThat(QfOauthUtils.resolveUid(auth)).isNotNull();
    }

    @Test
    void resolveUid_nonOAuthAuthentication_usesGetName() {
        Authentication auth = new UsernamePasswordAuthenticationToken("plain-user", "pwd");
        assertThat(QfOauthUtils.resolveUid(auth)).isEqualTo("plain-user");
    }
}

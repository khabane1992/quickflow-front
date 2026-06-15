package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * Tests "records" : peu de logique mais ils comptent dans la couverture
 * (accesseurs, equals/hashCode/toString générés). Adapter les champs aux
 * définitions réelles si elles diffèrent des captures.
 */

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RecordsTest {

    @Test
    void oAuthTokenDTO_accessorsAndEquals() {
        Instant a = Instant.now();
        Instant r = a.plusSeconds(3600);
        OAuthTokenDTO dto = new OAuthTokenDTO("uid", "access", "refresh", a, r);

        assertThat(dto.uid()).isEqualTo("uid");
        assertThat(dto.accessToken()).isEqualTo("access");
        assertThat(dto.refreshToken()).isEqualTo("refresh");
        assertThat(dto.accessTokenExpiresAt()).isEqualTo(a);
        assertThat(dto.refreshTokenExpiresAt()).isEqualTo(r);

        OAuthTokenDTO same = new OAuthTokenDTO("uid", "access", "refresh", a, r);
        assertThat(dto).isEqualTo(same).hasSameHashCodeAs(same);
        assertThat(dto.toString()).contains("uid");
    }

    @Test
    void qfRefreshTokenResponse_accessors() {
        QfRefreshTokenResponse resp =
                new QfRefreshTokenResponse("access", "refresh", 3600L, "Bearer");
        assertThat(resp.accessToken()).isEqualTo("access");
        assertThat(resp.refreshToken()).isEqualTo("refresh");
        assertThat(resp.expiresIn()).isEqualTo(3600L);
        assertThat(resp.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void qfGatewayOAuthProperties_accessors() {
        QfGatewayOAuthProperties p = new QfGatewayOAuthProperties(
                "client-id", "secret", "http://token", Duration.ofSeconds(60));
        assertThat(p.clientId()).isEqualTo("client-id");
        assertThat(p.clientSecret()).isEqualTo("secret");
        assertThat(p.tokenUri()).isEqualTo("http://token");
        assertThat(p.expiryMargin()).isEqualTo(Duration.ofSeconds(60));
    }

    @Test
    void qfUrlsProperties_accessors() {
        QfUrlsProperties p = new QfUrlsProperties("http://frontend", "http://ms-admin");
        assertThat(p.frontendBaseUrl()).isEqualTo("http://frontend");
        assertThat(p.msAdminBaseUrl()).isEqualTo("http://ms-admin");
    }

    @Test
    void qfSecurityProperties_accessors() {
        QfSecurityProperties p = new QfSecurityProperties(true);
        assertThat(p.mockAuthEnabled()).isTrue();
    }
}

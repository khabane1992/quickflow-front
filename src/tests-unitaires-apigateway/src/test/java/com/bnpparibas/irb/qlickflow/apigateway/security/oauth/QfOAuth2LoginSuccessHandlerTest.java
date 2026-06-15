package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * HYPOTHESES (cf. img5/6/7) :
 *  - QfOAuth2LoginSuccessHandler(ReactiveOAuth2AuthorizedClientService authorizedClientService,
 *                                WebClient webClient,
 *                                QfUrlsProperties urlsProperties)
 *  - onAuthenticationSuccess(WebFilterExchange, Authentication) :
 *      cast en OAuth2AuthenticationToken -> loadAuthorizedClient -> flatMap :
 *        construit request Map, POST /api/v1/internal/oauth-tokens, puis redirectToFrontend
 *  - redirectToFrontend met statut 302 FOUND + Location = frontendBaseUrl
 *  Le webClient.post() chain est mocké.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QfOAuth2LoginSuccessHandlerTest {

    @Mock
    ReactiveOAuth2AuthorizedClientService authorizedClientService;
    @Mock
    WebClient webClient;
    @Mock
    QfUrlsProperties urlsProperties;
    @Mock
    WebFilterExchange webFilterExchange;

    QfOAuth2LoginSuccessHandler handler;

    @BeforeEach
    void setUp() {
        lenient().when(urlsProperties.frontendBaseUrl()).thenReturn("http://frontend");
        lenient().when(urlsProperties.msAdminBaseUrl()).thenReturn("http://ms-admin");
        handler = new QfOAuth2LoginSuccessHandler(authorizedClientService, webClient, urlsProperties);
    }

    @Test
    void onAuthenticationSuccess_postsTokenAndRedirects() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/login/oauth2/code/reg").build());
        when(webFilterExchange.getExchange()).thenReturn(exchange);

        OAuth2User user = new DefaultOAuth2User(List.of(() -> "ROLE_USER"),
                Map.of("preferred_username", "john", "sub", "sub-1"), "preferred_username");
        OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
                user, user.getAuthorities(), "reg-id");

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "access-value",
                Instant.now(), Instant.now().plusSeconds(3600));
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                "refresh-value", Instant.now());
        OAuth2AuthorizedClient client = mock(OAuth2AuthorizedClient.class);
        when(client.getAccessToken()).thenReturn(accessToken);
        when(client.getRefreshToken()).thenReturn(refreshToken);

        when(authorizedClientService.loadAuthorizedClient(anyString(), anyString()))
                .thenReturn(Mono.just(client));

        WebClient.RequestBodyUriSpec bodyUriSpec =
                mock(WebClient.RequestBodyUriSpec.class, Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.bodyValue(any())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuthTokenDTO.class))
                .thenReturn(Mono.just(new OAuthTokenDTO("john", "a", "r",
                        Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200))));

        StepVerifier.create(handler.onAuthenticationSuccess(webFilterExchange, auth))
                .verifyComplete();

        org.assertj.core.api.Assertions.assertThat(exchange.getResponse().getStatusCode())
                .isEqualTo(HttpStatus.FOUND);
    }
}

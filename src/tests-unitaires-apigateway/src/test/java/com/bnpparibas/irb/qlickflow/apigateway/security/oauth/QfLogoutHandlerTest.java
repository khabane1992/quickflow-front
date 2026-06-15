package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * HYPOTHESES (cf. img12) :
 *  - QfLogoutHandler(WebClient webClient, QfUrlsProperties urlsProperties)
 *  - logout(WebFilterExchange exchange, Authentication authentication) :
 *      si auth null ou !isAuthenticated -> Mono.empty()
 *      sinon resolveUid via QfOauthUtils -> webClient.delete().uri(.../{uid}).retrieve()
 *            .bodyToMono(Void).onErrorResume(ex -> Mono.empty())
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QfLogoutHandlerTest {

    @Mock
    WebClient webClient;
    @Mock
    QfUrlsProperties urlsProperties;
    @Mock
    WebFilterExchange webFilterExchange;

    QfLogoutHandler handler;

    @BeforeEach
    void setUp() {
        when(urlsProperties.msAdminBaseUrl()).thenReturn("http://ms-admin");
        handler = new QfLogoutHandler(webClient, urlsProperties);
    }

    @Test
    void logout_returnsEmpty_whenAuthenticationNull() {
        StepVerifier.create(handler.logout(webFilterExchange, null)).verifyComplete();
    }

    @Test
    void logout_returnsEmpty_whenNotAuthenticated() {
        Authentication auth = new UsernamePasswordAuthenticationToken("u", "p");
        auth.setAuthenticated(false);
        StepVerifier.create(handler.logout(webFilterExchange, auth)).verifyComplete();
    }

    @Test
    void logout_callsDelete_whenAuthenticated() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user-1", "pwd");

        WebClient.RequestHeadersUriSpec uriSpec =
                mock(WebClient.RequestHeadersUriSpec.class, Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.delete()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        StepVerifier.create(handler.logout(webFilterExchange, auth)).verifyComplete();
    }

    @Test
    void logout_swallowsError_onDeleteFailure() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user-1", "pwd");

        WebClient.RequestHeadersUriSpec uriSpec =
                mock(WebClient.RequestHeadersUriSpec.class, Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.delete()).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString(), anyString())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException("x")));

        StepVerifier.create(handler.logout(webFilterExchange, auth)).verifyComplete();
    }
}

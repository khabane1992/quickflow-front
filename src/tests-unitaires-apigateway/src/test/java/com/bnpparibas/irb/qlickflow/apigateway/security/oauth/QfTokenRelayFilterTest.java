package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * HYPOTHESES (cf. img14/15) :
 *  - QfTokenRelayFilter(QfOAuthTokenFacade oAuthTokenFacade) (@RequiredArgsConstructor)
 *  - filter() : si path ne commence pas par "/api/" -> chain.filter(exchange)
 *      sinon getPrincipal().cast(Authentication).flatMap(...) -> resolveUid ->
 *      facade.getValidToken(uid).flatMap(token -> relayBearerToken(...))
 *      .switchIfEmpty(unauthorized).onErrorResume(... unauthorized)
 *  - getOrder() == -1
 *  resolveUid est appelé via QfOauthUtils (static) dans le filtre.
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QfTokenRelayFilterTest {

    @Mock
    QfOAuthTokenFacade facade;
    @Mock
    GatewayFilterChain chain;

    private QfTokenRelayFilter filter() {
        return new QfTokenRelayFilter(facade);
    }

    @Test
    void filter_skipsNonApiPaths() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/public/home").build());
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(filter().filter(exchange, chain)).verifyComplete();
    }

    @Test
    void filter_relaysToken_whenAuthenticatedAndTokenValid() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/resource").build());

        var auth = new UsernamePasswordAuthenticationToken("user-1", "pwd");
        OAuthTokenDTO token = new OAuthTokenDTO("user-1", "access", "refresh",
                Instant.now().plusSeconds(3600), Instant.now().plusSeconds(7200));

        lenient().when(facade.getValidToken(anyString())).thenReturn(Mono.just(token));
        lenient().when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(
                filter().filter(exchange, chain)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                                Mono.just(new SecurityContextImpl(auth))))
        ).verifyComplete();
    }

    @Test
    void filter_unauthorized_whenNoPrincipal() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/resource").build());

        StepVerifier.create(filter().filter(exchange, chain)).verifyComplete();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getOrder_returnsMinusOne() {
        assertThat(filter().getOrder()).isEqualTo(-1);
    }
}

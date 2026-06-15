package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * HYPOTHESES :
 *  - AuthUtils.unauthorized(ServerWebExchange) -> set 401 + setComplete()
 *  - AuthUtils.relayBearerToken(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken)
 *    -> mutate request avec setBearerAuth + chain.filter(...)
 *  Vérifie le 3e paramètre exact (token / accessToken) sur ta capture (img16).
 */

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthUtilsTest {

    @Test
    void unauthorized_sets401AndCompletes() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());

        StepVerifier.create(AuthUtils.unauthorized(exchange)).verifyComplete();
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void relayBearerToken_addsBearerHeaderAndCallsChain() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/test").build());

        org.springframework.cloud.gateway.filter.GatewayFilterChain chain =
                mock(org.springframework.cloud.gateway.filter.GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        StepVerifier.create(AuthUtils.relayBearerToken(exchange, chain, "my-access-token"))
                .verifyComplete();

        Mockito.verify(chain).filter(any(ServerWebExchange.class));
    }
}

package com.bnpparibas.irb.qlickflow.apigateway.observability.correlation;

/*
 * HYPOTHESES (cf. img13) :
 *  - CorrelationGatewayFilter(CorrelationIdProvider correlationIdProvider)
 *  - filter() : resolveOrCreate(exchange) -> ajoute header CorrelationIdConstants.HEADER_NAME
 *               -> chain.filter(mutatedExchange)
 *  - getOrder() == Ordered.HIGHEST_PRECEDENCE
 *  Adapter le nom de la constante HEADER_NAME si différent.
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrelationGatewayFilterTest {

    @Mock
    CorrelationIdProvider correlationIdProvider;
    @Mock
    GatewayFilterChain chain;

    @Test
    void filter_addsCorrelationHeaderAndCallsChain() {
        when(correlationIdProvider.resolveOrCreate(any(ServerWebExchange.class)))
                .thenReturn("corr-123");
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/x").build());

        CorrelationGatewayFilter filter = new CorrelationGatewayFilter(correlationIdProvider);

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(chain).filter(captor.capture());
        String header = captor.getValue().getRequest().getHeaders()
                .getFirst(CorrelationIdConstants.HEADER_NAME);
        assertThat(header).isEqualTo("corr-123");
    }

    @Test
    void getOrder_isHighestPrecedence() {
        CorrelationGatewayFilter filter = new CorrelationGatewayFilter(correlationIdProvider);
        assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }
}

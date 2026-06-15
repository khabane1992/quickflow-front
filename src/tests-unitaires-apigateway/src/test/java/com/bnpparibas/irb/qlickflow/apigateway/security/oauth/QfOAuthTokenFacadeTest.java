package com.bnpparibas.irb.qlickflow.apigateway.security.oauth;

/*
 * ============================================================================
 *  HYPOTHESES (à vérifier — déduites des captures d'écran) :
 *  - Constructeur (via @RequiredArgsConstructor, dans cet ordre) :
 *      QfOAuthTokenFacade(WebClient webClient,
 *                         QfGatewayOAuthProperties properties,
 *                         QfUrlsProperties urlsProperties,
 *                         Cache<String,OAuthTokenDTO> oAuthTokenCache,
 *                         ConcurrentHashMap<String,Mono<OAuthTokenDTO>> refreshInProgress)
 *    -> si refreshInProgress est initialisé "= new ConcurrentHashMap<>()" en
 *       inline (et non final injecté), retire-le du constructeur et garde
 *       seulement les 4 premiers args. (cf. img1 ligne 29 : il semble inline)
 *  - Cache = com.github.benmanes.caffeine.cache.Cache (Caffeine)
 *  - OAuthTokenDTO(uid, accessToken, refreshToken, accessTokenExpiresAt, refreshTokenExpiresAt)
 *  - isExpiredOrAlmostExpired(token) basé sur accessTokenExpiresAt vs Instant.now()+margin
 *  - getValidToken / getTokenFromUsers / refreshToken renvoient Mono<OAuthTokenDTO>
 * ============================================================================
 */

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QfOAuthTokenFacadeTest {

    @Mock
    WebClient webClient;
    @Mock
    QfGatewayOAuthProperties properties;
    @Mock
    QfUrlsProperties urlsProperties;
    @Mock
    Cache<String, OAuthTokenDTO> cache;

    ConcurrentHashMap<String, Mono<OAuthTokenDTO>> refreshInProgress;

    QfOAuthTokenFacade facade;

    private static final String UID = "user-1";

    @BeforeEach
    void setUp() {
        refreshInProgress = new ConcurrentHashMap<>();
        lenient().when(urlsProperties.msAdminBaseUrl()).thenReturn("http://ms-admin");
        lenient().when(properties.expiryMargin()).thenReturn(Duration.ofSeconds(60));
        // Adapter au vrai constructeur (voir HYPOTHESES en tête de fichier) :
        facade = new QfOAuthTokenFacade(webClient, properties, urlsProperties, cache, refreshInProgress);
    }

    private OAuthTokenDTO validToken() {
        return new OAuthTokenDTO(UID, "access", "refresh",
                Instant.now().plus(Duration.ofHours(1)),
                Instant.now().plus(Duration.ofHours(8)));
    }

    private OAuthTokenDTO expiredToken() {
        return new OAuthTokenDTO(UID, "access-old", "refresh-old",
                Instant.now().minus(Duration.ofMinutes(1)),
                Instant.now().plus(Duration.ofHours(8)));
    }

    @Test
    void getValidToken_returnsCachedToken_whenPresentAndNotExpired() {
        OAuthTokenDTO cached = validToken();
        when(cache.getIfPresent(UID)).thenReturn(cached);

        StepVerifier.create(facade.getValidToken(UID))
                .expectNext(cached)
                .verifyComplete();
    }

    @Test
    void getValidToken_fallsBackToGetTokenFromUsers_whenCacheMiss() {
        when(cache.getIfPresent(UID)).thenReturn(null);

        OAuthTokenDTO fromUsers = validToken();
        // getTokenFromUsers fait un webClient.get()... bodyToMono(OAuthTokenDTO.class)
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class, org.mockito.Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), eq(UID))).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuthTokenDTO.class)).thenReturn(Mono.just(fromUsers));

        StepVerifier.create(facade.getValidToken(UID))
                .expectNext(fromUsers)
                .verifyComplete();
    }

    @Test
    void getValidToken_whenCachedExpired_triggersRefreshPath() {
        when(cache.getIfPresent(UID)).thenReturn(expiredToken());

        // Selon implémentation, un cache expiré déclenche getTokenFromUsers/refreshOnce.
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class, org.mockito.Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        lenient().when(webClient.get()).thenReturn(uriSpec);
        lenient().when(uriSpec.uri(anyString(), eq(UID))).thenReturn(uriSpec);
        lenient().when(uriSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.bodyToMono(OAuthTokenDTO.class)).thenReturn(Mono.just(validToken()));

        StepVerifier.create(facade.getValidToken(UID))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getTokenFromUsers_propagatesError() {
        when(cache.getIfPresent(UID)).thenReturn(null);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class, org.mockito.Answers.RETURNS_SELF);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), eq(UID))).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuthTokenDTO.class))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        StepVerifier.create(facade.getValidToken(UID))
                .expectError(RuntimeException.class)
                .verify();
    }
}

# Tests unitaires — API Gateway OAuth (Spring Boot 3 / Java 17)

Classes de test générées pour viser ~80% de couverture sur le module sécurité/observabilité.
Style : **JUnit 5 + Mockito + Reactor StepVerifier** + `MockServerWebExchange` (WebFlux), sans `@SpringBootTest`.

## ⚠️ À LIRE EN PREMIER — ces tests sont reconstruits à partir de photos d'écran

Les captures étaient partielles/floues. J'ai dû **déduire** certaines signatures.
Chaque fichier de test commence par un bloc `HYPOTHESES` listant ce que tu dois vérifier.
Si un test ne compile pas, c'est presque toujours l'un de ces points :

1. **Le package racine.** J'ai utilisé `com.bnpparibas.irb.qlickflow.apigateway`.
   Sur certaines captures on lit `com.bnpparibas.irb.qlickflow`, sur d'autres
   `com.bnpparibas` tout court. → fais un rechercher/remplacer global du package.

2. **Constructeur de `QfOAuthTokenFacade`.** J'ai supposé l'ordre :
   `(WebClient, QfGatewayOAuthProperties, QfUrlsProperties, Cache, ConcurrentHashMap)`.
   Sur img1 le champ `refreshInProgress` semble initialisé **inline**
   (`= new ConcurrentHashMap<>()`), donc PAS dans le constructeur Lombok.
   → si c'est le cas, retire le dernier argument du `new QfOAuthTokenFacade(...)`.

3. **Type du cache.** Supposé `com.github.benmanes.caffeine.cache.Cache`.
   Si c'est `org.springframework.cache.Cache` ou autre, adapte l'import + le mock.

4. **Accesseurs des records.** Java records → accesseurs sans `get`
   (`dto.uid()`, pas `dto.getUid()`). Vérifie les noms exacts des champs.

5. **`CorrelationIdConstants.HEADER_NAME`** : adapte le nom de la constante.

6. **3e paramètre de `AuthUtils.relayBearerToken`** : j'ai supposé un `String accessToken`.

## Dépendances Maven requises (pom.xml)

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>io.projectreactor</groupId>
  <artifactId>reactor-test</artifactId>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-test</artifactId>
  <scope>test</scope>
</dependency>
```
(spring-cloud-starter-gateway et spring-boot-starter-oauth2-client doivent déjà être présents en compile.)

### Couverture (JaCoCo) — ajoute si pas déjà fait

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.12</version>
  <executions>
    <execution><goals><goal>prepare-agent</goal></goals></execution>
    <execution><id>report</id><phase>test</phase><goals><goal>report</goal></goals></execution>
  </executions>
</plugin>
```
Lancer : `mvn clean test` puis ouvrir `target/site/jacoco/index.html`.

## Fichiers de test fournis

| Test | Classe ciblée |
|------|----------------|
| QfOAuthTokenFacadeTest | QfOAuthTokenFacade |
| QfOauthUtilsTest | QfOauthUtils |
| AuthUtilsTest | AuthUtils |
| QfTokenRelayFilterTest | QfTokenRelayFilter |
| QfLogoutHandlerTest | QfLogoutHandler |
| QfOAuth2LoginSuccessHandlerTest | QfOAuth2LoginSuccessHandler |
| CorrelationGatewayFilterTest | CorrelationGatewayFilter |
| RecordsTest | OAuthTokenDTO, QfRefreshTokenResponse, QfGatewayOAuthProperties, QfUrlsProperties, QfSecurityProperties |

## Ce qu'il me manque pour aller plus loin / plus sûr

Pour fiabiliser et couvrir les branches restantes de `QfOAuthTokenFacade`
(`refreshToken`, `refreshOnce`, `updateTokenInUsers`, `deleteTokenInUsers`,
`isExpiredOrAlmostExpired`) et de `QfOAuth2LoginSuccessHandler`, colle-moi le
**code texte** (pas photo) de :

- `QfOAuthTokenFacade.java` (complet)
- `QfOAuth2LoginSuccessHandler.java` (complet, surtout le bas)
- `CorrelationIdProvider` + `CorrelationIdConstants`
- les 5 records (signatures exactes des champs)

Je te renverrai alors des tests qui compilent du premier coup et qui ciblent
précisément les branches non couvertes pour dépasser 80%.

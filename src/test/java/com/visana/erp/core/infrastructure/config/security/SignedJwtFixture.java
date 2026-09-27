package com.visana.erp.core.infrastructure.config.security;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/** Ephemeral RSA/JWKS fixture, loopback only. No real issuer or credential is used. */
public final class SignedJwtFixture implements AutoCloseable {
    public static final String CLIENT = "fixture-health-client";
    public static final String SUBJECT = "fixture-health-subject";
    public static final String AUDIENCE = "fixture-health-audience";
    private final RSAKey key;
    private final HttpServer server;
    public final String issuer;

    public SignedJwtFixture() {
        try {
            key = new RSAKeyGenerator(2048).keyID("synthetic-key").generate();
            server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            issuer = "http://127.0.0.1:" + server.getAddress().getPort() + "/fixture";
            byte[] json = ("{\"keys\":[" + key.toPublicJWK().toJSONString() + "]}").getBytes(StandardCharsets.UTF_8);
            server.createContext("/jwks", exchange -> {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.length);
                exchange.getResponseBody().write(json);
                exchange.close();
            });
            server.start();
        } catch (Exception error) { throw new IllegalStateException("Synthetic JWKS fixture unavailable"); }
    }

    public NimbusJwtDecoder decoder(HealthClientPolicy policy) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(issuer.replace("/fixture", "/jwks")).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), policy));
        return decoder;
    }

    public Map<String, Object> technicalClaims() {
        return Map.of("sub", SUBJECT, "azp", CLIENT, "aud", List.of(AUDIENCE), "scope", HealthClientPolicy.SCOPE);
    }

    public String token(Map<String, Object> claims) throws Exception {
        var builder = new JWTClaimsSet.Builder().issuer(issuer).issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(300)));
        claims.forEach(builder::claim);
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).keyID(key.getKeyID()).build(), builder.build());
        jwt.sign(new RSASSASigner(key));
        return jwt.serialize();
    }

    @Override public void close() { server.stop(0); }
}

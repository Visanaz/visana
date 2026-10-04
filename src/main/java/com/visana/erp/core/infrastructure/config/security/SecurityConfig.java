package com.visana.erp.core.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    @Bean
    public HealthClientPolicy healthClientPolicy(
            @Value("${visana.security.health-client-id:}") String client,
            @Value("${visana.security.health-subject:}") String subject,
            @Value("${visana.security.health-audience:}") String audience,
            org.springframework.core.env.Environment environment) {
        return new HealthClientPolicy(client, subject, audience,
                environment.acceptsProfiles(org.springframework.core.env.Profiles.of("dev")));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HealthClientPolicy policy, JwtDecoder decoder) throws Exception {
        // Preserve Boot's signature/issuer/time validators and add the scoped technical contract.
        JwtDecoder validatedDecoder = token -> {
            var jwt = decoder.decode(token);
            var result = policy.validate(jwt);
            if (result.hasErrors()) throw new JwtValidationException("Invalid technical health contract", result.getErrors());
            return jwt;
        };
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
				.headers(headers -> headers
						.contentTypeOptions(Customizer.withDefaults())
						.frameOptions(frame -> frame.deny())
						.referrerPolicy(referrer -> referrer.policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
				)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().access((authentication, context) -> {
                            var principal = authentication.get();
                            var request = context.getRequest();
                            String path = request.getRequestURI().substring(request.getContextPath().length());
                            if (principal instanceof JwtAuthenticationToken jwt && policy.technical(jwt.getToken())) {
                                return new AuthorizationDecision(!policy.validate(jwt.getToken()).hasErrors()
                                        && "GET".equals(request.getMethod()) && "/actuator/health".equals(path));
                            }
                            boolean publicDocs = path.equals("/v3/api-docs") || path.startsWith("/v3/api-docs/")
                                    || path.equals("/swagger-ui.html") || path.equals("/swagger-ui") || path.startsWith("/swagger-ui/");
                            return new AuthorizationDecision(publicDocs || (principal != null && principal.isAuthenticated()
                                    && !(principal instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)));
                        })
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(validatedDecoder).jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
                );

        return http.build();
    }
}

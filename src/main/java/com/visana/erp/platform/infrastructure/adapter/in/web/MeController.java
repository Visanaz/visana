package com.visana.erp.platform.infrastructure.adapter.in.web;

import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.platform.application.identity.ActorResolution;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.identity.AuthenticatedPrincipal;
import com.visana.erp.platform.application.identity.ActorResolutionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/me")
@Tag(name = "Identity", description = "Safe authenticated platform identity endpoints")
public class MeController {
    private final KeycloakAuthenticatedPrincipalAdapter principalAdapter;
    private final ActorResolverPort actorResolver;

    public MeController(KeycloakAuthenticatedPrincipalAdapter principalAdapter, ActorResolverPort actorResolver) {
        this.principalAdapter = principalAdapter;
        this.actorResolver = actorResolver;
    }

    @GetMapping
    @Operation(operationId = "getCurrentPlatformActor", summary = "Get current platform actor", description = "Returns only internal actor linkage status and granted role names; it never returns token claims or credentials.")
    public ResponseEntity<MeResponse> current(JwtAuthenticationToken authentication) {
        Set<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toUnmodifiableSet());
        Jwt jwt = authentication.getToken();
        AuthenticatedPrincipal principal = principalAdapter.from(jwt, roles);
        ActorResolution resolution = actorResolver.resolve(principal);
        UUID actorId = resolution.actorId() == null ? null : resolution.actorId().value();
        return ResponseEntity.ok(new MeResponse(actorId, resolution.status(), roles));
    }

    public record MeResponse(UUID actorId, ActorResolutionStatus identityStatus, Set<String> roles) { }
}

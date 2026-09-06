package com.visana.erp.network.foundation.infrastructure.web;

import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.network.foundation.application.NetworkFoundationService;
import com.visana.erp.platform.application.identity.ActorResolution;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.identity.UnlinkedIdentityException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/network")
@Tag(name = "Network", description = "Authenticated, minimum-data Plan 3 network queries")
public class NetworkQueryController {
    private final NetworkFoundationService network;
    private final KeycloakAuthenticatedPrincipalAdapter principals;
    private final ActorResolverPort actors;

    public NetworkQueryController(NetworkFoundationService network, KeycloakAuthenticatedPrincipalAdapter principals, ActorResolverPort actors) {
        this.network = network;
        this.principals = principals;
        this.actors = actors;
    }

    @GetMapping("/me")
    @Operation(operationId = "getCurrentNetworkMember", summary = "Get current network member")
    public MemberResponse me(@AuthenticationPrincipal Jwt jwt) {
        return new MemberResponse(member(jwt));
    }

    @GetMapping("/direct")
    @Operation(operationId = "listDirectNetworkMembers", summary = "List direct network members")
    public List<MemberResponse> direct(@RequestParam(defaultValue = "50") int limit, @AuthenticationPrincipal Jwt jwt) {
        return network.direct(member(jwt), limit).stream().map(MemberResponse::new).toList();
    }

    @GetMapping("/ancestors")
    @Operation(operationId = "listNetworkAncestors", summary = "List network ancestors")
    public List<MemberDepthResponse> ancestors(@RequestParam(defaultValue = "50") int limit, @AuthenticationPrincipal Jwt jwt) {
        return network.ancestors(member(jwt), limit).stream().map(value -> new MemberDepthResponse(value.memberId(), value.depth())).toList();
    }

    @GetMapping("/descendants")
    @Operation(operationId = "listNetworkDescendants", summary = "List network descendants")
    public List<MemberDepthResponse> descendants(@RequestParam(defaultValue = "50") int limit, @AuthenticationPrincipal Jwt jwt) {
        return network.descendants(member(jwt), limit).stream().map(value -> new MemberDepthResponse(value.memberId(), value.depth())).toList();
    }

    private UUID member(Jwt jwt) {
        ActorResolution resolution = actors.resolve(principals.from(jwt, Set.of()));
        if (!resolution.isLinked()) {
            throw new UnlinkedIdentityException();
        }
        return network.memberForActor(resolution.actorId().value());
    }

    public record MemberResponse(UUID memberId) {
    }

    public record MemberDepthResponse(UUID memberId, int depth) {
    }
}

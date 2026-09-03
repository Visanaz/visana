package com.visana.erp.network.foundation.infrastructure.web;

import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.network.foundation.application.NetworkFoundationService;
import com.visana.erp.network.foundation.application.NetworkOperationForbiddenException;
import com.visana.erp.platform.application.identity.ActorResolution;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NetworkQueryController.class)
class NetworkQueryControllerTest {
    @Autowired MockMvc mvc;
    @MockBean NetworkFoundationService network;
    @MockBean KeycloakAuthenticatedPrincipalAdapter principals;
    @MockBean ActorResolverPort actors;

    private final UUID actorId = UUID.randomUUID();

    @BeforeEach
    void linkedIdentity() {
        when(principals.from(any(Jwt.class), anySet()))
                .thenReturn(new com.visana.erp.platform.application.identity.AuthenticatedPrincipal(
                        "OIDC", "https://issuer.test", "subject", java.util.Set.of()));
        when(actors.resolve(any())).thenReturn(ActorResolution.linked(PlatformActorId.of(actorId)));
    }

    @Test
    void unauthenticatedNetworkReadIsDenied() throws Exception {
        mvc.perform(get("/api/v1/network/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unlinkedIdentityCannotReadNetwork() throws Exception {
        when(actors.resolve(any())).thenReturn(ActorResolution.unlinked());

        mvc.perform(get("/api/v1/network/me").with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void actorWithoutNetworkMembershipCannotReadNetwork() throws Exception {
        when(network.memberForActor(actorId)).thenThrow(new NetworkOperationForbiddenException("not linked"));

        mvc.perform(get("/api/v1/network/me").with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void linkedMemberCanReadOnlyItsOwnNetworkProjection() throws Exception {
        UUID member = UUID.randomUUID();
        UUID direct = UUID.randomUUID();
        UUID ancestor = UUID.randomUUID();
        UUID descendant = UUID.randomUUID();
        when(network.memberForActor(actorId)).thenReturn(member);
        when(network.direct(member, 50)).thenReturn(List.of(direct));
        when(network.ancestors(member, 50)).thenReturn(List.of(new NetworkFoundationService.MemberDepth(ancestor, 1)));
        when(network.descendants(member, 50)).thenReturn(List.of(new NetworkFoundationService.MemberDepth(descendant, 1)));

        mvc.perform(get("/api/v1/network/me").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.toString()));
        mvc.perform(get("/api/v1/network/direct").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(direct.toString()));
        mvc.perform(get("/api/v1/network/ancestors").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(ancestor.toString()))
                .andExpect(jsonPath("$[0].depth").value(1));
        mvc.perform(get("/api/v1/network/descendants").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(descendant.toString()))
                .andExpect(jsonPath("$[0].depth").value(1));

        verify(network, times(4)).memberForActor(eq(actorId));
    }
}

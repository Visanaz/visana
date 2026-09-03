package com.visana.erp.platform.infrastructure.adapter.in.web;

import com.visana.erp.core.infrastructure.config.security.KeycloakAuthenticatedPrincipalAdapter;
import com.visana.erp.platform.application.identity.ActorResolution;
import com.visana.erp.platform.application.identity.ActorResolverPort;
import com.visana.erp.platform.application.identity.AuthenticatedPrincipal;
import com.visana.erp.platform.domain.identity.PlatformActorId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MeController.class)
class MeControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private KeycloakAuthenticatedPrincipalAdapter principalAdapter;
    @MockBean private ActorResolverPort actorResolver;

    @Test
    void returnsOnlySafeLinkedActorIdentity() throws Exception {
        UUID actorId = UUID.randomUUID();
        org.mockito.Mockito.when(principalAdapter.from(any(), anySet()))
                .thenReturn(new AuthenticatedPrincipal("OIDC", "https://issuer.test", "subject", Set.of("ROLE_USER")));
        org.mockito.Mockito.when(actorResolver.resolve(any())).thenReturn(ActorResolution.linked(PlatformActorId.of(actorId)));

        mockMvc.perform(get("/api/v1/me").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").value(actorId.toString()))
                .andExpect(jsonPath("$.identityStatus").value("LINKED"));
    }

    @Test
    void reportsUnlinkedIdentityWithoutDisclosingClaims() throws Exception {
        org.mockito.Mockito.when(principalAdapter.from(any(), anySet()))
                .thenReturn(new AuthenticatedPrincipal("OIDC", "https://issuer.test", "subject", Set.of()));
        org.mockito.Mockito.when(actorResolver.resolve(any())).thenReturn(ActorResolution.unlinked());

        mockMvc.perform(get("/api/v1/me").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actorId").doesNotExist())
                .andExpect(jsonPath("$.identityStatus").value("UNLINKED_IDENTITY"));
    }
}

package com.visana.erp.qualification.application.service;

import static com.visana.erp.qualification.support.QualificationTestFixtures.components;
import static com.visana.erp.qualification.support.QualificationTestFixtures.period;
import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.application.port.out.GenealogyClosureReader;
import com.visana.erp.qualification.domain.network.GenealogyMemberDepth;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.volume.SaleEvidence;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateTeamVolumeServiceTest {
    @Mock
    private GenealogyClosureReader genealogy;

    @Test
    void usesTheExistingClosureReaderForTheAggregationScope() {
        UUID member = UUID.randomUUID();
        UUID descendant = UUID.randomUUID();
        BusinessRuleVersion version = provisionalRule(UUID.randomUUID(), 1);
        Instant occurredAt = Instant.parse("2026-09-10T12:00:00Z");
        when(genealogy.memberAndDescendants(member)).thenReturn(List.of(
                new GenealogyMemberDepth(member, 0),
                new GenealogyMemberDepth(descendant, 1)));
        List<SaleEvidence> evidence = List.of(
                new SaleEvidence(UUID.randomUUID(), member, occurredAt, "FIXTURE", components("100"), Money.zero(), "own"),
                new SaleEvidence(UUID.randomUUID(), descendant, occurredAt, "FIXTURE", components("200"), Money.zero(), "descendant"));

        var result = new CalculateTeamVolumeService(genealogy).calculate(
                member, period(), version, evidence, ignored -> true,
                sale -> sale.monetaryComponents().netBeforeTax(),
                Instant.parse("2026-10-01T01:00:00Z"));

        assertEquals(0, Money.of("300").amount().compareTo(result.teamVolume().amount()));
    }
}

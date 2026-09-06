package com.visana.erp.qualification.application.service;

import com.visana.erp.qualification.application.port.out.GenealogyClosureReader;
import com.visana.erp.qualification.domain.network.GenealogyMemberDepth;
import com.visana.erp.qualification.domain.period.QualificationPeriod;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import com.visana.erp.qualification.domain.volume.QualificationBasePolicy;
import com.visana.erp.qualification.domain.volume.QualifyingSalePolicy;
import com.visana.erp.qualification.domain.volume.SaleEvidence;
import com.visana.erp.qualification.domain.volume.TeamSalesAggregator;
import com.visana.erp.qualification.domain.volume.VolumeResult;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CalculateTeamVolumeService {
    private final GenealogyClosureReader genealogy;
    private final TeamSalesAggregator aggregator;

    public CalculateTeamVolumeService(GenealogyClosureReader genealogy) {
        this(genealogy, new TeamSalesAggregator(new RuleExecutionGuard()));
    }

    CalculateTeamVolumeService(GenealogyClosureReader genealogy, TeamSalesAggregator aggregator) {
        this.genealogy = Objects.requireNonNull(genealogy);
        this.aggregator = Objects.requireNonNull(aggregator);
    }

    public VolumeResult calculate(
            UUID memberId,
            QualificationPeriod period,
            BusinessRuleVersion ruleVersion,
            Collection<SaleEvidence> saleEvidence,
            QualifyingSalePolicy salePolicy,
            QualificationBasePolicy basePolicy,
            Instant calculatedAt) {
        Set<UUID> scope = genealogy.memberAndDescendants(memberId).stream()
                .map(GenealogyMemberDepth::memberId)
                .collect(Collectors.toSet());
        return aggregator.aggregate(
                memberId, period, ruleVersion, scope, saleEvidence, salePolicy, basePolicy, calculatedAt);
    }
}

package com.visana.erp.qualification.domain.service;

import static com.visana.erp.qualification.support.QualificationTestFixtures.activeWindow;
import static com.visana.erp.qualification.support.QualificationTestFixtures.period;
import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.NetworkRequirement;
import com.visana.erp.qualification.domain.model.QualificationEvaluation;
import com.visana.erp.qualification.domain.model.QualificationResult;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.model.VolumeRequirement;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.volume.VolumeResult;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VersionedQualificationEvaluatorTest {
    private static final Instant PAYMENT = Instant.parse("2026-09-05T19:35:00Z");
    private static final Instant EVALUATED_AT = Instant.parse("2026-09-20T12:00:00Z");
    private final QualificationEvaluatorService evaluator = new QualificationEvaluatorService();

    @Test
    void representsAndEvaluatesTheDocumentedProvisionalL1ToL8ThresholdTable() {
        BusinessRuleVersion version = provisionalRule(UUID.randomUUID(), 1);
        VersionedQualificationRuleSet rules = provisionalThresholds(version);
        UUID member = UUID.randomUUID();
        VolumeResult volume = volume(member, version, "25000000");
        QualificationEvaluation input = new QualificationEvaluation(
                member, period(), volume, 7, 25, true, activeWindow(version, PAYMENT),
                EVALUATED_AT, List.of("volume-result:" + volume.id(), "genealogy-snapshot:fixture"));

        QualificationResult result = evaluator.evaluate(input, rules, EVALUATED_AT);

        assertEquals(8, rules.levels().size());
        assertEquals(3, result.qualifiedLevel());
        assertTrue(result.qualified());
        assertTrue(result.activationEligible());
        assertEquals(version.id(), result.businessRuleVersionId());
        assertTrue(result.levelAssessments().get(2).explanation().contains("Team Sales = 25000000.0000"));
    }

    @Test
    void requiresAffiliationForL1AndActivationForQualification() {
        BusinessRuleVersion version = provisionalRule(UUID.randomUUID(), 1);
        UUID member = UUID.randomUUID();
        VolumeResult volume = volume(member, version, "0");
        QualificationEvaluation notAffiliated = new QualificationEvaluation(
                member, period(), volume, 0, 0, false, activeWindow(version, PAYMENT),
                EVALUATED_AT, List.of("fixture"));

        QualificationResult result = evaluator.evaluate(notAffiliated, provisionalThresholds(version), EVALUATED_AT);

        assertEquals(0, result.qualifiedLevel());
        assertFalse(result.qualified());
    }

    @Test
    void preservesHistoricalResultWhenANewRuleVersionIsIntroduced() {
        UUID member = UUID.randomUUID();
        BusinessRuleVersion versionA = provisionalRule(UUID.randomUUID(), 1);
        VolumeResult volumeA = volume(member, versionA, "3500000");
        QualificationResult historicalA = evaluator.evaluate(
                evaluation(member, versionA, volumeA), twoLevelRules(versionA, "3000000"), EVALUATED_AT);

        BusinessRuleVersion hypotheticalVersionB = provisionalRule(UUID.randomUUID(), 2);
        VolumeResult volumeB = volume(member, hypotheticalVersionB, "3500000");
        QualificationResult currentB = evaluator.evaluate(
                evaluation(member, hypotheticalVersionB, volumeB),
                twoLevelRules(hypotheticalVersionB, "4000000"), EVALUATED_AT);

        assertEquals(2, historicalA.qualifiedLevel());
        assertEquals(versionA.id(), historicalA.businessRuleVersionId());
        assertEquals(1, currentB.qualifiedLevel());
        assertEquals(hypotheticalVersionB.id(), currentB.businessRuleVersionId());
        assertEquals(2, historicalA.qualifiedLevel());
    }

    @Test
    void rejectsMixedRuleVersionsAcrossActivationVolumeAndQualification() {
        UUID member = UUID.randomUUID();
        BusinessRuleVersion versionA = provisionalRule(UUID.randomUUID(), 1);
        BusinessRuleVersion versionB = provisionalRule(UUID.randomUUID(), 2);
        VolumeResult volumeA = volume(member, versionA, "3500000");

        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(
                evaluation(member, versionB, volumeA), twoLevelRules(versionB, "3000000"), EVALUATED_AT));
    }

    private QualificationEvaluation evaluation(
            UUID member,
            BusinessRuleVersion version,
            VolumeResult volume) {
        return new QualificationEvaluation(
                member, period(), volume, 5, 0, true, activeWindow(version, PAYMENT),
                EVALUATED_AT, List.of("synthetic-evidence"));
    }

    private VolumeResult volume(UUID member, BusinessRuleVersion version, String teamVolume) {
        Money amount = Money.of(teamVolume);
        return new VolumeResult(
                UUID.randomUUID(), member, period(), version.id(), Money.zero(), amount, amount,
                Money.zero(), List.of("synthetic-sale"), EVALUATED_AT);
    }

    private VersionedQualificationRuleSet twoLevelRules(BusinessRuleVersion version, String l2Volume) {
        return new VersionedQualificationRuleSet(
                version,
                List.of(rule(1, 0, 0, "0"), rule(2, 5, 0, l2Volume)),
                Set.of(1));
    }

    private VersionedQualificationRuleSet provisionalThresholds(BusinessRuleVersion version) {
        return new VersionedQualificationRuleSet(
                version,
                List.of(
                        rule(1, 0, 0, "0"),
                        rule(2, 5, 0, "3000000"),
                        rule(3, 7, 25, "25000000"),
                        rule(4, 9, 110, "80000000"),
                        rule(5, 12, 350, "250000000"),
                        rule(6, 15, 1200, "1000000000"),
                        rule(7, 20, 3500, "2500000000"),
                        rule(8, 25, 15000, "30000000000")),
                Set.of(1));
    }

    private LevelQualificationRule rule(int level, int directs, int indirects, String volume) {
        return new LevelQualificationRule(
                level, new NetworkRequirement(directs, indirects),
                new VolumeRequirement(Money.of(volume)));
    }
}

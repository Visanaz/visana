package com.visana.erp.qualification.infrastructure.persistence;

import static com.visana.erp.qualification.support.QualificationTestFixtures.period;
import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.NetworkRequirement;
import com.visana.erp.qualification.domain.model.QualificationResult;
import com.visana.erp.qualification.domain.model.QualificationLevelAssessment;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.model.VolumeRequirement;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.volume.VolumeResult;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import({JdbcQualificationRuleSetCatalog.class, JdbcQualificationHistoryStore.class})
class JdbcQualificationFoundationPersistenceTest {
    private static final UUID SEEDED_VERSION_ID = UUID.fromString("8d8ef6e5-0905-4c01-9000-000000000001");

    @Autowired
    private JdbcQualificationRuleSetCatalog catalog;

    @Autowired
    private JdbcQualificationHistoryStore historyStore;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void loadsTheVersionedProvisionalThresholdTableFromFlywayData() {
        VersionedQualificationRuleSet rules = catalog.findByVersionId(SEEDED_VERSION_ID).orElseThrow();

        assertEquals(8, rules.levels().size());
        assertEquals(Set.of(1), rules.affiliationRequiredLevels());
        assertEquals(0, Money.of("30000000000").amount().compareTo(
                rules.levels().get(7).getVolumeRequirement().minTeamSales().amount()));
        assertEquals("PROVISIONAL", rules.ruleVersion().status().name());
        assertEquals("HIGH", rules.ruleVersion().provisionalConfidence().name());
    }

    @Test
    void appendOnlyRuleVersionCannotOverwriteExistingHistory() {
        BusinessRuleVersion version = provisionalRule(UUID.randomUUID(), 42);
        VersionedQualificationRuleSet ruleSet = new VersionedQualificationRuleSet(
                version,
                List.of(new LevelQualificationRule(
                        1, new NetworkRequirement(0, 0), new VolumeRequirement(Money.zero()))),
                Set.of(1));
        historyStore.appendRuleSet(ruleSet);

        assertThrows(DataIntegrityViolationException.class, () -> historyStore.appendRuleSet(ruleSet));
        assertEquals("VISANA verbal clarification 2026-09-05",
                jdbcTemplate.queryForObject(
                        "select source from business_rule_versions where id = ?", String.class, version.id()));
    }

    @Test
    void appendsVolumeAndQualificationResultsWithExactRuleVersionReferences() {
        UUID memberId = seedNetworkMember();
        Instant calculatedAt = Instant.parse("2026-10-01T01:00:00Z");
        VolumeResult volume = new VolumeResult(
                UUID.randomUUID(), memberId, period(), SEEDED_VERSION_ID,
                Money.of("100"), Money.of("300"), Money.of("300"), Money.zero(),
                List.of("order:synthetic-1"), calculatedAt);
        historyStore.appendVolumeResult(volume);
        QualificationResult qualification = new QualificationResult(
                UUID.randomUUID(), memberId, volume.id(), period(), SEEDED_VERSION_ID,
                1, true, true, List.of(new QualificationLevelAssessment(
                        1, true, true, true, true, 0, 0, 0, 0,
                        Money.zero(), Money.of("300"), "L1 synthetic assessment")),
                List.of("volume-result:" + volume.id()),
                calculatedAt, "Qualified level = L1; synthetic persistence fixture");

        historyStore.appendQualificationResult(qualification);

        assertEquals(SEEDED_VERSION_ID, jdbcTemplate.queryForObject(
                "select business_rule_version_id from volume_results where id = ?", UUID.class, volume.id()));
        assertEquals(SEEDED_VERSION_ID, jdbcTemplate.queryForObject(
                "select business_rule_version_id from qualification_results where id = ?", UUID.class, qualification.id()));
        assertTrue(jdbcTemplate.queryForObject(
                "select count(*) from qualification_result_evidence where qualification_result_id = ?",
                Integer.class, qualification.id()) > 0);
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from qualification_result_assessments where qualification_result_id = ?",
                Integer.class, qualification.id()));
    }

    private UUID seedNetworkMember() {
        UUID actorId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Instant now = Instant.parse("2026-09-05T00:00:00Z");
        jdbcTemplate.update(
                "insert into platform_actors(actor_id, status, created_at) values (?, 'ACTIVE', ?)",
                actorId.toString(), now);
        jdbcTemplate.update(
                """
                insert into business_profiles(id, profile_type, record_status, created_by_actor_id, created_at)
                values (?, 'NETWORK_MEMBER', 'RECORD_ACTIVE', ?, ?)
                """,
                profileId, actorId.toString(), now);
        jdbcTemplate.update(
                """
                insert into network_members(id, business_profile_id, record_status, created_at)
                values (?, ?, 'RECORD_ACTIVE', ?)
                """,
                memberId, profileId, now);
        jdbcTemplate.update(
                "insert into genealogy_closure(ancestor_member_id, descendant_member_id, depth) values (?, ?, 0)",
                memberId, memberId);
        return memberId;
    }
}

package com.visana.erp.qualification.infrastructure.persistence;

import com.visana.erp.qualification.application.port.out.QualificationHistoryStore;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.QualificationLevelAssessment;
import com.visana.erp.qualification.domain.model.QualificationResult;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.volume.VolumeResult;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcQualificationHistoryStore implements QualificationHistoryStore {
    private final JdbcTemplate jdbcTemplate;

    public JdbcQualificationHistoryStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void appendRuleSet(VersionedQualificationRuleSet ruleSet) {
        BusinessRuleVersion version = ruleSet.ruleVersion();
        jdbcTemplate.update(
                """
                insert into business_rule_versions(
                    id, rule_set_id, version_number, status, effective_from, effective_to,
                    source, approved_at, approved_by, provisional_confidence, notes, created_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                version.id(), version.ruleSetId(), version.version(), version.status().name(),
                timestamp(version.effectiveFrom()), timestamp(version.effectiveTo()), version.source(),
                timestamp(version.approvedAt()), version.approvedBy(),
                version.provisionalConfidence() == null ? null : version.provisionalConfidence().name(),
                version.notes(), timestamp(Instant.now()));

        for (LevelQualificationRule level : ruleSet.levels()) {
            jdbcTemplate.update(
                    """
                    insert into qualification_rank_thresholds(
                        id, business_rule_version_id, rank_level, affiliation_required,
                        min_active_directs, min_indirects, min_team_volume, currency_code
                    ) values (?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    UUID.randomUUID(), version.id(), level.getLevel(),
                    ruleSet.affiliationRequiredLevels().contains(level.getLevel()),
                    level.getNetworkRequirement().minDirects(),
                    level.getNetworkRequirement().minIndirects(),
                    level.getVolumeRequirement().minTeamSales().amount(), "COP");
        }
    }

    @Override
    @Transactional
    public void appendVolumeResult(VolumeResult result) {
        jdbcTemplate.update(
                """
                insert into volume_results(
                    id, member_id, period_key, period_start, period_end, business_rule_version_id,
                    personal_volume, team_volume, qualification_base, adjustment_total, calculated_at
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                result.id(), result.memberId(), result.period().key(), timestamp(result.period().startInclusive()),
                timestamp(result.period().endExclusive()), result.businessRuleVersionId(),
                result.personalVolume().amount(), result.teamVolume().amount(),
                result.qualificationBase().amount(), result.adjustmentTotal().amount(), timestamp(result.calculatedAt()));
        appendEvidence("volume_result_evidence", "volume_result_id", result.id(), result.evidenceReferences());
    }

    @Override
    @Transactional
    public void appendQualificationResult(QualificationResult result) {
        jdbcTemplate.update(
                """
                insert into qualification_results(
                    id, member_id, volume_result_id, period_key, period_start, period_end,
                    business_rule_version_id, qualified_level, qualified, activation_eligible,
                    calculated_at, explanation
                ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                result.id(), result.memberId(), result.volumeResultId(), result.period().key(),
                timestamp(result.period().startInclusive()), timestamp(result.period().endExclusive()),
                result.businessRuleVersionId(), result.qualifiedLevel(), result.qualified(),
                result.activationEligible(), timestamp(result.calculatedAt()), result.explanation());
        appendEvidence(
                "qualification_result_evidence",
                "qualification_result_id",
                result.id(),
                result.evidenceReferences());
        for (QualificationLevelAssessment assessment : result.levelAssessments()) {
            jdbcTemplate.update(
                    """
                    insert into qualification_result_assessments(
                        qualification_result_id, rank_level, satisfied, activation_eligible,
                        affiliation_required, affiliated, required_directs, actual_directs,
                        required_indirects, actual_indirects, required_team_volume,
                        actual_team_volume, explanation
                    ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """,
                    result.id(), assessment.level(), assessment.satisfied(), assessment.activationEligible(),
                    assessment.affiliationRequired(), assessment.affiliated(),
                    assessment.requiredDirects(), assessment.actualDirects(),
                    assessment.requiredIndirects(), assessment.actualIndirects(),
                    assessment.requiredTeamVolume().amount(), assessment.actualTeamVolume().amount(),
                    assessment.explanation());
        }
    }

    private void appendEvidence(String table, String idColumn, UUID resultId, Iterable<String> evidence) {
        String sql = "insert into " + table + "(" + idColumn + ", evidence_reference) values (?, ?)";
        for (String reference : evidence) {
            jdbcTemplate.update(sql, resultId, reference);
        }
    }

    private Timestamp timestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }
}

package com.visana.erp.qualification.infrastructure.persistence;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.application.port.out.QualificationRuleSetCatalog;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.NetworkRequirement;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.model.VolumeRequirement;
import com.visana.erp.qualification.domain.rules.BusinessRuleStatus;
import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.ProvisionalConfidence;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcQualificationRuleSetCatalog implements QualificationRuleSetCatalog {
    private final JdbcTemplate jdbcTemplate;

    public JdbcQualificationRuleSetCatalog(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VersionedQualificationRuleSet> findByVersionId(UUID versionId) {
        List<BusinessRuleVersion> versions = jdbcTemplate.query(
                """
                select id, rule_set_id, version_number, status, effective_from, effective_to,
                       source, approved_at, approved_by, provisional_confidence, notes
                from business_rule_versions
                where id = ?
                """,
                (resultSet, row) -> mapVersion(resultSet),
                versionId);
        if (versions.isEmpty()) {
            return Optional.empty();
        }

        Set<Integer> affiliationRequired = new LinkedHashSet<>();
        List<LevelQualificationRule> levels = jdbcTemplate.query(
                """
                select rank_level, affiliation_required, min_active_directs, min_indirects, min_team_volume
                from qualification_rank_thresholds
                where business_rule_version_id = ?
                order by rank_level
                """,
                (resultSet, row) -> {
                    int level = resultSet.getInt("rank_level");
                    if (resultSet.getBoolean("affiliation_required")) {
                        affiliationRequired.add(level);
                    }
                    return new LevelQualificationRule(
                            level,
                            new NetworkRequirement(
                                    resultSet.getInt("min_active_directs"),
                                    resultSet.getInt("min_indirects")),
                            new VolumeRequirement(Money.of(resultSet.getBigDecimal("min_team_volume"))));
                },
                versionId);
        return Optional.of(new VersionedQualificationRuleSet(
                versions.getFirst(), levels, affiliationRequired));
    }

    private BusinessRuleVersion mapVersion(ResultSet resultSet) throws SQLException {
        String confidence = resultSet.getString("provisional_confidence");
        return new BusinessRuleVersion(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("rule_set_id"),
                resultSet.getInt("version_number"),
                BusinessRuleStatus.valueOf(resultSet.getString("status")),
                resultSet.getTimestamp("effective_from").toInstant(),
                instant(resultSet.getTimestamp("effective_to")),
                resultSet.getString("source"),
                instant(resultSet.getTimestamp("approved_at")),
                resultSet.getString("approved_by"),
                confidence == null ? null : ProvisionalConfidence.valueOf(confidence),
                resultSet.getString("notes"));
    }

    private java.time.Instant instant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}

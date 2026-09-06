package com.visana.erp.qualification.application.port.out;

import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import java.util.Optional;
import java.util.UUID;

public interface QualificationRuleSetCatalog {
    Optional<VersionedQualificationRuleSet> findByVersionId(UUID versionId);
}

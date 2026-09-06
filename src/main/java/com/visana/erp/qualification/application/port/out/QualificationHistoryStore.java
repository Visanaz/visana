package com.visana.erp.qualification.application.port.out;

import com.visana.erp.qualification.domain.model.QualificationResult;
import com.visana.erp.qualification.domain.model.VersionedQualificationRuleSet;
import com.visana.erp.qualification.domain.volume.VolumeResult;

public interface QualificationHistoryStore {
    void appendRuleSet(VersionedQualificationRuleSet ruleSet);

    void appendVolumeResult(VolumeResult result);

    void appendQualificationResult(QualificationResult result);
}

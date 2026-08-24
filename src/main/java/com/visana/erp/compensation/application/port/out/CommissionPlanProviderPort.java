package com.visana.erp.compensation.application.port.out;

import com.visana.erp.core.domain.model.Percentage;
import java.util.Map;

public interface CommissionPlanProviderPort {
    Map<Integer, Percentage> getUnilevelPlan();
}

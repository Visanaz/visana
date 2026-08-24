package com.visana.erp.compensation.application.port.out;

import com.visana.erp.compensation.domain.model.Commission;
import java.util.List;

public interface CommissionRepository {
    void saveAll(List<Commission> commissions);
}

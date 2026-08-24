package com.visana.erp.compensation.application.port.in;

import com.visana.erp.commerce.application.dto.OrderPaidEvent;

public interface CalculateCommissionsUseCase {
    void execute(OrderPaidEvent event);
}

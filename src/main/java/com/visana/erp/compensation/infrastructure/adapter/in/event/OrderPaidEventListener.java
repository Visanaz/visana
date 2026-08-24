package com.visana.erp.compensation.infrastructure.adapter.in.event;

import com.visana.erp.commerce.application.dto.OrderPaidEvent;
import com.visana.erp.compensation.application.port.in.CalculateCommissionsUseCase;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPaidEventListener {

    private final CalculateCommissionsUseCase calculateCommissionsUseCase;

    public OrderPaidEventListener(CalculateCommissionsUseCase calculateCommissionsUseCase) {
        this.calculateCommissionsUseCase = calculateCommissionsUseCase;
    }

    @EventListener
    public void handleOrderPaidEvent(OrderPaidEvent event) {
        calculateCommissionsUseCase.execute(event);
    }
}

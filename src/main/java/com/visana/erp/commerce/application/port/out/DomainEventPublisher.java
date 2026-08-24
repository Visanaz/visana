package com.visana.erp.commerce.application.port.out;

import com.visana.erp.core.domain.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}

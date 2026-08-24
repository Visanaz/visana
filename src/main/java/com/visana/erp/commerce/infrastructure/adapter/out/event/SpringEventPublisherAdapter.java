package com.visana.erp.commerce.infrastructure.adapter.out.event;

import com.visana.erp.commerce.application.port.out.DomainEventPublisher;
import com.visana.erp.core.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisherAdapter implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderSpringDataRepository repository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderSpringDataRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return repository.findById(id.value().toString())
                .map(mapper::toDomainEntity);
    }

    @Override
    public void save(Order order) {
        OrderJpaEntity entity = mapper.toJpaEntity(order);
        repository.save(entity);
    }
}

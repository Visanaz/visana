package com.visana.erp.commerce.application.port.out;

import com.visana.erp.commerce.domain.model.Order;
import com.visana.erp.commerce.domain.model.OrderId;

import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    void save(Order order);
}

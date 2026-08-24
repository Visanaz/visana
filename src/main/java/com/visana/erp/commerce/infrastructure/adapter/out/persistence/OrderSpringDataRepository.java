package com.visana.erp.commerce.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderSpringDataRepository extends JpaRepository<OrderJpaEntity, String> {
}

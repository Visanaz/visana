package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommissionSpringDataRepository extends JpaRepository<CommissionJpaEntity, UUID> {
}

package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommissionSpringDataRepository extends JpaRepository<CommissionJpaEntity, String> {
}

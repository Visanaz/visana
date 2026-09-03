package com.visana.erp.platform.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface AuditEventSpringDataRepository extends JpaRepository<AuditEventJpaEntity, UUID> { }

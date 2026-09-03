package com.visana.erp.commerce.order.infrastructure.persistence;
import java.util.UUID;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable; import org.springframework.data.jpa.repository.JpaRepository;
public interface OwnedOrderSpringDataRepository extends JpaRepository<OwnedOrderJpaEntity, UUID> { Page<OwnedOrderJpaEntity> findByOwnerActorId(UUID ownerActorId, Pageable pageable); }

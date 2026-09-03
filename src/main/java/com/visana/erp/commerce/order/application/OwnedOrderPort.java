package com.visana.erp.commerce.order.application;
import com.visana.erp.commerce.order.domain.OwnedOrder;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface OwnedOrderPort { OwnedOrder save(OwnedOrder order); Optional<OwnedOrder> findById(UUID id); Page<OwnedOrder> findByOwnerActorId(UUID actorId, Pageable pageable); }

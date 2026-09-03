package com.visana.erp.network.foundation.infrastructure.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessProfileActorLinkRepository extends JpaRepository<BusinessProfileActorLinkJpaEntity,UUID>{ Optional<BusinessProfileActorLinkJpaEntity> findByActorId(UUID actorId); }

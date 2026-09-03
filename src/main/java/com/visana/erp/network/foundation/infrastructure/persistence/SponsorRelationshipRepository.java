package com.visana.erp.network.foundation.infrastructure.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface SponsorRelationshipRepository extends JpaRepository<SponsorRelationshipJpaEntity,UUID>{ boolean existsByMemberId(UUID memberId); }

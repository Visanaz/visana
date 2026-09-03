package com.visana.erp.network.foundation.infrastructure.persistence;
import java.util.*; import org.springframework.data.jpa.repository.JpaRepository;
public interface NetworkMemberRepository extends JpaRepository<NetworkMemberJpaEntity,UUID>{ Optional<NetworkMemberJpaEntity> findByBusinessProfileId(UUID profileId); }

package com.visana.erp.compensation.infrastructure.adapter.out.persistence;

import com.visana.erp.compensation.application.port.out.CommissionRepository;
import com.visana.erp.compensation.domain.model.Commission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommissionPersistenceAdapter implements CommissionRepository {

    private final CommissionSpringDataRepository repository;
    private final CommissionMapper mapper;

    public CommissionPersistenceAdapter(CommissionSpringDataRepository repository, CommissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<Commission> commissions) {
        List<CommissionJpaEntity> entities = commissions.stream()
                .map(mapper::toJpaEntity)
                .collect(Collectors.toList());
        repository.saveAll(entities);
    }
}

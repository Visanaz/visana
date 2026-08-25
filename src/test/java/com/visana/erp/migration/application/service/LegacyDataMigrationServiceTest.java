package com.visana.erp.migration.application.service;

import com.visana.erp.commerce.application.port.out.OrderRepository;
import com.visana.erp.migration.application.dto.LegacyOrderDto;
import com.visana.erp.migration.application.dto.LegacyUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyDataMigrationServiceTest {

    private OrderRepository orderRepository;
    private LegacyDataMigrationService migrationService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        migrationService = new LegacyDataMigrationService(orderRepository);
    }

    @Test
    void shouldMigrateValidUser() {
        LegacyUserDto user = new LegacyUserDto(
                UUID.randomUUID(), UUID.randomUUID(), null, "AFFILIATE", "ACTIVE"
        );
        migrationService.migrateUser(user);
        assertTrue(migrationService.getConciliationReport().isEmpty());
    }

    @Test
    void shouldReportErrorForCorruptUser() {
        LegacyUserDto user = new LegacyUserDto(
                UUID.randomUUID(), UUID.randomUUID(), null, "INVALID_ROLE", "ACTIVO"
        );
        migrationService.migrateUser(user);
        
        assertEquals(1, migrationService.getConciliationReport().size());
        assertTrue(migrationService.getConciliationReport().get(0).contains("INVALID_ROLE"));
    }

    @Test
    void shouldMigrateValidOrder() {
        LegacyOrderDto order = new LegacyOrderDto(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "PURCHASE", "PENDING", BigDecimal.valueOf(100)
        );
        migrationService.migrateOrder(order);
        assertTrue(migrationService.getConciliationReport().isEmpty());
    }

    @Test
    void shouldReportErrorForCorruptOrder() {
        LegacyOrderDto order = new LegacyOrderDto(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "INVALID_TYPE", "PAID", BigDecimal.valueOf(100)
        );
        migrationService.migrateOrder(order);
        
        assertEquals(1, migrationService.getConciliationReport().size());
        assertTrue(migrationService.getConciliationReport().get(0).contains("INVALID_TYPE"));
    }
}

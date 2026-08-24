package com.visana.erp.qualification.domain.model;

import com.visana.erp.core.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LevelQualificationRuleTest {

    @Test
    void shouldCreateRule() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertEquals(3, rule.getLevel());
        assertEquals(networkReq, rule.getNetworkRequirement());
        assertEquals(volumeReq, rule.getVolumeRequirement());
    }

    @Test
    void shouldThrowExceptionWhenLevelIsLessThanOne() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new LevelQualificationRule(0, networkReq, volumeReq);
        });
        
        assertTrue(exception.getMessage().contains("Level must be greater than or equal to 1"));
    }
    
    @Test
    void shouldBeSatisfiedWhenMetricsExceedRequirements() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertTrue(rule.isSatisfiedBy(6, 12, Money.of(new BigDecimal("1500.00"))));
    }
    
    @Test
    void shouldBeSatisfiedWhenMetricsExactlyMeetRequirements() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertTrue(rule.isSatisfiedBy(5, 10, Money.of(new BigDecimal("1000.00"))));
    }
    
    @Test
    void shouldNotBeSatisfiedWhenDirectsAreTooLow() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertFalse(rule.isSatisfiedBy(4, 15, Money.of(new BigDecimal("1500.00"))));
    }
    
    @Test
    void shouldNotBeSatisfiedWhenIndirectsAreTooLow() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertFalse(rule.isSatisfiedBy(6, 9, Money.of(new BigDecimal("1500.00"))));
    }
    
    @Test
    void shouldNotBeSatisfiedWhenVolumeIsTooLow() {
        NetworkRequirement networkReq = new NetworkRequirement(5, 10);
        VolumeRequirement volumeReq = new VolumeRequirement(Money.of(new BigDecimal("1000.00")));
        LevelQualificationRule rule = new LevelQualificationRule(3, networkReq, volumeReq);
        
        assertFalse(rule.isSatisfiedBy(6, 15, Money.of(new BigDecimal("999.99"))));
    }
}

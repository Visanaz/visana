package com.visana.erp.qualification.domain.service;

import com.visana.erp.core.domain.model.Money;
import com.visana.erp.qualification.domain.model.AffiliateMetrics;
import com.visana.erp.qualification.domain.model.LevelQualificationRule;
import com.visana.erp.qualification.domain.model.NetworkRequirement;
import com.visana.erp.qualification.domain.model.VolumeRequirement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QualificationEvaluatorServiceTest {

    private final QualificationEvaluatorService service = new QualificationEvaluatorService();

    @Test
    void shouldReturnMaxLevelSatisfied() {
        List<LevelQualificationRule> rules = List.of(
                createRule(1, 0, 0, "0.00"),
                createRule(2, 2, 0, "500.00"),
                createRule(3, 5, 5, "1000.00"),
                createRule(4, 10, 20, "5000.00")
        );

        AffiliateMetrics metrics = new AffiliateMetrics(7, 10, Money.of(new BigDecimal("1500.00")));
        
        int maxLevel = service.evaluateMaxLevel(metrics, rules);

        assertEquals(3, maxLevel);
    }

    @Test
    void shouldReturnZeroIfNoRulesAreSatisfied() {
        List<LevelQualificationRule> rules = List.of(
                createRule(1, 2, 0, "500.00")
        );

        AffiliateMetrics metrics = new AffiliateMetrics(1, 0, Money.of(new BigDecimal("0.00")));
        
        int maxLevel = service.evaluateMaxLevel(metrics, rules);

        assertEquals(0, maxLevel);
    }
    
    @Test
    void shouldReturnZeroIfRulesListIsEmpty() {
        AffiliateMetrics metrics = new AffiliateMetrics(10, 10, Money.of(new BigDecimal("1000.00")));
        
        assertEquals(0, service.evaluateMaxLevel(metrics, List.of()));
        assertEquals(0, service.evaluateMaxLevel(metrics, null));
    }

    private LevelQualificationRule createRule(int level, int minDirects, int minIndirects, String minVolume) {
        return new LevelQualificationRule(
                level, 
                new NetworkRequirement(minDirects, minIndirects), 
                new VolumeRequirement(Money.of(new BigDecimal(minVolume)))
        );
    }
}

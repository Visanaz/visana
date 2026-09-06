package com.visana.erp.qualification.domain.activation;

import static com.visana.erp.qualification.support.QualificationTestFixtures.BUSINESS_ZONE;
import static com.visana.erp.qualification.support.QualificationTestFixtures.activationPolicy;
import static com.visana.erp.qualification.support.QualificationTestFixtures.provisionalRule;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.visana.erp.qualification.domain.rules.BusinessRuleVersion;
import com.visana.erp.qualification.domain.rules.RuleExecutionGuard;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ActivationCalculatorTest {
    private final ActivationCalculator calculator = new ActivationCalculator(new RuleExecutionGuard());

    @ParameterizedTest(name = "CALENDAR_MONTH_POLICY {0} -> {1}")
    @MethodSource("calendarMonthCases")
    void shouldApplyCalendarMonthArithmetic(LocalDateTime start, LocalDateTime expectedEnd) {
        ZonedDateTime result = new CalendarMonthActivationDuration(1)
                .expiryFrom(start.atZone(BUSINESS_ZONE));

        assertEquals(expectedEnd, result.toLocalDateTime());
    }

    @Test
    void shouldApplyTheEffectiveProvisionalRuleToTheExactSeptemberExample() {
        BusinessRuleVersion rule = provisionalRule(UUID.randomUUID(), 1);
        ZonedDateTime localStart = ZonedDateTime.of(2026, 9, 5, 14, 35, 0, 0, BUSINESS_ZONE);

        ActivationWindow result = calculator.calculate(localStart.toInstant(), activationPolicy(rule));

        assertEquals(LocalDateTime.of(2026, 10, 5, 14, 35),
                result.endExclusive().atZone(BUSINESS_ZONE).toLocalDateTime());
        assertEquals(localStart.toInstant(), result.startInclusive());
        assertEquals(rule.id(), result.businessRuleVersionId());
    }

    static Stream<Arguments> calendarMonthCases() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2026, 9, 5, 14, 35), LocalDateTime.of(2026, 10, 5, 14, 35)),
                Arguments.of(LocalDateTime.of(2026, 1, 31, 8, 0), LocalDateTime.of(2026, 2, 28, 8, 0)),
                Arguments.of(LocalDateTime.of(2028, 1, 31, 8, 0), LocalDateTime.of(2028, 2, 29, 8, 0)),
                Arguments.of(LocalDateTime.of(2028, 2, 29, 8, 0), LocalDateTime.of(2028, 3, 29, 8, 0)),
                Arguments.of(LocalDateTime.of(2027, 2, 28, 8, 0), LocalDateTime.of(2027, 3, 28, 8, 0)),
                Arguments.of(LocalDateTime.of(2026, 12, 31, 8, 0), LocalDateTime.of(2027, 1, 31, 8, 0)));
    }

    @Test
    void shouldPreserveLocalTimeAcrossTimezoneOffsetBoundary() {
        ZoneId newYork = ZoneId.of("America/New_York");
        ZonedDateTime localStart = ZonedDateTime.of(2026, 2, 15, 14, 35, 0, 0, newYork);

        ZonedDateTime result = new CalendarMonthActivationDuration(1).expiryFrom(localStart);

        assertEquals(LocalDateTime.of(2026, 3, 15, 14, 35),
                result.toLocalDateTime());
    }

    @Test
    void shouldRequirePolicyDecisionForOverlappingRepurchase() {
        BusinessRuleVersion rule = provisionalRule(UUID.randomUUID(), 1);
        Instant payment = Instant.parse("2026-09-05T19:35:00Z");

        assertThrows(IllegalStateException.class, () -> calculator.calculate(
                payment, Optional.of(Instant.parse("2026-09-20T19:35:00Z")), activationPolicy(rule)));
    }

    @Test
    void shouldReferenceTheVersionThatDefinedEachActivationCalculation() {
        Instant payment = Instant.parse("2026-09-05T19:35:00Z");
        BusinessRuleVersion versionA = provisionalRule(UUID.randomUUID(), 1);
        BusinessRuleVersion hypotheticalVersionB = provisionalRule(UUID.randomUUID(), 2);
        ActivationPolicy hypotheticalPolicyB = new ActivationPolicy(
                hypotheticalVersionB, ActivationTrigger.PAYMENT_CONFIRMED,
                new CalendarMonthActivationDuration(2), BUSINESS_ZONE,
                ExpirySemantics.EXACT_ANNIVERSARY_TIMESTAMP,
                RepurchaseStartPolicy.PAYMENT_CONFIRMATION);

        ActivationWindow resultA = calculator.calculate(payment, activationPolicy(versionA));
        ActivationWindow resultB = calculator.calculate(payment, hypotheticalPolicyB);

        assertEquals(versionA.id(), resultA.businessRuleVersionId());
        assertEquals(hypotheticalVersionB.id(), resultB.businessRuleVersionId());
        assertEquals(LocalDateTime.of(2026, 11, 5, 14, 35),
                resultB.endExclusive().atZone(BUSINESS_ZONE).toLocalDateTime());
    }

}

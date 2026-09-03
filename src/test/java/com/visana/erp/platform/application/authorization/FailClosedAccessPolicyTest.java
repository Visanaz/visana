package com.visana.erp.platform.application.authorization;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailClosedAccessPolicyTest {
    private final FailClosedAccessPolicy policy = new FailClosedAccessPolicy();

    @Test void deniesWhenRelationshipIsUnknown() { assertFalse(policy.isAllowed(Optional.empty())); }
    @Test void deniesAnExplicitDeniedFixture() { assertFalse(policy.isAllowed(Optional.of(false))); }
    @Test void allowsOnlyAnExplicitAllowedFixture() { assertTrue(policy.isAllowed(Optional.of(true))); }
}

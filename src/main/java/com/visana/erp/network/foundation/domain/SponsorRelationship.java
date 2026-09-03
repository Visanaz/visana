package com.visana.erp.network.foundation.domain;
import java.time.Instant; import java.util.UUID;
public record SponsorRelationship(UUID id, UUID sponsorMemberId, UUID memberId, Instant effectiveFrom, Instant createdAt) { public SponsorRelationship { if(sponsorMemberId.equals(memberId)) throw new IllegalArgumentException("a member cannot sponsor itself"); } }

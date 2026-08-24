package com.visana.erp.qualification.domain.model;

public record NetworkRequirement(int minDirects, int minIndirects) {
    public NetworkRequirement {
        if (minDirects < 0) {
            throw new IllegalArgumentException("Minimum directs cannot be negative");
        }
        if (minIndirects < 0) {
            throw new IllegalArgumentException("Minimum indirects cannot be negative");
        }
    }
}

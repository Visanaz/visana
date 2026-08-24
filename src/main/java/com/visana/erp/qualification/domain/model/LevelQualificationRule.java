package com.visana.erp.qualification.domain.model;

import java.util.Objects;

public class LevelQualificationRule {
    private final int level;
    private final NetworkRequirement networkRequirement;
    private final VolumeRequirement volumeRequirement;

    public LevelQualificationRule(int level, NetworkRequirement networkRequirement, VolumeRequirement volumeRequirement) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be greater than or equal to 1");
        }
        this.level = level;
        this.networkRequirement = Objects.requireNonNull(networkRequirement, "Network requirement cannot be null");
        this.volumeRequirement = Objects.requireNonNull(volumeRequirement, "Volume requirement cannot be null");
    }

    public int getLevel() {
        return level;
    }

    public NetworkRequirement getNetworkRequirement() {
        return networkRequirement;
    }

    public VolumeRequirement getVolumeRequirement() {
        return volumeRequirement;
    }

    public boolean isSatisfiedBy(int actualDirects, int actualIndirects, com.visana.erp.core.domain.model.Money actualTeamSales) {
        if (actualDirects < networkRequirement.minDirects()) {
            return false;
        }
        if (actualIndirects < networkRequirement.minIndirects()) {
            return false;
        }
        if (actualTeamSales.amount().compareTo(volumeRequirement.minTeamSales().amount()) < 0) {
            return false;
        }
        return true;
    }
}

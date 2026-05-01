package com.blockforge.common.progression;

public class ArchitectProgressionService {
    public int levelForReputation(int reputation) {
        if (reputation >= 1000) {
            return 5;
        }
        if (reputation >= 500) {
            return 4;
        }
        if (reputation >= 250) {
            return 3;
        }
        if (reputation >= 100) {
            return 2;
        }
        return 1;
    }

    public ArchitectProfile recalculate(ArchitectProfile profile) {
        int level = levelForReputation(profile.reputation());
        return new ArchitectProfile(profile.playerId(), profile.reputation(), profile.experience(), level, UnlockRegistry.unlocksForLevel(level), profile.completedContractIds());
    }
}

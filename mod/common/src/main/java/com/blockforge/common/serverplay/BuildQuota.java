package com.blockforge.common.serverplay;

public record BuildQuota(
        String playerId,
        int maxBlocksPerDay,
        int usedBlocksToday,
        int maxActiveJobs,
        int activeJobs,
        long resetAtEpochMillis
) {
    public BuildQuota {
        playerId = playerId == null ? "" : playerId;
        maxBlocksPerDay = Math.max(0, maxBlocksPerDay);
        usedBlocksToday = Math.max(0, usedBlocksToday);
        maxActiveJobs = Math.max(0, maxActiveJobs);
        activeJobs = Math.max(0, activeJobs);
    }

    public boolean canReserveBlocks(int blocks) {
        return usedBlocksToday + Math.max(0, blocks) <= maxBlocksPerDay;
    }

    public boolean canStartJob() {
        return activeJobs < maxActiveJobs;
    }
}

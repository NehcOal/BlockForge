package com.blockforge.common.settlement.emergency;

public record EmergencyRepairRequest(
        String repairId,
        String settlementId,
        String eventId,
        String targetBlueprintId,
        String dimensionId,
        int baseX,
        int baseY,
        int baseZ,
        int missingBlocks,
        int wrongBlocks,
        int requiredCompletionPercent,
        long expiresAtGameTime
) {
    public EmergencyRepairRequest {
        if (repairId == null || repairId.isBlank()) {
            throw new IllegalArgumentException("repairId is required");
        }
        if (settlementId == null || settlementId.isBlank()) {
            throw new IllegalArgumentException("settlementId is required");
        }
        eventId = eventId == null ? "" : eventId;
        targetBlueprintId = targetBlueprintId == null ? "" : targetBlueprintId;
        dimensionId = dimensionId == null ? "minecraft:overworld" : dimensionId;
        missingBlocks = Math.max(0, missingBlocks);
        wrongBlocks = Math.max(0, wrongBlocks);
        requiredCompletionPercent = Math.max(1, Math.min(100, requiredCompletionPercent));
    }

    public boolean expired(long gameTime) {
        return expiresAtGameTime > 0 && gameTime >= expiresAtGameTime;
    }
}

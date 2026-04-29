package com.blockforge.common.serverplay;

import java.util.List;

public record BuildAuditEntry(
        String auditId,
        String jobId,
        String playerId,
        String playerName,
        String action,
        String blueprintId,
        String dimensionId,
        int baseX,
        int baseY,
        int baseZ,
        int placedBlocks,
        int consumedItems,
        String sourceType,
        String status,
        List<String> warnings,
        long gameTime,
        String createdAtIso
) {
    public BuildAuditEntry {
        auditId = auditId == null ? "" : auditId;
        jobId = jobId == null ? "" : jobId;
        playerId = playerId == null ? "" : playerId;
        playerName = playerName == null ? "" : playerName;
        action = action == null ? "unknown" : action;
        blueprintId = blueprintId == null ? "" : blueprintId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        placedBlocks = Math.max(0, placedBlocks);
        consumedItems = Math.max(0, consumedItems);
        sourceType = sourceType == null ? "" : sourceType;
        status = status == null ? "unknown" : status;
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
        createdAtIso = createdAtIso == null ? "" : createdAtIso;
    }
}

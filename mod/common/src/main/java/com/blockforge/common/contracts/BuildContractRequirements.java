package com.blockforge.common.contracts;

import java.util.List;

public record BuildContractRequirements(
        int minBlocks,
        int maxBlocks,
        int minWidth,
        int minHeight,
        int minDepth,
        int maxWidth,
        int maxHeight,
        int maxDepth,
        List<String> requiredBlockIds,
        List<String> bannedBlockIds,
        boolean requireDoor,
        boolean requireRoof,
        boolean requireWindows,
        boolean requireFoundation,
        boolean requireSurvivalMaterials,
        int requiredCompletionPercent
) {
    public BuildContractRequirements {
        if (minBlocks < 0 || maxBlocks < minBlocks) {
            throw new IllegalArgumentException("invalid block count range");
        }
        requiredBlockIds = requiredBlockIds == null ? List.of() : List.copyOf(requiredBlockIds);
        bannedBlockIds = bannedBlockIds == null ? List.of() : List.copyOf(bannedBlockIds);
        requiredCompletionPercent = Math.max(0, Math.min(100, requiredCompletionPercent));
    }
}

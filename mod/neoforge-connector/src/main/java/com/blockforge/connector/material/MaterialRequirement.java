package com.blockforge.connector.material;

public record MaterialRequirement(
        String blockStateKey,
        String blockId,
        String itemId,
        int required,
        int available,
        int missing
) {
    public MaterialRequirement withAvailability(int availableCount) {
        return new MaterialRequirement(
                blockStateKey,
                blockId,
                itemId,
                required,
                availableCount,
                Math.max(0, required - availableCount)
        );
    }

    public boolean consumable() {
        return itemId != null && !itemId.isBlank() && !"minecraft:air".equals(itemId);
    }
}

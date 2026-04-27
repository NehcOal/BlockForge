package com.blockforge.common.material.source;

public record MaterialSourceRef(
        MaterialSourceType type,
        String id,
        String displayName,
        String dimensionId,
        int x,
        int y,
        int z
) {
    public MaterialSourceRef(
            MaterialSourceType type,
            String id,
            String displayName,
            int x,
            int y,
            int z
    ) {
        this(type, id, displayName, "", x, y, z);
    }

    public MaterialSourceRef {
        type = type == null ? MaterialSourceType.PLAYER_INVENTORY : type;
        id = id == null ? "" : id;
        displayName = displayName == null ? id : displayName;
        dimensionId = dimensionId == null ? "" : dimensionId;
    }

    public static MaterialSourceRef playerInventory(String playerId, String playerName) {
        String resolvedId = playerId == null || playerId.isBlank() ? "player_inventory" : playerId;
        String resolvedName = playerName == null || playerName.isBlank() ? "Player Inventory" : playerName;
        return new MaterialSourceRef(MaterialSourceType.PLAYER_INVENTORY, resolvedId, resolvedName, "", 0, 0, 0);
    }
}

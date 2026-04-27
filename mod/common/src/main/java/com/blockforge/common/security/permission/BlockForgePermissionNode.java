package com.blockforge.common.security.permission;

public record BlockForgePermissionNode(
        String node,
        int fallbackOpLevel,
        boolean defaultSingleplayerAllowed,
        String description
) {
    public BlockForgePermissionNode {
        if (node == null || node.isBlank()) {
            throw new IllegalArgumentException("Permission node cannot be blank.");
        }
        if (fallbackOpLevel < 0) {
            throw new IllegalArgumentException("Fallback permission level cannot be negative.");
        }
        description = description == null ? "" : description;
    }
}

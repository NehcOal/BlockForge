package com.blockforge.common.security.permission;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class BlockForgePermissionNodes {
    private static final List<BlockForgePermissionNode> ALL = Arrays.stream(BlockForgePermissionAction.values())
            .map(BlockForgePermissionAction::node)
            .toList();

    private BlockForgePermissionNodes() {
    }

    public static List<BlockForgePermissionNode> all() {
        return ALL;
    }

    public static Optional<BlockForgePermissionNode> find(String node) {
        return ALL.stream().filter(candidate -> candidate.node().equals(node)).findFirst();
    }
}

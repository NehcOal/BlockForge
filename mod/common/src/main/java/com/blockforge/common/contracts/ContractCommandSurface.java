package com.blockforge.common.contracts;

import java.util.List;

public final class ContractCommandSurface {
    private ContractCommandSurface() {
    }

    public static List<String> settlementCommands() {
        return List.of("create", "info", "list", "members", "invite", "leave", "level", "contracts", "abandon");
    }

    public static List<String> contractCommands() {
        return List.of("list", "info", "accept", "active", "verify", "submit", "abandon", "refresh");
    }

    public static List<String> architectCommands() {
        return List.of("profile", "contracts", "reputation");
    }

    public static List<String> eventCommands() {
        return List.of("list", "info", "refresh", "resolve", "ignore");
    }

    public static List<String> projectCommands() {
        return List.of("list", "info", "activate", "status", "complete");
    }

    public static List<String> emergencyCommands() {
        return List.of("list", "info", "repair", "verify");
    }
}

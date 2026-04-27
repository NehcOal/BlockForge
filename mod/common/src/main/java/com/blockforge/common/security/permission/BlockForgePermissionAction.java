package com.blockforge.common.security.permission;

public enum BlockForgePermissionAction {
    COMMAND_GUI("blockforge.command.gui", 0, true, "Open the BlockForge blueprint selector."),
    COMMAND_RELOAD("blockforge.command.reload", 2, true, "Reload BlockForge blueprints and packs."),
    COMMAND_PACKS_RELOAD("blockforge.command.packs.reload", 2, true, "Reload BlockForge blueprint packs."),
    COMMAND_EXAMPLES_INSTALL("blockforge.command.examples.install", 2, true, "Install bundled BlockForge examples."),
    COMMAND_SOURCES_SCAN("blockforge.command.sources.scan", 0, true, "Scan nearby material sources."),
    COMMAND_SOURCES_USE_CONTAINERS("blockforge.command.sources.use_containers", 0, true, "Use nearby containers as material sources."),
    BUILD_COMMAND("blockforge.build.command", 2, true, "Build a blueprint from commands."),
    BUILD_WAND("blockforge.build.wand", 0, true, "Build a blueprint with the Builder Wand."),
    BUILD_BYPASS_LIMITS("blockforge.build.bypass_limits", 2, true, "Bypass BlockForge build safety limits."),
    BUILD_BYPASS_PROTECTION("blockforge.build.bypass_protection", 2, true, "Bypass BlockForge protection regions."),
    UNDO_SELF("blockforge.undo.self", 0, true, "Undo your own BlockForge builds."),
    UNDO_OTHERS("blockforge.undo.others", 2, true, "Undo another player's BlockForge builds."),
    ADMIN_CONFIG("blockforge.admin.config", 2, true, "Manage BlockForge configuration."),
    ADMIN_PROTECTION("blockforge.admin.protection", 2, true, "Manage BlockForge protection regions.");

    private final BlockForgePermissionNode node;

    BlockForgePermissionAction(String node, int fallbackOpLevel, boolean defaultSingleplayerAllowed, String description) {
        this.node = new BlockForgePermissionNode(node, fallbackOpLevel, defaultSingleplayerAllowed, description);
    }

    public BlockForgePermissionNode node() {
        return node;
    }
}

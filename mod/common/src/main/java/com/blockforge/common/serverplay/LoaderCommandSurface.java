package com.blockforge.common.serverplay;

import java.util.List;
import java.util.Map;

public record LoaderCommandSurface(
        String loader,
        Map<String, CommandSupportStatus> commands
) {
    public static final List<String> REQUIRED_V43_COMMANDS = List.of(
            "/blockforge cache list",
            "/blockforge cache info <id>",
            "/blockforge cache scan",
            "/blockforge station list",
            "/blockforge station info <id>",
            "/blockforge station status",
            "/blockforge station step",
            "/blockforge diagnostics export",
            "/blockforge quota get <player>",
            "/blockforge admin builds",
            "/blockforge admin audit"
    );

    public LoaderCommandSurface {
        loader = loader == null || loader.isBlank() ? "unknown" : loader;
        commands = commands == null ? Map.of() : Map.copyOf(commands);
    }

    public List<String> missingRequiredCommands() {
        return REQUIRED_V43_COMMANDS.stream()
                .filter(command -> !commands.containsKey(command))
                .toList();
    }

    public boolean completeEnoughForDraftPr() {
        return missingRequiredCommands().isEmpty();
    }
}

package com.blockforge.connector.serverplay;

import com.blockforge.common.serverplay.CommandSupportStatus;
import com.blockforge.common.serverplay.LoaderCommandSurface;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoaderCommandSurfaceTest {
    @Test
    void reportsMissingAdvancedCommands() {
        LoaderCommandSurface surface = new LoaderCommandSurface("forge", Map.of(
                "/blockforge cache list", CommandSupportStatus.ALPHA
        ));

        assertTrue(surface.missingRequiredCommands().contains("/blockforge station step"));
    }

    @Test
    void acceptsCompleteDraftCommandSurface() {
        LoaderCommandSurface surface = new LoaderCommandSurface(
                "fabric",
                LoaderCommandSurface.REQUIRED_V43_COMMANDS.stream()
                        .collect(Collectors.toMap(command -> command, command -> CommandSupportStatus.ALPHA))
        );

        assertEquals(0, surface.missingRequiredCommands().size());
        assertTrue(surface.completeEnoughForDraftPr());
    }
}

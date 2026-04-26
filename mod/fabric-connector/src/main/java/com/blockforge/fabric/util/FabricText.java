package com.blockforge.fabric.util;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class FabricText {
    private FabricText() {
    }

    public static Text literal(String message) {
        return Text.literal(message);
    }

    public static void success(ServerCommandSource source, String message) {
        source.sendFeedback(() -> literal(message), false);
    }

    public static void broadcastSuccess(ServerCommandSource source, String message) {
        source.sendFeedback(() -> literal(message), true);
    }

    public static void failure(ServerCommandSource source, String message) {
        source.sendError(literal(message));
    }
}

package com.blockforge.forge.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public final class ForgeText {
    private ForgeText() {
    }

    public static void info(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }

    public static void broadcast(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), true);
    }

    public static void error(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message));
    }
}

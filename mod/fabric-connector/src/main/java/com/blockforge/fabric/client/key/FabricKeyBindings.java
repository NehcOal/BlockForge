package com.blockforge.fabric.client.key;

import com.blockforge.fabric.client.gui.FabricBlueprintSelectorScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class FabricKeyBindings {
    private static KeyBinding openBlueprintSelector;

    private FabricKeyBindings() {
    }

    public static void register() {
        openBlueprintSelector = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.blockforge_connector.open_blueprint_selector",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.categories.blockforge_connector"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBlueprintSelector.wasPressed()) {
                FabricBlueprintSelectorScreen.openAndRequestList();
            }
        });
    }
}

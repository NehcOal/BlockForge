package com.blockforge.forge.client.key;

import com.blockforge.forge.BlockForgeForge;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = BlockForgeForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeKeyMappings {
    public static final KeyMapping OPEN_BLUEPRINT_SELECTOR = new KeyMapping(
            "key.blockforge_connector.open_blueprint_selector",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.blockforge_connector"
    );

    private ForgeKeyMappings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BLUEPRINT_SELECTOR);
    }
}

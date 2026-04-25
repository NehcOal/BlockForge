package com.blockforge.connector.client;

import com.blockforge.connector.BlockForgeConnector;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(
        modid = BlockForgeConnector.MOD_ID,
        value = Dist.CLIENT,
        bus = EventBusSubscriber.Bus.MOD
)
public final class BlockForgeClientModEvents {
    public static final KeyMapping OPEN_BLUEPRINT_SELECTOR = new KeyMapping(
            "key.blockforge_connector.open_blueprint_selector",
            InputConstants.KEY_B,
            "key.categories.blockforge_connector"
    );

    private BlockForgeClientModEvents() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BLUEPRINT_SELECTOR);
    }
}

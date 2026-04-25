package com.blockforge.connector.client;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.client.gui.BlueprintSelectorScreen;
import com.blockforge.connector.client.preview.GhostPreviewController;
import com.blockforge.connector.client.preview.GhostPreviewRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(
        modid = BlockForgeConnector.MOD_ID,
        value = Dist.CLIENT
)
public final class BlockForgeConnectorClient {
    private BlockForgeConnectorClient() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        GhostPreviewController.update();
    }

    @SubscribeEvent
    public static void onKey(InputEvent.Key event) {
        while (BlockForgeClientModEvents.OPEN_BLUEPRINT_SELECTOR.consumeClick()) {
            BlueprintSelectorScreen.openAndRequestList();
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        GhostPreviewRenderer.render(event);
    }
}

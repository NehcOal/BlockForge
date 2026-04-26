package com.blockforge.forge.client;

import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.client.gui.ForgeBlueprintSelectorScreen;
import com.blockforge.forge.client.key.ForgeKeyMappings;
import com.blockforge.forge.client.preview.ForgeGhostPreviewController;
import com.blockforge.forge.client.preview.ForgeGhostPreviewRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockForgeForge.MOD_ID, value = Dist.CLIENT)
public final class BlockForgeForgeClient {
    private BlockForgeForgeClient() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        ForgeGhostPreviewController.update();
        while (ForgeKeyMappings.OPEN_BLUEPRINT_SELECTOR.consumeClick()) {
            ForgeBlueprintSelectorScreen.openAndRequestList();
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        ForgeGhostPreviewRenderer.render(event);
    }
}

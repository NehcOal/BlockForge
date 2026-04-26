package com.blockforge.fabric.client;

import com.blockforge.fabric.client.gui.FabricBlueprintClientCache;
import com.blockforge.fabric.client.gui.FabricBlueprintSelectorScreen;
import com.blockforge.fabric.client.key.FabricKeyBindings;
import com.blockforge.fabric.client.preview.FabricClientPreviewState;
import com.blockforge.fabric.client.preview.FabricGhostPreviewController;
import com.blockforge.fabric.client.preview.FabricGhostPreviewRenderer;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class BlockForgeFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricKeyBindings.register();
        FabricGhostPreviewController.register();
        FabricGhostPreviewRenderer.register();
        registerClientNetworking();
    }

    private static void registerClientNetworking() {
        ClientPlayNetworking.registerGlobalReceiver(
                FabricBlueprintGuiNetworking.BLUEPRINT_LIST_ID,
                (payload, context) -> context.client().execute(() -> {
                    FabricBlueprintClientCache.apply(payload.view());
                    if (payload.openScreen()) {
                        MinecraftClient.getInstance().setScreen(new FabricBlueprintSelectorScreen());
                    } else {
                        FabricBlueprintSelectorScreen.refreshOpenScreen();
                    }
                })
        );
        ClientPlayNetworking.registerGlobalReceiver(
                FabricBlueprintGuiNetworking.SELECTION_RESULT_ID,
                (payload, context) -> context.client().execute(() -> {
                    FabricBlueprintClientCache.setMessage(payload.message());
                    if (payload.success()) {
                        FabricBlueprintClientCache.setSelected(payload.selectedBlueprintId(), payload.rotationDegrees());
                    }
                    FabricBlueprintSelectorScreen.refreshOpenScreen();
                })
        );
        ClientPlayNetworking.registerGlobalReceiver(
                FabricBlueprintGuiNetworking.PREVIEW_SELECTION_ID,
                (payload, context) -> context.client().execute(() -> FabricClientPreviewState.apply(payload))
        );
        ClientPlayNetworking.registerGlobalReceiver(
                FabricBlueprintGuiNetworking.CLEAR_PREVIEW_ID,
                (payload, context) -> context.client().execute(() -> {
                    FabricClientPreviewState.clear();
                    FabricBlueprintClientCache.setMessage(payload.reason());
                    FabricBlueprintSelectorScreen.refreshOpenScreen();
                })
        );
    }
}

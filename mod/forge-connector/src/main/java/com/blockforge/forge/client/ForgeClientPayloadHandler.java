package com.blockforge.forge.client;

import com.blockforge.forge.client.gui.ForgeBlueprintClientCache;
import com.blockforge.forge.client.gui.ForgeBlueprintSelectorScreen;
import com.blockforge.forge.network.ForgeBlueprintGuiNetworking;
import net.minecraft.client.Minecraft;

public final class ForgeClientPayloadHandler {
    private ForgeClientPayloadHandler() {
    }

    public static void handleBlueprintList(ForgeBlueprintGuiNetworking.BlueprintListPayload payload) {
        ForgeBlueprintClientCache.apply(payload.view());
        if (payload.openScreen()) {
            Minecraft.getInstance().setScreen(new ForgeBlueprintSelectorScreen());
        } else {
            ForgeBlueprintSelectorScreen.refreshOpenScreen();
        }
    }

    public static void handleSelectionResult(ForgeBlueprintGuiNetworking.SelectionResultPayload payload) {
        ForgeBlueprintClientCache.setMessage(payload.message());
        if (payload.success()) {
            ForgeBlueprintClientCache.setSelected(payload.selectedBlueprintId(), payload.rotationDegrees());
        }
        ForgeBlueprintSelectorScreen.refreshOpenScreen();
    }
}

package com.blockforge.connector.client;

import com.blockforge.connector.client.gui.BlueprintClientCache;
import com.blockforge.connector.client.gui.BlueprintSelectorScreen;
import com.blockforge.connector.client.preview.ClientPreviewState;
import com.blockforge.connector.network.payload.BlueprintListPayload;
import com.blockforge.connector.network.payload.ClearPreviewPayload;
import com.blockforge.connector.network.payload.SelectedBlueprintPayload;
import net.minecraft.client.Minecraft;

public final class ClientPayloadHandler {
    private ClientPayloadHandler() {
    }

    public static void handleSelectedBlueprint(SelectedBlueprintPayload payload) {
        ClientPreviewState.apply(payload);
        BlueprintClientCache.setSelected(payload.blueprintId(), payload.rotation());
        BlueprintSelectorScreen.refreshOpenScreen();
    }

    public static void handleClearPreview(ClearPreviewPayload payload) {
        ClientPreviewState.clearSelection();
        BlueprintClientCache.setError(payload.reason());
        BlueprintSelectorScreen.refreshOpenScreen();
    }

    public static void handleBlueprintList(BlueprintListPayload payload) {
        BlueprintClientCache.setBlueprints(payload.blueprints());
        BlueprintSelectorScreen.refreshOpenScreen();

        if (payload.openScreen()) {
            Minecraft.getInstance().setScreen(new BlueprintSelectorScreen());
        }
    }
}

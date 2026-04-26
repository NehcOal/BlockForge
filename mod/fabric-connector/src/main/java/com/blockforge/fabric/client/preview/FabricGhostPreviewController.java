package com.blockforge.fabric.client.preview;

import com.blockforge.common.preview.PreviewState;
import com.blockforge.fabric.registry.FabricModItems;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public final class FabricGhostPreviewController {
    private FabricGhostPreviewController() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> update());
    }

    public static void update() {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ClientPlayerEntity player = minecraft.player;
        ClientWorld world = minecraft.world;
        PreviewState selection = FabricClientPreviewState.selection();

        if (player == null || world == null || selection == null) {
            FabricClientPreviewState.hideTarget();
            return;
        }

        if (!isHoldingBuilderWand(player.getMainHandStack()) && !isHoldingBuilderWand(player.getOffHandStack())) {
            FabricClientPreviewState.hideTarget();
            return;
        }

        if (!(minecraft.crosshairTarget instanceof BlockHitResult hitResult)
                || hitResult.getType() != HitResult.Type.BLOCK) {
            FabricClientPreviewState.hideTarget();
            return;
        }

        BlockPos basePos = hitResult.getBlockPos().offset(hitResult.getSide());
        boolean valid = selection.width() > 0
                && selection.height() > 0
                && selection.depth() > 0
                && basePos.getY() >= world.getBottomY()
                && basePos.getY() + selection.height() <= world.getTopY();

        FabricClientPreviewState.setTarget(basePos, valid, world.getTime());
    }

    private static boolean isHoldingBuilderWand(ItemStack stack) {
        return stack.isOf(FabricModItems.BUILDER_WAND);
    }
}

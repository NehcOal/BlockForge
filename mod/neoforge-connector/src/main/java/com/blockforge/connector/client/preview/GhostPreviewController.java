package com.blockforge.connector.client.preview;

import com.blockforge.connector.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class GhostPreviewController {
    private GhostPreviewController() {
    }

    public static void update() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;

        if (player == null || level == null || !ClientPreviewState.hasSelection()) {
            ClientPreviewState.hideTarget();
            return;
        }

        if (!isHoldingBuilderWand(player.getMainHandItem()) && !isHoldingBuilderWand(player.getOffhandItem())) {
            ClientPreviewState.hideTarget();
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult hitResult)
                || hitResult.getType() != HitResult.Type.BLOCK) {
            ClientPreviewState.hideTarget();
            return;
        }

        BlockPos basePos = hitResult.getBlockPos().relative(hitResult.getDirection());
        ClientPreviewState.SelectedBlueprint selected = ClientPreviewState.selectedBlueprint();
        boolean valid = selected.width() > 0
                && selected.height() > 0
                && selected.depth() > 0
                && basePos.getY() >= level.getMinBuildHeight()
                && basePos.getY() + selected.height() <= level.getMaxBuildHeight();

        ClientPreviewState.setTarget(basePos, valid);
    }

    private static boolean isHoldingBuilderWand(ItemStack stack) {
        return stack.is(ModItems.BUILDER_WAND.get());
    }
}

package com.blockforge.forge.client.preview;

import com.blockforge.common.preview.PreviewState;
import com.blockforge.forge.registry.ForgeModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ForgeGhostPreviewController {
    private ForgeGhostPreviewController() {
    }

    public static void update() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        PreviewState selection = ForgeClientPreviewState.selection();

        if (player == null || level == null || selection == null) {
            ForgeClientPreviewState.hideTarget();
            return;
        }

        if (!isHoldingBuilderWand(player.getMainHandItem()) && !isHoldingBuilderWand(player.getOffhandItem())) {
            ForgeClientPreviewState.hideTarget();
            return;
        }

        if (!(minecraft.hitResult instanceof BlockHitResult hitResult)
                || hitResult.getType() != HitResult.Type.BLOCK) {
            ForgeClientPreviewState.hideTarget();
            return;
        }

        BlockPos basePos = hitResult.getBlockPos().relative(hitResult.getDirection());
        boolean valid = selection.width() > 0
                && selection.height() > 0
                && selection.depth() > 0
                && basePos.getY() >= level.getMinBuildHeight()
                && basePos.getY() + selection.height() <= level.getMaxBuildHeight();

        ForgeClientPreviewState.setTarget(basePos, valid, level.getGameTime());
    }

    private static boolean isHoldingBuilderWand(ItemStack stack) {
        return stack.is(ForgeModItems.BUILDER_WAND.get());
    }
}

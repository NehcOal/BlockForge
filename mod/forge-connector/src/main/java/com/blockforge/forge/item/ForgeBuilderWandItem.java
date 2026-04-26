package com.blockforge.forge.item;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.builder.ForgeBlueprintPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ForgeBuilderWandItem extends Item {
    private static final ForgeBlueprintPlacer PLACER = new ForgeBlueprintPlacer();

    public ForgeBuilderWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(context.getPlayer() instanceof ServerPlayer player)) {
            return InteractionResult.FAIL;
        }

        PlayerSelection selection = BlockForgeForge.SELECTIONS.get(player.getUUID()).orElse(null);
        if (selection == null) {
            player.sendSystemMessage(Component.literal("Use /blockforge select <id> first."));
            return InteractionResult.FAIL;
        }

        long now = System.currentTimeMillis();
        if (BlockForgeForge.SELECTIONS.isCoolingDown(selection, now)) {
            long remainingMillis = BlockForgeForge.SELECTIONS.remainingCooldownMillis(selection, now);
            player.sendSystemMessage(Component.literal("BlockForge Builder Wand cooldown: "
                    + Math.max(1, (long) Math.ceil(remainingMillis / 1000.0))
                    + "s remaining."));
            return InteractionResult.FAIL;
        }

        Blueprint blueprint = BlockForgeForge.BLUEPRINTS.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            player.sendSystemMessage(Component.literal("Unknown BlockForge Forge blueprint id: "
                    + selection.selectedBlueprintId()
                    + ". Run /blockforge reload or select another blueprint."));
            return InteractionResult.FAIL;
        }

        ServerLevel level = (ServerLevel) context.getLevel();
        BlockPos basePos = context.getClickedPos().relative(context.getClickedFace());
        ForgeBlueprintPlacer.PlacementResult result = PLACER.place(
                level,
                player,
                basePos,
                blueprint,
                selection.rotation()
        );

        if (result.snapshot() != null) {
            BlockForgeForge.UNDO.record(result.snapshot());
        }

        sendPlacementResult(player, result);

        if (result.placedBlocks() > 0) {
            BlockForgeForge.SELECTIONS.markBuilt(selection, now);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }

    private static void sendPlacementResult(
            ServerPlayer player,
            ForgeBlueprintPlacer.PlacementResult result
    ) {
        if (result.tooLarge()) {
            player.sendSystemMessage(Component.literal("Blueprint has "
                    + result.totalBlocks()
                    + " blocks, which exceeds the "
                    + result.maxBlocks()
                    + " block safety limit."));
            return;
        }

        if (result.empty()) {
            player.sendSystemMessage(Component.literal("Blueprint has no blocks and cannot be built."));
            return;
        }

        player.sendSystemMessage(Component.literal("BlockForge Forge build complete: placed "
                + result.placedBlocks()
                + " blocks. skipped: missingPalette="
                + result.skippedMissingPalette()
                + ", invalidBlockId="
                + result.skippedInvalidBlockIds()
                + ", invalidProperties="
                + result.skippedInvalidProperties()
                + ", outOfWorld="
                + result.skippedOutOfWorld()
                + ". appliedProperties="
                + result.appliedProperties()
                + ". totalBlocks="
                + result.totalBlocks()
                + ". Use /blockforge undo to revert blocks."));
    }
}

package com.blockforge.connector.item;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.build.BuildService;
import com.blockforge.connector.material.MaterialRequirement;
import com.blockforge.connector.player.PlayerBlueprintSelection;
import com.blockforge.connector.player.PlayerSelectionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class BuilderWandItem extends Item {
    private static final BuildService BUILDS = new BuildService(BlockForgeConnector.UNDO);

    public BuilderWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer player)) {
            return InteractionResult.PASS;
        }

        if (!player.hasPermissions(2)) {
            player.sendSystemMessage(Component.literal("BlockForge Builder Wand requires permission level 2."));
            return InteractionResult.FAIL;
        }

        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());

        if (!selection.hasSelection()) {
            player.sendSystemMessage(Component.literal("No BlockForge blueprint selected. Run /blockforge select <id> first."));
            return InteractionResult.FAIL;
        }

        Blueprint blueprint = BlockForgeConnector.BLUEPRINTS.get(selection.getSelectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            player.sendSystemMessage(Component.literal("Selected BlockForge blueprint is not loaded: " + selection.getSelectedBlueprintId()));
            return InteractionResult.FAIL;
        }

        PlayerSelectionManager.CooldownResult cooldown = BlockForgeConnector.SELECTIONS.checkCooldown(
                player.getUUID(),
                serverLevel.getGameTime()
        );
        if (!cooldown.allowed()) {
            player.sendSystemMessage(Component.literal(
                    "BlockForge Builder Wand cooling down. Try again in "
                            + String.format("%.1f", cooldown.remainingSeconds())
                            + "s."
            ));
            return InteractionResult.FAIL;
        }

        BlockPos basePos = context.getClickedPos().relative(context.getClickedFace());
        BuildService.BuildResult buildResult = BUILDS.build(
                serverLevel,
                player,
                basePos,
                blueprint,
                selection.getRotation()
        );

        if (!buildResult.allowed()) {
            if (buildResult.placementResult() != null) {
                sendPlacementResult(player, buildResult);
            } else {
                player.sendSystemMessage(Component.literal("BlockForge Builder Wand rejected build: " + buildResult.message()));
            }

            if (buildResult.materialReport() != null) {
                sendMissingMaterials(player, buildResult.materialReport().requirements());
            }
            return InteractionResult.FAIL;
        }

        sendPlacementResult(player, buildResult);
        return InteractionResult.SUCCESS;
    }

    private void sendPlacementResult(
            ServerPlayer player,
            BuildService.BuildResult buildResult
    ) {
        var result = buildResult.placementResult();
        if (result.tooLarge()) {
            player.sendSystemMessage(Component.literal(
                    "Blueprint has " + result.totalBlocks() + " blocks, which exceeds the "
                            + result.maxBlocks() + " block safety limit."
            ));
            return;
        }

        if (result.empty()) {
            player.sendSystemMessage(Component.literal("Blueprint has no blocks and cannot be built."));
            return;
        }

        player.sendSystemMessage(Component.literal("BlockForge Builder Wand placed "
                + result.placedBlocks()
                + " blocks. skipped: missingPalette=" + result.skippedMissingPalette()
                + ", invalidBlockId=" + result.skippedInvalidBlockIds()
                + ", invalidProperties=" + result.skippedInvalidProperties()
                + ", outOfWorld=" + result.skippedOutOfWorld()
                + ", protected=" + result.skippedProtected()
                + ", nonReplaceable=" + result.skippedNonReplaceable()
                + ". appliedProperties=" + result.appliedProperties()
                + ". totalBlocks=" + result.totalBlocks()
                + materialSummary(buildResult)
                + undoHint(buildResult)));
    }

    private String materialSummary(BuildService.BuildResult buildResult) {
        if (buildResult == null || buildResult.materialReport() == null) {
            return "";
        }

        if (buildResult.creativeBypass()) {
            return ". Creative mode: no materials consumed";
        }

        return ". consumedItems=" + buildResult.consumedItems();
    }

    private String undoHint(BuildService.BuildResult buildResult) {
        if (buildResult != null && buildResult.consumedItems() > 0) {
            return ". Use /blockforge undo to restore blocks and refund materials.";
        }

        return ". Use /blockforge undo to revert blocks.";
    }

    private void sendMissingMaterials(ServerPlayer player, java.util.List<MaterialRequirement> requirements) {
        requirements.stream()
                .filter(requirement -> requirement.missing() > 0)
                .limit(5)
                .forEach(requirement -> player.sendSystemMessage(Component.literal("- "
                        + requirement.itemId()
                        + " missing="
                        + requirement.missing()
                        + " required="
                        + requirement.required()
                        + " available="
                        + requirement.available())));
    }
}

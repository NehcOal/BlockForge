package com.blockforge.connector.item;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.builder.BlueprintPlacer;
import com.blockforge.connector.material.MaterialBuildGate;
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
    private static final BlueprintPlacer PLACER = new BlueprintPlacer();
    private static final MaterialBuildGate MATERIALS = new MaterialBuildGate();

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
        BlueprintPlacer.PlacementResult dryRun = PLACER.dryRun(blueprint);
        if (dryRun.tooLarge() || dryRun.empty()) {
            sendPlacementResult(player, dryRun, null);
            return InteractionResult.FAIL;
        }

        MaterialBuildGate.BuildMaterialResult materialResult = MATERIALS.prepare(player, blueprint);
        if (!materialResult.allowed()) {
            player.sendSystemMessage(Component.literal("BlockForge Builder Wand rejected build: " + materialResult.message()));
            if (materialResult.report() != null) {
                sendMissingMaterials(player, materialResult.report().requirements());
            }
            return InteractionResult.FAIL;
        }

        BlueprintPlacer.PlacementResult result = PLACER.place(
                serverLevel,
                basePos,
                blueprint,
                selection.getRotation(),
                player
        );

        if (result.snapshot() != null) {
            BlockForgeConnector.UNDO.record(result.snapshot());
        }

        sendPlacementResult(player, result, materialResult);
        return InteractionResult.SUCCESS;
    }

    private void sendPlacementResult(
            ServerPlayer player,
            BlueprintPlacer.PlacementResult result,
            MaterialBuildGate.BuildMaterialResult materialResult
    ) {
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
                + materialSummary(materialResult)
                + ". Use /blockforge undo to revert blocks."));
    }

    private String materialSummary(MaterialBuildGate.BuildMaterialResult materialResult) {
        if (materialResult == null || materialResult.report() == null) {
            return "";
        }

        if (materialResult.creativeBypass()) {
            return ". Creative mode: no materials consumed";
        }

        return ". consumedItems=" + materialResult.consumedItems();
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

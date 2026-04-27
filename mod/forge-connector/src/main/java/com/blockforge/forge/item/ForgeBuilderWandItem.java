package com.blockforge.forge.item;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.protection.ProtectionPreflightReport;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.builder.ForgeBlueprintPlacer;
import com.blockforge.forge.material.ForgeMaterialBuildGate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ForgeBuilderWandItem extends Item {
    private static final ForgeBlueprintPlacer PLACER = new ForgeBlueprintPlacer();
    private static final ForgeMaterialBuildGate MATERIALS = new ForgeMaterialBuildGate();

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
        ForgeBlueprintPlacer.PlacementResult dryRun = PLACER.dryRun(level, basePos, blueprint, selection.rotation());
        if (dryRun.tooLarge() || dryRun.empty() || dryRun.placedBlocks() == 0) {
            sendPlacementResult(player, dryRun);
            return InteractionResult.FAIL;
        }

        ProtectionPreflightReport security = BlockForgeForge.PROTECTION.preflight(
                player,
                level,
                basePos,
                blueprint,
                selection.rotation(),
                BlockForgePermissionAction.BUILD_WAND
        );
        if (!security.allowed()) {
            sendSecurityDenied(player, security);
            return InteractionResult.FAIL;
        }

        ForgeMaterialBuildGate.BuildMaterialResult materialResult = MATERIALS.prepare(player, level, basePos, blueprint);
        if (!materialResult.allowed()) {
            player.sendSystemMessage(Component.literal(materialResult.message()));
            sendMissingMaterials(player, materialResult);
            return InteractionResult.FAIL;
        }

        ForgeBlueprintPlacer.PlacementResult result = PLACER.place(
                level,
                player,
                basePos,
                blueprint,
                selection.rotation()
        );

        if (result.snapshot() != null) {
            BlockForgeForge.UNDO.record(result.snapshot().withMaterialTransaction(materialResult.transaction()));
        } else if (materialResult.transaction() != null) {
            var rollbackResult = MATERIALS.rollback(player, materialResult.transaction());
            sendPlacementResult(player, result);
            if (materialResult.transaction().hasConsumedItems()) {
                player.sendSystemMessage(Component.literal("Build placed no blocks; rolled back "
                        + rollbackResult.refundedItems()
                        + " consumed items"
                        + (rollbackResult.droppedItems() > 0
                        ? ", dropped " + rollbackResult.droppedItems() + " items near player."
                        : ".")));
            } else {
                sendMaterialResult(player, materialResult);
            }
            return InteractionResult.FAIL;
        }

        sendPlacementResult(player, result);
        sendMaterialResult(player, materialResult);

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
                + ". Use /blockforge undo to restore blocks and refund materials."));
    }

    private static void sendSecurityDenied(ServerPlayer player, ProtectionPreflightReport report) {
        player.sendSystemMessage(Component.literal(report.reason().isBlank()
                ? "BlockForge Forge build denied by security preflight."
                : report.reason()));
        for (String warning : report.warnings()) {
            player.sendSystemMessage(Component.literal("Warning: " + warning));
        }
    }

    private static void sendMaterialResult(
            ServerPlayer player,
            ForgeMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        if (materialResult.creativeBypass()) {
            player.sendSystemMessage(Component.literal("Creative mode: no materials consumed."));
            return;
        }

        player.sendSystemMessage(Component.literal("Consumed "
                + materialResult.consumedItems()
                + (materialResult.consumedFromNearbyContainers() > 0
                ? " items (nearbyContainers=" + materialResult.consumedFromNearbyContainers() + ")"
                : " items")
                + ". Use /blockforge undo to restore blocks and refund materials."));
    }

    private static void sendMissingMaterials(
            ServerPlayer player,
            ForgeMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        materialResult.report()
                .requirements()
                .stream()
                .filter(requirement -> requirement.missing() > 0)
                .limit(5)
                .forEach(requirement -> player.sendSystemMessage(Component.literal("Missing materials: "
                        + requirement.itemId()
                        + " missing="
                        + requirement.missing()
                        + " required="
                        + requirement.required()
                        + " available="
                        + requirement.available())));
    }
}

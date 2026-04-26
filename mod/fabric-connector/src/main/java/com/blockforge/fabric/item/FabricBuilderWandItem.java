package com.blockforge.fabric.item;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.fabric.BlockForgeFabric;
import com.blockforge.fabric.builder.FabricBlueprintPlacer;
import com.blockforge.fabric.material.FabricMaterialBuildGate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class FabricBuilderWandItem extends Item {
    private static final FabricBlueprintPlacer PLACER = new FabricBlueprintPlacer();
    private static final FabricMaterialBuildGate MATERIALS = new FabricMaterialBuildGate();

    public FabricBuilderWandItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient()) {
            return ActionResult.SUCCESS;
        }

        if (!(context.getPlayer() instanceof ServerPlayerEntity player)) {
            return ActionResult.FAIL;
        }

        PlayerSelection selection = BlockForgeFabric.SELECTIONS.get(player.getUuid()).orElse(null);
        if (selection == null) {
            player.sendMessage(Text.literal("Use /blockforge select <id> first."), false);
            return ActionResult.FAIL;
        }

        long now = System.currentTimeMillis();
        if (BlockForgeFabric.SELECTIONS.isCoolingDown(selection, now)) {
            long remainingMillis = BlockForgeFabric.SELECTIONS.remainingCooldownMillis(selection, now);
            player.sendMessage(Text.literal("BlockForge Builder Wand cooldown: "
                    + Math.max(1, (long) Math.ceil(remainingMillis / 1000.0))
                    + "s remaining."), false);
            return ActionResult.FAIL;
        }

        Blueprint blueprint = BlockForgeFabric.BLUEPRINTS.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            player.sendMessage(Text.literal("Unknown BlockForge Fabric blueprint id: "
                    + selection.selectedBlueprintId()
                    + ". Run /blockforge reload or select another blueprint."), false);
            return ActionResult.FAIL;
        }

        ServerWorld world = (ServerWorld) context.getWorld();
        BlockPos basePos = context.getBlockPos().offset(context.getSide());
        FabricBlueprintPlacer.PlacementResult dryRun = PLACER.dryRun(world, basePos, blueprint, selection.rotation());
        if (dryRun.tooLarge() || dryRun.empty() || dryRun.placedBlocks() == 0) {
            sendPlacementResult(player, dryRun);
            return ActionResult.FAIL;
        }

        FabricMaterialBuildGate.BuildMaterialResult materialResult = MATERIALS.prepare(player, blueprint);
        if (!materialResult.allowed()) {
            player.sendMessage(Text.literal(materialResult.message()), false);
            sendMissingMaterials(player, materialResult);
            return ActionResult.FAIL;
        }

        FabricBlueprintPlacer.PlacementResult result = PLACER.place(
                world,
                player,
                basePos,
                blueprint,
                selection.rotation()
        );

        if (result.snapshot() != null) {
            BlockForgeFabric.UNDO.record(result.snapshot());
        }

        sendPlacementResult(player, result);
        sendMaterialResult(player, materialResult);

        if (result.placedBlocks() > 0) {
            BlockForgeFabric.SELECTIONS.markBuilt(selection, now);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    private static void sendPlacementResult(
            ServerPlayerEntity player,
            FabricBlueprintPlacer.PlacementResult result
    ) {
        if (result.tooLarge()) {
            player.sendMessage(Text.literal("Blueprint has "
                    + result.totalBlocks()
                    + " blocks, which exceeds the "
                    + result.maxBlocks()
                    + " block safety limit."), false);
            return;
        }

        if (result.empty()) {
            player.sendMessage(Text.literal("Blueprint has no blocks and cannot be built."), false);
            return;
        }

        player.sendMessage(Text.literal("BlockForge Fabric build complete: placed "
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
                + ". Use /blockforge undo to revert blocks."), false);
    }

    private static void sendMaterialResult(
            ServerPlayerEntity player,
            FabricMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        if (materialResult.creativeBypass()) {
            player.sendMessage(Text.literal("Creative mode: no materials consumed."), false);
            return;
        }

        player.sendMessage(Text.literal("Consumed "
                + materialResult.consumedItems()
                + " items. Materials are not refunded yet in Fabric alpha."), false);
    }

    private static void sendMissingMaterials(
            ServerPlayerEntity player,
            FabricMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        materialResult.report()
                .requirements()
                .stream()
                .filter(requirement -> requirement.missing() > 0)
                .limit(5)
                .forEach(requirement -> player.sendMessage(Text.literal("Missing materials: "
                        + requirement.itemId()
                        + " missing="
                        + requirement.missing()
                        + " required="
                        + requirement.required()
                        + " available="
                        + requirement.available()), false));
    }
}

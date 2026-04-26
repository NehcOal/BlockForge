package com.blockforge.forge.builder;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.build.BuildPlan;
import com.blockforge.common.build.PlannedBlock;
import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.common.util.BlockPosition;
import com.blockforge.forge.undo.ForgeUndoManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ForgeBlueprintPlacer {
    public static final int MAX_BLOCKS = 10_000;

    public PlacementResult dryRun(
            ServerLevel level,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        return evaluate(level, null, basePos, blueprint, rotation, false);
    }

    public PlacementResult place(
            ServerLevel level,
            ServerPlayer player,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        return evaluate(level, player, basePos, blueprint, rotation, true);
    }

    private PlacementResult evaluate(
            ServerLevel level,
            ServerPlayer player,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation,
            boolean placeBlocks
    ) {
        if (blueprint.getBlockCount() == 0) {
            return PlacementResult.emptyResult();
        }

        if (blueprint.getBlockCount() > MAX_BLOCKS) {
            return PlacementResult.tooLarge(blueprint.getBlockCount());
        }

        int placed = 0;
        int missingPalette = 0;
        int invalidBlockIds = 0;
        int invalidProperties = 0;
        int outOfWorld = 0;
        int appliedProperties = 0;
        List<ForgeUndoManager.BlockSnapshotEntry> snapshotEntries = new ArrayList<>();
        List<PlannedBlock> plannedBlocks = new ArrayList<>();

        for (BlueprintBlock blueprintBlock : blueprint.getBlocks()) {
            BlueprintPaletteEntry paletteEntry = blueprint.getPalette().get(blueprintBlock.getState());

            if (paletteEntry == null || paletteEntry.name() == null || paletteEntry.name().isBlank()) {
                missingPalette++;
                continue;
            }

            ResourceLocation location = ResourceLocation.tryParse(paletteEntry.name());
            if (location == null) {
                invalidBlockIds++;
                continue;
            }

            Optional<Block> block = BuiltInRegistries.BLOCK.getOptional(location);
            if (block.isEmpty() || block.get() == Blocks.AIR) {
                invalidBlockIds++;
                continue;
            }

            BlueprintRotation.RotatedPosition rotated = rotation.rotate(blueprintBlock, blueprint.getSize());
            BlockPos target = basePos.offset(rotated.x(), blueprintBlock.getY(), rotated.z());

            if (level != null && (target.getY() < level.getMinBuildHeight() || target.getY() >= level.getMaxBuildHeight())) {
                outOfWorld++;
                continue;
            }

            StateResult stateResult = applyProperties(block.get().defaultBlockState(), paletteEntry, rotation);
            appliedProperties += stateResult.appliedProperties();

            if (stateResult.invalidProperties() > 0) {
                invalidProperties++;
                continue;
            }

            if (placeBlocks && level != null) {
                BlockState previousState = level.getBlockState(target);
                level.setBlock(target, stateResult.state(), Block.UPDATE_ALL);
                snapshotEntries.add(new ForgeUndoManager.BlockSnapshotEntry(target, previousState));
            }

            plannedBlocks.add(new PlannedBlock(
                    new BlockPosition(target.getX(), target.getY(), target.getZ()),
                    paletteEntry.name(),
                    blueprintBlock.getState(),
                    paletteEntry.properties()
            ));
            placed++;
        }

        ForgeUndoManager.PlacementSnapshot snapshot = null;
        if (placeBlocks && player != null && level != null && placed > 0 && !snapshotEntries.isEmpty()) {
            snapshot = new ForgeUndoManager.PlacementSnapshot(
                    player.getUUID(),
                    player.getGameProfile().getName(),
                    blueprint.getId(),
                    level.getGameTime(),
                    placed,
                    snapshotEntries
            );
        }

        BuildPlan buildPlan = new BuildPlan(
                blueprint.getId(),
                new BlockPosition(basePos.getX(), basePos.getY(), basePos.getZ()),
                rotation,
                plannedBlocks,
                blueprint.getBlockCount()
        );

        return new PlacementResult(
                false,
                false,
                blueprint.getBlockCount(),
                placed,
                missingPalette,
                invalidBlockIds,
                invalidProperties,
                outOfWorld,
                appliedProperties,
                MAX_BLOCKS,
                snapshot,
                buildPlan
        );
    }

    private StateResult applyProperties(
            BlockState state,
            BlueprintPaletteEntry paletteEntry,
            BlueprintRotation rotation
    ) {
        BlockState currentState = state;
        int applied = 0;
        int invalid = 0;

        for (var entry : paletteEntry.properties().entrySet()) {
            String propertyName = entry.getKey();
            String propertyValue = "facing".equals(propertyName)
                    ? rotation.rotateFacing(entry.getValue())
                    : entry.getValue();
            Optional<Property<?>> property = currentState.getProperties()
                    .stream()
                    .filter(candidate -> candidate.getName().equals(propertyName))
                    .findFirst();

            if (property.isEmpty()) {
                invalid++;
                continue;
            }

            PropertyApplyResult result = applyProperty(currentState, property.get(), propertyValue);
            currentState = result.state();
            applied += result.applied() ? 1 : 0;
            invalid += result.applied() ? 0 : 1;
        }

        return new StateResult(currentState, applied, invalid);
    }

    private <T extends Comparable<T>> PropertyApplyResult applyProperty(
            BlockState state,
            Property<T> property,
            String value
    ) {
        Optional<T> parsedValue = property.getValue(value);

        if (parsedValue.isEmpty()) {
            return new PropertyApplyResult(state, false);
        }

        return new PropertyApplyResult(state.setValue(property, parsedValue.get()), true);
    }

    public record PlacementResult(
            boolean tooLarge,
            boolean empty,
            int totalBlocks,
            int placedBlocks,
            int skippedMissingPalette,
            int skippedInvalidBlockIds,
            int skippedInvalidProperties,
            int skippedOutOfWorld,
            int appliedProperties,
            int maxBlocks,
            ForgeUndoManager.PlacementSnapshot snapshot,
            BuildPlan buildPlan
    ) {
        public static PlacementResult tooLarge(int totalBlocks) {
            return new PlacementResult(true, false, totalBlocks, 0, 0, 0, 0, 0, 0, MAX_BLOCKS, null, null);
        }

        public static PlacementResult emptyResult() {
            return new PlacementResult(false, true, 0, 0, 0, 0, 0, 0, 0, MAX_BLOCKS, null, null);
        }

        public boolean validBuildPlan() {
            return !tooLarge && !empty && buildPlan != null && buildPlan.plannedBlockCount() > 0;
        }
    }

    private record StateResult(BlockState state, int appliedProperties, int invalidProperties) {
    }

    private record PropertyApplyResult(BlockState state, boolean applied) {
    }
}

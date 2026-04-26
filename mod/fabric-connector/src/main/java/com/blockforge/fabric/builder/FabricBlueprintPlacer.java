package com.blockforge.fabric.builder;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.build.BuildPlan;
import com.blockforge.common.build.PlannedBlock;
import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.common.util.BlockPosition;
import com.blockforge.fabric.undo.FabricUndoManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricBlueprintPlacer {
    public static final int MAX_BLOCKS = 10_000;

    public PlacementResult dryRun(
            ServerWorld world,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        return evaluate(world, null, basePos, blueprint, rotation, false);
    }

    public PlacementResult place(
            ServerWorld world,
            ServerPlayerEntity player,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        return evaluate(world, player, basePos, blueprint, rotation, true);
    }

    private PlacementResult evaluate(
            ServerWorld world,
            ServerPlayerEntity player,
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
        List<FabricUndoManager.BlockSnapshotEntry> snapshotEntries = new ArrayList<>();
        List<PlannedBlock> plannedBlocks = new ArrayList<>();

        for (BlueprintBlock blueprintBlock : blueprint.getBlocks()) {
            BlueprintPaletteEntry paletteEntry = blueprint.getPalette().get(blueprintBlock.getState());

            if (paletteEntry == null || paletteEntry.name() == null || paletteEntry.name().isBlank()) {
                missingPalette++;
                continue;
            }

            Identifier identifier = Identifier.tryParse(paletteEntry.name());
            if (identifier == null) {
                invalidBlockIds++;
                continue;
            }

            Optional<Block> block = Registries.BLOCK.getOrEmpty(identifier);
            if (block.isEmpty() || block.get() == Blocks.AIR) {
                invalidBlockIds++;
                continue;
            }

            BlueprintRotation.RotatedPosition rotated = rotation.rotate(blueprintBlock, blueprint.getSize());
            BlockPos target = basePos.add(rotated.x(), blueprintBlock.getY(), rotated.z());

            if (world != null && world.isOutOfHeightLimit(target)) {
                outOfWorld++;
                continue;
            }

            StateResult stateResult = applyProperties(block.get().getDefaultState(), paletteEntry, rotation);
            appliedProperties += stateResult.appliedProperties();

            if (stateResult.invalidProperties() > 0) {
                invalidProperties++;
                continue;
            }

            if (placeBlocks && world != null) {
                BlockState previousState = world.getBlockState(target);
                world.setBlockState(target, stateResult.state(), Block.NOTIFY_ALL);
                snapshotEntries.add(new FabricUndoManager.BlockSnapshotEntry(target, previousState));
            }

            plannedBlocks.add(new PlannedBlock(
                    new BlockPosition(target.getX(), target.getY(), target.getZ()),
                    paletteEntry.name(),
                    blueprintBlock.getState(),
                    paletteEntry.properties()
            ));
            placed++;
        }

        FabricUndoManager.PlacementSnapshot snapshot = null;
        if (placeBlocks && player != null && placed > 0 && !snapshotEntries.isEmpty()) {
            snapshot = new FabricUndoManager.PlacementSnapshot(
                    player.getUuid(),
                    player.getName().getString(),
                    blueprint.getId(),
                    world.getTime(),
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
        Optional<T> parsedValue = property.parse(value);

        if (parsedValue.isEmpty()) {
            return new PropertyApplyResult(state, false);
        }

        return new PropertyApplyResult(state.with(property, parsedValue.get()), true);
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
            FabricUndoManager.PlacementSnapshot snapshot,
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

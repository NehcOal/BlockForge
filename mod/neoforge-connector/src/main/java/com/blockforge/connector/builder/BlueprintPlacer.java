package com.blockforge.connector.builder;

import com.blockforge.common.build.BuildPlan;
import com.blockforge.common.build.PlannedBlock;
import com.blockforge.common.util.BlockPosition;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;
import com.blockforge.connector.blueprint.BlueprintPaletteEntry;
import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.undo.BlockSnapshotEntry;
import com.blockforge.connector.undo.PlacementSnapshot;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlueprintPlacer {
    public PlacementResult dryRun(Blueprint blueprint) {
        return evaluate(null, null, BlockPos.ZERO, blueprint, BlueprintRotation.NONE, false);
    }

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
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        return place(level, basePos, blueprint, rotation, null);
    }

    public PlacementResult place(
            ServerLevel level,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation,
            ServerPlayer player
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
        int maxBlocks = BlockForgeConfig.maxBlocksPerBuild();

        if (blueprint.getBlockCount() == 0) {
            return PlacementResult.empty(maxBlocks);
        }

        if (blueprint.getBlockCount() > maxBlocks) {
            return PlacementResult.tooLarge(blueprint.getBlockCount(), maxBlocks);
        }

        int placed = 0;
        int missingPalette = 0;
        int invalidBlockIds = 0;
        int outOfWorld = 0;
        int protectedBlocks = 0;
        int nonReplaceable = 0;
        int appliedProperties = 0;
        int invalidProperties = 0;
        List<BlockSnapshotEntry> snapshotEntries = new ArrayList<>();
        List<BlueprintBlock> acceptedBlocks = new ArrayList<>();
        List<PlannedBlock> plannedBlocks = new ArrayList<>();

        for (BlueprintBlock blueprintBlock : blueprint.getBlocks()) {
            String stateKey = blueprintBlock.getState();
            BlueprintPaletteEntry paletteEntry = blueprint.getPalette().get(stateKey);

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

            BlueprintRotation.RotatedPosition rotatedPosition = rotation.rotate(blueprintBlock, blueprint.getSize());
            BlockPos target = basePos.offset(rotatedPosition.x(), blueprintBlock.getY(), rotatedPosition.z());

            if (level != null && (target.getY() < level.getMinBuildHeight() || target.getY() >= level.getMaxBuildHeight())) {
                outOfWorld++;
                continue;
            }

            BlockState previousState = null;
            CompoundTag blockEntityTag = null;

            if (level != null) {
                previousState = level.getBlockState(target);
                BlockEntity blockEntity = level.getBlockEntity(target);

                if (BlockForgeConfig.protectBlockEntities() && blockEntity != null) {
                    protectedBlocks++;
                    continue;
                }

                if (!BlockForgeConfig.allowReplaceNonAir()
                        && !previousState.isAir()
                        && !previousState.canBeReplaced()) {
                    nonReplaceable++;
                    continue;
                }

                if (placeBlocks && blockEntity != null) {
                    blockEntityTag = blockEntity.saveWithFullMetadata(level.registryAccess());
                }
            }

            StateResult stateResult = applyProperties(block.get().defaultBlockState(), paletteEntry, rotation);
            appliedProperties += stateResult.appliedProperties();
            invalidProperties += stateResult.invalidProperties();

            if (placeBlocks) {
                level.setBlock(target, stateResult.state(), Block.UPDATE_ALL);
                snapshotEntries.add(new BlockSnapshotEntry(target, previousState, blockEntityTag));
            }

            acceptedBlocks.add(blueprintBlock);
            plannedBlocks.add(new PlannedBlock(
                    new BlockPosition(target.getX(), target.getY(), target.getZ()),
                    paletteEntry.name(),
                    stateKey,
                    paletteEntry.properties()
            ));
            placed++;
        }

        PlacementSnapshot snapshot = createSnapshot(level, player, blueprint, placed, snapshotEntries);
        BuildPlan buildPlan = new BuildPlan(
                blueprint.getId(),
                new BlockPosition(basePos.getX(), basePos.getY(), basePos.getZ()),
                rotation.toCommon(),
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
                protectedBlocks,
                nonReplaceable,
                appliedProperties,
                maxBlocks,
                snapshot,
                acceptedBlocks,
                buildPlan
        );
    }

    private PlacementSnapshot createSnapshot(
            ServerLevel level,
            ServerPlayer player,
            Blueprint blueprint,
            int placed,
            List<BlockSnapshotEntry> entries
    ) {
        if (level == null || player == null || placed <= 0 || entries.isEmpty()) {
            return null;
        }

        return new PlacementSnapshot(
                player.getUUID(),
                player.getGameProfile().getName(),
                blueprint.getId(),
                level.getGameTime(),
                placed,
                entries
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
            int skippedProtected,
            int skippedNonReplaceable,
            int appliedProperties,
            int maxBlocks,
            PlacementSnapshot snapshot,
            List<BlueprintBlock> acceptedBlocks,
            BuildPlan buildPlan
    ) {
        public PlacementResult {
            acceptedBlocks = acceptedBlocks == null ? List.of() : List.copyOf(acceptedBlocks);
        }

        public static PlacementResult tooLarge(int totalBlocks, int maxBlocks) {
            return new PlacementResult(true, false, totalBlocks, 0, 0, 0, 0, 0, 0, 0, 0, maxBlocks, null, List.of(), null);
        }

        public static PlacementResult empty(int maxBlocks) {
            return new PlacementResult(false, true, 0, 0, 0, 0, 0, 0, 0, 0, 0, maxBlocks, null, List.of(), null);
        }

        public PlacementResult withSnapshot(PlacementSnapshot updatedSnapshot) {
            return new PlacementResult(
                    tooLarge,
                    empty,
                    totalBlocks,
                    placedBlocks,
                    skippedMissingPalette,
                    skippedInvalidBlockIds,
                    skippedInvalidProperties,
                    skippedOutOfWorld,
                    skippedProtected,
                    skippedNonReplaceable,
                    appliedProperties,
                    maxBlocks,
                    updatedSnapshot,
                    acceptedBlocks,
                    buildPlan
            );
        }
    }

    private record StateResult(BlockState state, int appliedProperties, int invalidProperties) {
    }

    private record PropertyApplyResult(BlockState state, boolean applied) {
    }
}

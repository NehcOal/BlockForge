package com.blockforge.connector.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceScanResult;
import com.blockforge.common.material.source.MaterialSourceType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeMaterialSourceScanner {
    public Scan scan(
            ServerPlayer player,
            ServerLevel level,
            BlockPos center,
            MaterialSourceConfig config
    ) {
        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        if (player == null || level == null || center == null || !resolvedConfig.enableNearbyContainers()) {
            return new Scan(new MaterialSourceScanResult(List.of(), 0, 0, List.of()), List.of());
        }

        List<NeoForgeContainerMaterialSource> containers = new ArrayList<>();
        List<MaterialSourceRef> refs = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int radius = resolvedConfig.searchRadius();
        int scannedBlocks = 0;

        outer:
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
                for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                    if (y < level.getMinBuildHeight() || y >= level.getMaxBuildHeight()) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.hasChunkAt(pos)) {
                        continue;
                    }

                    scannedBlocks++;
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity == null) {
                        continue;
                    }

                    IItemHandler handler = itemHandler(level, pos, blockEntity);
                    if (handler == null) {
                        continue;
                    }

                    MaterialSourceRef ref = new MaterialSourceRef(
                            MaterialSourceType.NEARBY_CONTAINER,
                            sourceId(level, pos),
                            blockEntity.getBlockState().getBlock().getName().getString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );
                    refs.add(ref);
                    containers.add(new NeoForgeContainerMaterialSource(level, pos, ref, handler));

                    if (containers.size() >= resolvedConfig.maxContainersScanned()) {
                        warnings.add("Reached nearby container scan limit: " + resolvedConfig.maxContainersScanned());
                        break outer;
                    }
                }
            }
        }

        return new Scan(
                new MaterialSourceScanResult(refs, scannedBlocks, containers.size(), warnings),
                containers
        );
    }

    public NeoForgeContainerMaterialSource sourceFor(ServerLevel level, MaterialSourceRef ref) {
        if (level == null || ref == null || ref.type() != MaterialSourceType.NEARBY_CONTAINER) {
            return null;
        }

        BlockPos pos = new BlockPos(ref.x(), ref.y(), ref.z());
        if (!level.hasChunkAt(pos)) {
            return null;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return null;
        }

        IItemHandler handler = itemHandler(level, pos, blockEntity);
        if (handler == null) {
            return null;
        }

        return new NeoForgeContainerMaterialSource(level, pos, ref, handler);
    }

    private IItemHandler itemHandler(ServerLevel level, BlockPos pos, BlockEntity blockEntity) {
        return level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                pos,
                blockEntity.getBlockState(),
                blockEntity,
                null
        );
    }

    private String sourceId(ServerLevel level, BlockPos pos) {
        return level.dimension().location() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public record Scan(
            MaterialSourceScanResult result,
            List<NeoForgeContainerMaterialSource> containers
    ) {
        public Scan {
            containers = containers == null ? List.of() : List.copyOf(containers);
        }
    }
}

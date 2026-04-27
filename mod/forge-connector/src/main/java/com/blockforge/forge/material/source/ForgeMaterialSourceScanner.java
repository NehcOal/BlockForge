package com.blockforge.forge.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceScanResult;
import com.blockforge.common.material.source.MaterialSourceType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public class ForgeMaterialSourceScanner {
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

        List<MaterialSourceRef> refs = new ArrayList<>();
        List<ForgeContainerMaterialSource> containers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int scannedBlocks = 0;
        int radius = resolvedConfig.searchRadius();

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

                    IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
                    if (handler == null) {
                        continue;
                    }

                    MaterialSourceRef ref = new MaterialSourceRef(
                            MaterialSourceType.NEARBY_CONTAINER,
                            sourceId(level, pos),
                            blockEntity.getBlockState().getBlock().getName().getString(),
                            level.dimension().location().toString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );
                    refs.add(ref);
                    containers.add(new ForgeContainerMaterialSource(level, pos, ref, handler));

                    if (containers.size() >= resolvedConfig.maxContainersScanned()) {
                        warnings.add("Reached nearby container scan limit: " + resolvedConfig.maxContainersScanned());
                        break outer;
                    }
                }
            }
        }

        return new Scan(new MaterialSourceScanResult(refs, scannedBlocks, containers.size(), warnings), containers);
    }

    public ForgeContainerMaterialSource sourceFor(ServerLevel level, MaterialSourceRef ref) {
        if (level == null || ref == null || ref.type() != MaterialSourceType.NEARBY_CONTAINER) {
            return null;
        }
        String currentDimension = level.dimension().location().toString();
        if (!ref.dimensionId().isBlank() && !ref.dimensionId().equals(currentDimension)) {
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

        IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (handler == null) {
            return null;
        }

        return new ForgeContainerMaterialSource(level, pos, ref, handler);
    }

    private String sourceId(ServerLevel level, BlockPos pos) {
        return level.dimension().location() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public record Scan(MaterialSourceScanResult result, List<ForgeContainerMaterialSource> containers) {
        public Scan {
            containers = containers == null ? List.of() : List.copyOf(containers);
        }
    }
}

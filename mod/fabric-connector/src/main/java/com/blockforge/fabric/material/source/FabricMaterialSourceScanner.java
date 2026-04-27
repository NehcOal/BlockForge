package com.blockforge.fabric.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceRef;
import com.blockforge.common.material.source.MaterialSourceScanResult;
import com.blockforge.common.material.source.MaterialSourceType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class FabricMaterialSourceScanner {
    public Scan scan(
            ServerPlayerEntity player,
            ServerWorld world,
            BlockPos center,
            MaterialSourceConfig config
    ) {
        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        if (player == null || world == null || center == null || !resolvedConfig.enableNearbyContainers()) {
            return new Scan(new MaterialSourceScanResult(List.of(), 0, 0, List.of()), List.of());
        }

        List<MaterialSourceRef> refs = new ArrayList<>();
        List<FabricContainerMaterialSource> containers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        int scannedBlocks = 0;
        int radius = resolvedConfig.searchRadius();

        outer:
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int y = center.getY() - radius; y <= center.getY() + radius; y++) {
                for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                    if (y < world.getBottomY() || y >= world.getTopY()) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(x, y, z);
                    if (!world.isChunkLoaded(pos)) {
                        continue;
                    }

                    scannedBlocks++;
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (!(blockEntity instanceof Inventory inventory)) {
                        continue;
                    }

                    MaterialSourceRef ref = new MaterialSourceRef(
                            MaterialSourceType.NEARBY_CONTAINER,
                            sourceId(world, pos),
                            blockEntity.getCachedState().getBlock().getName().getString(),
                            world.getRegistryKey().getValue().toString(),
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );
                    refs.add(ref);
                    containers.add(new FabricContainerMaterialSource(world, pos, ref, inventory));

                    if (containers.size() >= resolvedConfig.maxContainersScanned()) {
                        warnings.add("Reached nearby container scan limit: " + resolvedConfig.maxContainersScanned());
                        break outer;
                    }
                }
            }
        }

        return new Scan(new MaterialSourceScanResult(refs, scannedBlocks, containers.size(), warnings), containers);
    }

    public FabricContainerMaterialSource sourceFor(ServerWorld world, MaterialSourceRef ref) {
        if (world == null || ref == null || ref.type() != MaterialSourceType.NEARBY_CONTAINER) {
            return null;
        }
        String currentDimension = world.getRegistryKey().getValue().toString();
        if (!ref.dimensionId().isBlank() && !ref.dimensionId().equals(currentDimension)) {
            return null;
        }

        BlockPos pos = new BlockPos(ref.x(), ref.y(), ref.z());
        if (!world.isChunkLoaded(pos)) {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof Inventory inventory)) {
            return null;
        }

        return new FabricContainerMaterialSource(world, pos, ref, inventory);
    }

    private String sourceId(ServerWorld world, BlockPos pos) {
        return world.getRegistryKey().getValue() + ":" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public record Scan(MaterialSourceScanResult result, List<FabricContainerMaterialSource> containers) {
        public Scan {
            containers = containers == null ? List.of() : List.copyOf(containers);
        }
    }
}

package com.blockforge.common.house;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.blueprint.BlueprintSize;
import com.blockforge.common.house.HousePlan.HouseModule;
import com.blockforge.common.house.HousePlan.HouseModuleType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public final class HouseBlueprintCompiler {
    public Blueprint compile(HousePlan plan) {
        Map<String, String> blocks = new TreeMap<>();
        for (HouseModule module : plan.modules()) {
            placeModule(blocks, module, plan);
        }
        Map<String, BlueprintPaletteEntry> palette = new LinkedHashMap<>();
        List<BlueprintBlock> blueprintBlocks = new ArrayList<>();
        for (Map.Entry<String, String> entry : blocks.entrySet()) {
            String blockId = entry.getValue();
            String stateKey = stateKey(blockId);
            palette.putIfAbsent(stateKey, new BlueprintPaletteEntry(blockId, Map.of()));
            int[] position = parsePosition(entry.getKey());
            blueprintBlocks.add(new BlueprintBlock(position[0], position[1], position[2], stateKey));
        }
        blueprintBlocks.sort((a, b) -> {
            if (a.getY() != b.getY()) {
                return Integer.compare(a.getY(), b.getY());
            }
            if (a.getZ() != b.getZ()) {
                return Integer.compare(a.getZ(), b.getZ());
            }
            return Integer.compare(a.getX(), b.getX());
        });
        return new Blueprint(
                2,
                plan.housePlanId(),
                plan.name(),
                "Rule-based BlockForge house plan generated from " + plan.style().name().toLowerCase(Locale.ROOT) + ".",
                "1.21.1",
                "blockforge-house-generator",
                new BlueprintSize(plan.footprint().width(), plan.dimensions().totalHeight(), plan.footprint().depth()),
                palette,
                blueprintBlocks
        );
    }

    private static void placeModule(Map<String, String> blocks, HouseModule module, HousePlan plan) {
        if (module.type() == HouseModuleType.DOOR || module.type() == HouseModuleType.WINDOW) {
            clearOpening(blocks, module);
        }
        if (module.type() == HouseModuleType.ROOF) {
            placeRoof(blocks, module, plan);
            return;
        }
        for (int x = 0; x < module.width(); x++) {
            for (int y = 0; y < module.height(); y++) {
                for (int z = 0; z < module.depth(); z++) {
                    int px = module.x() + x;
                    int py = module.y() + y;
                    int pz = module.z() + z;
                    if (inBounds(px, py, pz, plan)) {
                        blocks.put(key(px, py, pz), module.blockKey());
                    }
                }
            }
        }
    }

    private static void placeRoof(Map<String, String> blocks, HouseModule module, HousePlan plan) {
        int width = plan.footprint().width();
        int depth = plan.footprint().depth();
        int baseY = module.y();
        String block = module.blockKey();
        switch (plan.roof().type()) {
            case FLAT, SHED -> {
                for (int x = 0; x < width; x++) {
                    for (int z = 0; z < depth; z++) {
                        blocks.put(key(x, baseY, z), block);
                    }
                }
            }
            case TOWER, PYRAMID, HIP -> {
                int layers = Math.max(1, module.height());
                for (int layer = 0; layer < layers; layer++) {
                    for (int x = layer; x < width - layer; x++) {
                        for (int z = layer; z < depth - layer; z++) {
                            if (x == layer || x == width - layer - 1 || z == layer || z == depth - layer - 1 || layer == layers - 1) {
                                blocks.put(key(x, baseY + layer, z), block);
                            }
                        }
                    }
                }
            }
            case GABLE -> {
                int ridge = width / 2;
                for (int z = 0; z < depth; z++) {
                    for (int x = 0; x < width; x++) {
                        int y = baseY + Math.max(0, Math.min(x, width - 1 - x));
                        if (Math.abs(x - ridge) <= Math.max(1, module.height())) {
                            blocks.put(key(x, Math.min(baseY + module.height() - 1, y), z), block);
                        }
                    }
                    blocks.put(key(ridge, baseY + module.height() - 1, z), plan.roof().trimBlockId());
                }
            }
            case NONE -> {
            }
        }
    }

    private static void clearOpening(Map<String, String> blocks, HouseModule module) {
        for (int x = 0; x < Math.max(1, module.width()); x++) {
            for (int y = 0; y < Math.max(1, module.height()); y++) {
                for (int z = 0; z < Math.max(1, module.depth()); z++) {
                    blocks.remove(key(module.x() + x, module.y() + y, module.z() + z));
                }
            }
        }
    }

    private static boolean inBounds(int x, int y, int z, HousePlan plan) {
        return x >= 0 && y >= 0 && z >= 0
                && x < plan.footprint().width()
                && y < plan.dimensions().totalHeight()
                && z < plan.footprint().depth();
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }

    private static int[] parsePosition(String key) {
        String[] parts = key.split(":");
        return new int[] {Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])};
    }

    private static String stateKey(String blockId) {
        return blockId.replace("minecraft:", "").replace(':', '_').replace('/', '_');
    }
}

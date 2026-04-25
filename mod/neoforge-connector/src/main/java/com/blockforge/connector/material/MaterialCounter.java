package com.blockforge.connector.material;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;
import com.blockforge.connector.blueprint.BlueprintPaletteEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialCounter {
    public MaterialReport count(Blueprint blueprint) {
        return withAvailability(blueprint, Map.of());
    }

    public MaterialReport withAvailability(Blueprint blueprint, Map<String, Integer> availableItems) {
        Map<String, MutableRequirement> requirements = new HashMap<>();

        for (BlueprintBlock block : blueprint.getBlocks()) {
            BlueprintPaletteEntry paletteEntry = blueprint.getPalette().get(block.getState());
            if (paletteEntry == null || paletteEntry.name() == null || paletteEntry.name().isBlank()) {
                continue;
            }

            String blockId = paletteEntry.name();
            String countedItemId = itemIdForBlock(blockId);
            String resolvedItemId = countedItemId == null ? "minecraft:air" : countedItemId;

            MutableRequirement requirement = requirements.computeIfAbsent(
                    resolvedItemId,
                    ignored -> new MutableRequirement(block.getState(), blockId, resolvedItemId)
            );
            requirement.required++;
        }

        List<MaterialRequirement> resolvedRequirements = requirements.values()
                .stream()
                .map(requirement -> requirement.toRequirement(availableItems.getOrDefault(requirement.itemId, 0)))
                .sorted((left, right) -> left.itemId().compareTo(right.itemId()))
                .toList();

        int totalRequired = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .mapToInt(MaterialRequirement::required)
                .sum();
        int totalAvailable = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .mapToInt(requirement -> Math.min(requirement.available(), requirement.required()))
                .sum();
        boolean enough = resolvedRequirements.stream()
                .filter(MaterialRequirement::consumable)
                .allMatch(requirement -> requirement.missing() == 0);

        return new MaterialReport(
                blueprint.getId(),
                blueprint.getBlockCount(),
                totalRequired,
                totalAvailable,
                enough,
                resolvedRequirements
        );
    }

    private String itemIdForBlock(String blockId) {
        ResourceLocation location = ResourceLocation.tryParse(blockId);
        if (location == null) {
            return "minecraft:air";
        }

        return BuiltInRegistries.BLOCK.getOptional(location)
                .map(Block::asItem)
                .filter(item -> item != Items.AIR)
                .map(this::itemId)
                .orElse("minecraft:air");
    }

    private String itemId(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return location == null ? "minecraft:air" : location.toString();
    }

    private static final class MutableRequirement {
        private final String blockStateKey;
        private final String blockId;
        private final String itemId;
        private int required;

        private MutableRequirement(String blockStateKey, String blockId, String itemId) {
            this.blockStateKey = blockStateKey;
            this.blockId = blockId;
            this.itemId = itemId;
        }

        private MaterialRequirement toRequirement(int available) {
            return new MaterialRequirement(
                    blockStateKey,
                    blockId,
                    itemId,
                    required,
                    available,
                    Math.max(0, required - available)
            );
        }
    }
}

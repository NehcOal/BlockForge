package com.blockforge.connector.material;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.BlueprintBlock;
import com.blockforge.connector.blueprint.BlueprintPaletteEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MaterialCounter {
    private final com.blockforge.common.material.MaterialCounter counter;

    public MaterialCounter() {
        this(MaterialCounter::itemIdForBlock);
    }

    MaterialCounter(Function<String, String> itemIdResolver) {
        this.counter = new com.blockforge.common.material.MaterialCounter(itemIdResolver);
    }

    public MaterialReport count(Blueprint blueprint) {
        return withAvailability(blueprint, Map.of());
    }

    public MaterialReport count(Blueprint blueprint, List<BlueprintBlock> blocks) {
        return withAvailability(blueprint, blocks, Map.of());
    }

    public MaterialReport withAvailability(Blueprint blueprint, Map<String, Integer> availableItems) {
        return withAvailability(blueprint, blueprint.getBlocks(), availableItems);
    }

    public MaterialReport withAvailability(
            Blueprint blueprint,
            List<BlueprintBlock> blocks,
            Map<String, Integer> availableItems
    ) {
        com.blockforge.common.material.MaterialReport report = counter.withAvailability(
                blueprint.getId(),
                blocks,
                blueprint.getPalette(),
                availableItems
        );
        List<MaterialRequirement> requirements = report.requirements()
                .stream()
                .map(requirement -> new MaterialRequirement(
                        requirement.blockStateKey(),
                        requirement.blockId(),
                        requirement.itemId(),
                        requirement.required(),
                        requirement.available(),
                        requirement.missing()
                ))
                .toList();

        return new MaterialReport(
                report.blueprintId(),
                report.totalBlocks(),
                report.totalRequiredItems(),
                report.totalAvailableItems(),
                report.enoughMaterials(),
                requirements
        );
    }

    private static String itemIdForBlock(String blockId) {
        ResourceLocation location = ResourceLocation.tryParse(blockId);
        if (location == null) {
            return "minecraft:air";
        }

        return BuiltInRegistries.BLOCK.getOptional(location)
                .map(Block::asItem)
                .filter(item -> item != Items.AIR)
                .map(MaterialCounter::itemId)
                .orElse("minecraft:air");
    }

    private static String itemId(Item item) {
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(item);
        return location == null ? "minecraft:air" : location.toString();
    }

}

package com.blockforge.forge.material;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ForgeMaterialConsumer {
    public ConsumeResult consume(ServerPlayer player, MaterialReport report) {
        if (player == null) {
            return new ConsumeResult(false, 0, "This build requires a player.");
        }
        if (player.isCreative()) {
            return new ConsumeResult(true, 0, "Creative mode: no materials consumed.");
        }
        if (!report.enoughMaterials()) {
            return new ConsumeResult(false, 0, "Not enough materials.");
        }

        int consumedItems = 0;
        for (MaterialRequirement requirement : report.requirements()) {
            if (!requirement.consumable() || requirement.required() <= 0) {
                continue;
            }

            Item item = itemForId(requirement.itemId());
            if (item == Items.AIR) {
                return new ConsumeResult(false, consumedItems, "Cannot consume item: " + requirement.itemId());
            }

            int consumed = consumeItem(player, item, requirement.required());
            consumedItems += consumed;
            if (consumed < requirement.required()) {
                return new ConsumeResult(false, consumedItems, "Could not consume enough of " + requirement.itemId());
            }
        }

        return new ConsumeResult(true, consumedItems, "");
    }

    private int consumeItem(ServerPlayer player, Item item, int required) {
        int remaining = required;
        int consumed = 0;

        for (int slot = 0; slot < player.getInventory().getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty() || !stack.is(item)) {
                continue;
            }

            int amount = Math.min(remaining, stack.getCount());
            stack.shrink(amount);
            remaining -= amount;
            consumed += amount;
        }

        return consumed;
    }

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }

    public record ConsumeResult(boolean success, int consumedItems, String message) {
    }
}

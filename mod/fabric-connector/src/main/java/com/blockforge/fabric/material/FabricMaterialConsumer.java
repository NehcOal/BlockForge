package com.blockforge.fabric.material;

import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FabricMaterialConsumer {
    public ConsumeResult consume(ServerPlayerEntity player, MaterialReport report) {
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

    private int consumeItem(ServerPlayerEntity player, Item item, int required) {
        int remaining = required;
        int consumed = 0;

        for (int slot = 0; slot < player.getInventory().size() && remaining > 0; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty() || !stack.isOf(item)) {
                continue;
            }

            int amount = Math.min(remaining, stack.getCount());
            stack.decrement(amount);
            remaining -= amount;
            consumed += amount;
        }

        return consumed;
    }

    private Item itemForId(String itemId) {
        Identifier identifier = Identifier.tryParse(itemId);
        if (identifier == null) {
            return Items.AIR;
        }
        return Registries.ITEM.getOrEmpty(identifier).orElse(Items.AIR);
    }

    public record ConsumeResult(boolean success, int consumedItems, String message) {
    }
}

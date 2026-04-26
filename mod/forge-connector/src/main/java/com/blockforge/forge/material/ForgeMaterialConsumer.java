package com.blockforge.forge.material;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForgeMaterialConsumer {
    public ConsumeResult consumeMaterials(ServerPlayer player, MaterialReport report) {
        if (player == null) {
            return ConsumeResult.failed("This build requires a player.");
        }
        if (player.isCreative()) {
            return ConsumeResult.success(MaterialTransaction.creative(
                    player.getUUID(),
                    report.blueprintId(),
                    player.level().getGameTime()
            ));
        }
        if (!report.enoughMaterials()) {
            return ConsumeResult.failed("Not enough materials.");
        }

        Map<String, Integer> consumedItems = new LinkedHashMap<>();
        for (MaterialRequirement requirement : report.requirements()) {
            if (!requirement.consumable() || requirement.required() <= 0) {
                continue;
            }

            Item item = itemForId(requirement.itemId());
            if (item == Items.AIR) {
                MaterialTransaction partial = transaction(player, report.blueprintId(), consumedItems);
                rollback(player, partial);
                return ConsumeResult.failed("Cannot consume item: " + requirement.itemId());
            }

            int consumed = consumeItem(player, item, requirement.required());
            if (consumed > 0) {
                consumedItems.merge(requirement.itemId(), consumed, Integer::sum);
            }
            if (consumed < requirement.required()) {
                MaterialTransaction partial = transaction(player, report.blueprintId(), consumedItems);
                rollback(player, partial);
                return ConsumeResult.failed("Could not consume enough of " + requirement.itemId());
            }
        }

        return ConsumeResult.success(transaction(player, report.blueprintId(), consumedItems));
    }

    public MaterialRefundResult refundMaterials(ServerPlayer player, MaterialTransaction transaction) {
        if (player == null || transaction == null || transaction.creativeBypass() || transaction.consumedItems().isEmpty()) {
            return MaterialRefundResult.none();
        }

        int refundedItems = 0;
        int droppedItems = 0;
        List<String> warnings = new ArrayList<>();

        for (ConsumedMaterialEntry entry : transaction.consumedItems()) {
            Item item = itemForId(entry.itemId());
            if (item == Items.AIR) {
                warnings.add("Cannot refund unknown item: " + entry.itemId());
                continue;
            }

            int remaining = entry.count();
            while (remaining > 0) {
                int amount = Math.min(remaining, item.getDefaultMaxStackSize());
                ItemStack stack = new ItemStack(item, amount);
                int before = stack.getCount();
                player.getInventory().add(stack);
                int leftover = stack.getCount();
                refundedItems += before - leftover;

                if (leftover > 0) {
                    player.drop(new ItemStack(item, leftover), false);
                    droppedItems += leftover;
                }

                remaining -= amount;
            }
        }

        return new MaterialRefundResult(refundedItems, droppedItems, warnings);
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

    private void rollback(ServerPlayer player, MaterialTransaction transaction) {
        refundMaterials(player, transaction);
    }

    private MaterialTransaction transaction(
            ServerPlayer player,
            String blueprintId,
            Map<String, Integer> consumedItems
    ) {
        List<ConsumedMaterialEntry> entries = consumedItems.entrySet()
                .stream()
                .map(entry -> new ConsumedMaterialEntry(entry.getKey(), entry.getValue()))
                .toList();
        return new MaterialTransaction(
                player.getUUID(),
                blueprintId,
                entries,
                player.level().getGameTime(),
                false
        );
    }

    public record ConsumeResult(boolean success, MaterialTransaction transaction, String message) {
        public static ConsumeResult success(MaterialTransaction transaction) {
            return new ConsumeResult(true, transaction, "");
        }

        public static ConsumeResult failed(String message) {
            return new ConsumeResult(false, null, message);
        }
    }
}

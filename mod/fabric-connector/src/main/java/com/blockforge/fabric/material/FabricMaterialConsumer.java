package com.blockforge.fabric.material;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FabricMaterialConsumer {
    public ConsumeResult consumeMaterials(ServerPlayerEntity player, MaterialReport report) {
        if (player == null) {
            return ConsumeResult.failed("This build requires a player.");
        }
        if (player.isCreative()) {
            return ConsumeResult.success(MaterialTransaction.creative(
                    player.getUuid(),
                    report.blueprintId(),
                    player.getServerWorld().getTime()
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

    public MaterialRefundResult refundMaterials(ServerPlayerEntity player, MaterialTransaction transaction) {
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
                int amount = Math.min(remaining, item.getMaxCount());
                ItemStack stack = new ItemStack(item, amount);
                int before = stack.getCount();
                player.getInventory().insertStack(stack);
                int leftover = stack.getCount();
                refundedItems += before - leftover;

                if (leftover > 0) {
                    player.dropItem(new ItemStack(item, leftover), false);
                    droppedItems += leftover;
                }

                remaining -= amount;
            }
        }

        return new MaterialRefundResult(refundedItems, droppedItems, warnings);
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

    private void rollback(ServerPlayerEntity player, MaterialTransaction transaction) {
        refundMaterials(player, transaction);
    }

    private MaterialTransaction transaction(
            ServerPlayerEntity player,
            String blueprintId,
            Map<String, Integer> consumedItems
    ) {
        List<ConsumedMaterialEntry> entries = consumedItems.entrySet()
                .stream()
                .map(entry -> new ConsumedMaterialEntry(entry.getKey(), entry.getValue()))
                .toList();
        return new MaterialTransaction(
                player.getUuid(),
                blueprintId,
                entries,
                player.getServerWorld().getTime(),
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

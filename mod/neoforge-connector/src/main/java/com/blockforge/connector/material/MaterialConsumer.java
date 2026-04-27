package com.blockforge.connector.material;

import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceRefundHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialConsumer {
    private final NeoForgeMaterialSourceRefundHandler sourceRefundHandler = new NeoForgeMaterialSourceRefundHandler();

    public ConsumeResult consume(ServerPlayer player, MaterialReport report) {
        long gameTime = player == null ? 0L : player.serverLevel().getGameTime();
        return consume(player, report.blueprintId(), gameTime, report);
    }

    public ConsumeResult consume(
            ServerPlayer player,
            String blueprintId,
            long createdAtGameTime,
            MaterialReport report
    ) {
        if (player == null) {
            return new ConsumeResult(true, null, MaterialRefundResult.empty(), "");
        }

        if (player.isCreative()) {
            return new ConsumeResult(
                    true,
                    MaterialTransaction.creative(player.getUUID(), blueprintId, createdAtGameTime),
                    MaterialRefundResult.empty(),
                    ""
            );
        }

        if (!report.enoughMaterials()) {
            return new ConsumeResult(false, null, MaterialRefundResult.empty(), "Not enough materials.");
        }

        Map<String, Integer> consumedItems = new HashMap<>();
        for (MaterialRequirement requirement : report.requirements()) {
            if (!requirement.consumable() || requirement.required() <= 0) {
                continue;
            }

            Item item = itemForId(requirement.itemId());
            if (item == Items.AIR) {
                MaterialTransaction transaction = createTransaction(player, blueprintId, createdAtGameTime, consumedItems);
                MaterialRefundResult rollback = rollbackConsumedMaterials(player, transaction);
                return new ConsumeResult(false, transaction, rollback, "Cannot consume item: " + requirement.itemId());
            }

            int consumed = consumeItem(player, item, requirement.required());
            consumedItems.merge(requirement.itemId(), consumed, Integer::sum);

            if (consumed < requirement.required()) {
                MaterialTransaction transaction = createTransaction(player, blueprintId, createdAtGameTime, consumedItems);
                MaterialRefundResult rollback = rollbackConsumedMaterials(player, transaction);
                return new ConsumeResult(false, transaction, rollback, "Could not consume enough of " + requirement.itemId());
            }
        }

        return new ConsumeResult(
                true,
                createTransaction(player, blueprintId, createdAtGameTime, consumedItems),
                MaterialRefundResult.empty(),
                ""
        );
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

    public MaterialRefundResult refundMaterials(ServerPlayer player, MaterialTransaction transaction) {
        if (player == null || transaction == null || transaction.creativeBypass() || transaction.consumedItems().isEmpty()) {
            return MaterialRefundResult.empty();
        }

        if (transaction.includesNearbyContainers()) {
            return sourceRefundHandler.refund(player, transaction, BlockForgeConfig.materialSourceConfig());
        }

        int refunded = 0;
        int dropped = 0;
        List<String> warnings = new ArrayList<>();

        for (ConsumedMaterialEntry entry : transaction.consumedItems()) {
            Item item = itemForId(entry.itemId());
            if (item == Items.AIR) {
                warnings.add("Cannot refund unknown item: " + entry.itemId());
                continue;
            }

            RefundCounts counts = refundItem(player, item, entry.count());
            refunded += counts.refundedItems();
            dropped += counts.droppedItems();
        }

        return new MaterialRefundResult(refunded, dropped, warnings);
    }

    public MaterialRefundResult rollbackConsumedMaterials(ServerPlayer player, MaterialTransaction transaction) {
        return refundMaterials(player, transaction);
    }

    private RefundCounts refundItem(ServerPlayer player, Item item, int count) {
        int remaining = count;
        int refunded = 0;
        int dropped = 0;

        while (remaining > 0) {
            ItemStack template = new ItemStack(item);
            int amount = Math.min(remaining, template.getMaxStackSize());
            ItemStack stack = new ItemStack(item, amount);
            int originalCount = stack.getCount();

            player.getInventory().add(stack);
            int leftover = stack.getCount();
            refunded += originalCount - leftover;

            if (!stack.isEmpty()) {
                dropped += stack.getCount();
                player.drop(stack.copy(), false);
                stack.setCount(0);
            }

            remaining -= amount;
        }

        return new RefundCounts(refunded, dropped);
    }

    private MaterialTransaction createTransaction(
            ServerPlayer player,
            String blueprintId,
            long createdAtGameTime,
            Map<String, Integer> consumedItems
    ) {
        List<ConsumedMaterialEntry> entries = consumedItems.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new ConsumedMaterialEntry(entry.getKey(), entry.getValue()))
                .sorted((left, right) -> left.itemId().compareTo(right.itemId()))
                .toList();

        return new MaterialTransaction(player.getUUID(), blueprintId, entries, createdAtGameTime, false);
    }

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }

        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }

    public record ConsumeResult(
            boolean success,
            MaterialTransaction transaction,
            MaterialRefundResult rollbackResult,
            String message
    ) {
        public int consumedItems() {
            return transaction == null ? 0 : transaction.totalConsumedItems();
        }
    }

    private record RefundCounts(int refundedItems, int droppedItems) {
    }
}

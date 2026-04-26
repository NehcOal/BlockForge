package com.blockforge.fabric.material.source;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class FabricMaterialSourceRefundHandler {
    private final FabricMaterialSourceScanner scanner = new FabricMaterialSourceScanner();

    public MaterialRefundResult refund(
            ServerPlayerEntity player,
            MaterialTransaction transaction,
            MaterialSourceConfig config
    ) {
        if (player == null || transaction == null || transaction.creativeBypass() || transaction.consumedItems().isEmpty()) {
            return MaterialRefundResult.none();
        }

        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        int refunded = 0;
        int dropped = 0;
        List<String> warnings = new ArrayList<>();

        for (ConsumedMaterialEntry entry : transaction.consumedItems()) {
            Item item = itemForId(entry.itemId());
            if (item == Items.AIR) {
                warnings.add("Cannot refund unknown item: " + entry.itemId());
                continue;
            }

            int remaining = entry.count();
            if (resolvedConfig.returnRefundsToOriginalSource()
                    && entry.sourceType() == MaterialSourceType.NEARBY_CONTAINER
                    && entry.source() != null) {
                FabricContainerMaterialSource container = scanner.sourceFor(player.getServerWorld(), entry.source());
                if (container != null) {
                    int inserted = container.insertItem(entry.itemId(), remaining);
                    refunded += inserted;
                    remaining -= inserted;
                }
                if (remaining > 0) {
                    warnings.add("Original material source unavailable or full: " + entry.sourceId());
                }
            }

            RefundCounts counts = refundToPlayer(player, item, remaining);
            refunded += counts.refundedItems();
            dropped += counts.droppedItems();
        }

        return new MaterialRefundResult(refunded, dropped, warnings);
    }

    private RefundCounts refundToPlayer(ServerPlayerEntity player, Item item, int count) {
        int remaining = count;
        int refunded = 0;
        int dropped = 0;

        while (remaining > 0) {
            int amount = Math.min(remaining, item.getMaxCount());
            ItemStack stack = new ItemStack(item, amount);
            int before = stack.getCount();
            player.getInventory().insertStack(stack);
            int leftover = stack.getCount();
            refunded += before - leftover;

            if (leftover > 0) {
                player.dropItem(new ItemStack(item, leftover), false);
                dropped += leftover;
            }

            remaining -= amount;
        }

        return new RefundCounts(refunded, dropped);
    }

    private Item itemForId(String itemId) {
        Identifier identifier = Identifier.tryParse(itemId);
        if (identifier == null) {
            return Items.AIR;
        }
        return Registries.ITEM.getOrEmpty(identifier).orElse(Items.AIR);
    }

    private record RefundCounts(int refundedItems, int droppedItems) {
    }
}

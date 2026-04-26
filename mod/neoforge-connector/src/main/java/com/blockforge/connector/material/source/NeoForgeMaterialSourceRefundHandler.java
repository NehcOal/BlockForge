package com.blockforge.connector.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.connector.material.ConsumedMaterialEntry;
import com.blockforge.connector.material.MaterialRefundResult;
import com.blockforge.connector.material.MaterialTransaction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeMaterialSourceRefundHandler {
    private final NeoForgeMaterialSourceScanner scanner = new NeoForgeMaterialSourceScanner();

    public MaterialRefundResult refund(
            ServerPlayer player,
            MaterialTransaction transaction,
            MaterialSourceConfig config
    ) {
        if (player == null || transaction == null || transaction.creativeBypass() || transaction.consumedItems().isEmpty()) {
            return MaterialRefundResult.empty();
        }

        MaterialSourceConfig resolvedConfig = config == null ? MaterialSourceConfig.defaults() : config;
        int refundedToPlayer = 0;
        int refundedToContainers = 0;
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
                    && entry.source() != null
                    && entry.source().type() == MaterialSourceType.NEARBY_CONTAINER) {
                NeoForgeContainerMaterialSource container = scanner.sourceFor(player.serverLevel(), entry.source());
                if (container != null) {
                    int inserted = container.insertItem(entry.itemId(), remaining);
                    refundedToContainers += inserted;
                    remaining -= inserted;
                }

                if (remaining > 0) {
                    warnings.add("Original material source unavailable or full: " + entry.sourceId());
                }
            }

            if (remaining > 0) {
                RefundCounts counts = refundToPlayer(player, item, remaining);
                refundedToPlayer += counts.refundedItems();
                dropped += counts.droppedItems();
            }
        }

        return new MaterialRefundResult(
                refundedToPlayer + refundedToContainers,
                refundedToPlayer,
                refundedToContainers,
                dropped,
                warnings
        );
    }

    private RefundCounts refundToPlayer(ServerPlayer player, Item item, int count) {
        int remaining = count;
        int refunded = 0;
        int dropped = 0;

        while (remaining > 0) {
            int amount = Math.min(remaining, new ItemStack(item).getMaxStackSize());
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

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }

    private record RefundCounts(int refundedItems, int droppedItems) {
    }
}

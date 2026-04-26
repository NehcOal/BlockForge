package com.blockforge.forge.material.source;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class ForgeMaterialSourceRefundHandler {
    private final ForgeMaterialSourceScanner scanner = new ForgeMaterialSourceScanner();

    public MaterialRefundResult refund(
            ServerPlayer player,
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
                ForgeContainerMaterialSource container = scanner.sourceFor(player.serverLevel(), entry.source());
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

    private RefundCounts refundToPlayer(ServerPlayer player, Item item, int count) {
        int remaining = count;
        int refunded = 0;
        int dropped = 0;

        while (remaining > 0) {
            int amount = Math.min(remaining, item.getDefaultMaxStackSize());
            ItemStack stack = new ItemStack(item, amount);
            int before = stack.getCount();
            player.getInventory().add(stack);
            int leftover = stack.getCount();
            refunded += before - leftover;

            if (leftover > 0) {
                player.drop(new ItemStack(item, leftover), false);
                dropped += leftover;
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

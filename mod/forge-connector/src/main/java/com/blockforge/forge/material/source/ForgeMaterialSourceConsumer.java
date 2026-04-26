package com.blockforge.forge.material.source;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.forge.material.ForgeMaterialConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForgeMaterialSourceConsumer {
    private final ForgeMaterialConsumer playerConsumer = new ForgeMaterialConsumer();
    private final ForgeMaterialSourceRefundHandler refundHandler = new ForgeMaterialSourceRefundHandler();

    public ForgeMaterialConsumer.ConsumeResult consume(
            ServerPlayer player,
            String blueprintId,
            MaterialSourceReport report,
            List<ForgeContainerMaterialSource> containers,
            MaterialSourceConfig config
    ) {
        if (player == null) {
            return ForgeMaterialConsumer.ConsumeResult.failed("This build requires a player.");
        }
        if (player.isCreative()) {
            return ForgeMaterialConsumer.ConsumeResult.success(MaterialTransaction.creative(
                    player.getUUID(),
                    blueprintId,
                    player.level().getGameTime()
            ));
        }
        if (report == null || !report.enoughMaterials()) {
            return ForgeMaterialConsumer.ConsumeResult.failed("Not enough materials.");
        }

        Map<String, ForgeContainerMaterialSource> containersById = new HashMap<>();
        for (ForgeContainerMaterialSource container : containers == null ? List.<ForgeContainerMaterialSource>of() : containers) {
            containersById.put(container.ref().id(), container);
        }

        List<ConsumedMaterialEntry> consumedEntries = new ArrayList<>();
        for (MaterialSourceItemEntry entry : report.entries()
                .stream()
                .filter(candidate -> candidate.reserved() > 0)
                .sorted(Comparator.comparing(MaterialSourceItemEntry::itemId))
                .toList()) {
            int consumed = consumeEntry(player, entry, containersById);
            if (consumed > 0) {
                consumedEntries.add(new ConsumedMaterialEntry(
                        entry.itemId(),
                        consumed,
                        entry.source(),
                        entry.source() == null ? "" : entry.source().id()
                ));
            }
            if (consumed < entry.reserved()) {
                MaterialTransaction partial = transaction(player, blueprintId, consumedEntries);
                refundHandler.refund(player, partial, config);
                return ForgeMaterialConsumer.ConsumeResult.failed("Could not consume enough of " + entry.itemId());
            }
        }

        return ForgeMaterialConsumer.ConsumeResult.success(transaction(player, blueprintId, consumedEntries));
    }

    public MaterialRefundResult refund(ServerPlayer player, MaterialTransaction transaction, MaterialSourceConfig config) {
        if (transaction != null && transaction.includesNearbyContainers()) {
            return refundHandler.refund(player, transaction, config);
        }
        return playerConsumer.refundMaterials(player, transaction);
    }

    private int consumeEntry(
            ServerPlayer player,
            MaterialSourceItemEntry entry,
            Map<String, ForgeContainerMaterialSource> containersById
    ) {
        MaterialSourceType type = entry.source() == null ? MaterialSourceType.PLAYER_INVENTORY : entry.source().type();
        if (type == MaterialSourceType.NEARBY_CONTAINER) {
            ForgeContainerMaterialSource container = containersById.get(entry.source().id());
            return container == null ? 0 : container.extractItem(entry.itemId(), entry.reserved());
        }

        Item item = itemForId(entry.itemId());
        if (item == Items.AIR) {
            return 0;
        }
        return consumeFromPlayer(player, item, entry.reserved());
    }

    private int consumeFromPlayer(ServerPlayer player, Item item, int required) {
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

    private MaterialTransaction transaction(
            ServerPlayer player,
            String blueprintId,
            List<ConsumedMaterialEntry> entries
    ) {
        MaterialSourceType sourceType = sourceType(entries);
        return new MaterialTransaction(
                player.getUUID(),
                blueprintId,
                entries,
                player.level().getGameTime(),
                false,
                sourceType,
                sourceType == MaterialSourceType.NEARBY_CONTAINER || sourceType == MaterialSourceType.MIXED
        );
    }

    private MaterialSourceType sourceType(List<ConsumedMaterialEntry> entries) {
        boolean hasPlayer = false;
        boolean hasContainer = false;
        for (ConsumedMaterialEntry entry : entries) {
            MaterialSourceType type = entry.sourceType();
            hasPlayer |= type == MaterialSourceType.PLAYER_INVENTORY;
            hasContainer |= type == MaterialSourceType.NEARBY_CONTAINER;
        }
        if (hasPlayer && hasContainer) {
            return MaterialSourceType.MIXED;
        }
        return hasContainer ? MaterialSourceType.NEARBY_CONTAINER : MaterialSourceType.PLAYER_INVENTORY;
    }

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }
}

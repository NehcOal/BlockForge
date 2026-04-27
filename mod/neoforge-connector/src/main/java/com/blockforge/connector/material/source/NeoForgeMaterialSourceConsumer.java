package com.blockforge.connector.material.source;

import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.connector.material.ConsumedMaterialEntry;
import com.blockforge.connector.material.MaterialConsumer;
import com.blockforge.connector.material.MaterialRefundResult;
import com.blockforge.connector.material.MaterialTransaction;
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

public class NeoForgeMaterialSourceConsumer {
    private final MaterialConsumer playerConsumer = new MaterialConsumer();
    private final NeoForgeMaterialSourceRefundHandler refundHandler = new NeoForgeMaterialSourceRefundHandler();

    public MaterialConsumer.ConsumeResult consume(
            ServerPlayer player,
            String blueprintId,
            long createdAtGameTime,
            MaterialSourceReport report,
            List<NeoForgeContainerMaterialSource> containers,
            MaterialSourceConfig config
    ) {
        if (player == null) {
            return new MaterialConsumer.ConsumeResult(true, null, MaterialRefundResult.empty(), "");
        }

        if (player.isCreative()) {
            return new MaterialConsumer.ConsumeResult(
                    true,
                    MaterialTransaction.creative(player.getUUID(), blueprintId, createdAtGameTime),
                    MaterialRefundResult.empty(),
                    ""
            );
        }

        if (report == null || !report.enoughMaterials()) {
            return new MaterialConsumer.ConsumeResult(false, null, MaterialRefundResult.empty(), "Not enough materials.");
        }

        Map<String, NeoForgeContainerMaterialSource> containersById = new HashMap<>();
        for (NeoForgeContainerMaterialSource container : containers == null ? List.<NeoForgeContainerMaterialSource>of() : containers) {
            containersById.put(container.ref().id(), container);
        }

        List<ConsumedMaterialEntry> consumedEntries = new ArrayList<>();
        for (MaterialSourceItemEntry entry : report.entries()
                .stream()
                .filter(sourceEntry -> sourceEntry.reserved() > 0)
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
                MaterialTransaction partial = createTransaction(
                        player,
                        blueprintId,
                        createdAtGameTime,
                        consumedEntries
                );
                MaterialRefundResult rollback = refundHandler.refund(player, partial, config);
                return new MaterialConsumer.ConsumeResult(false, partial, rollback, "Could not consume enough of " + entry.itemId());
            }
        }

        return new MaterialConsumer.ConsumeResult(
                true,
                createTransaction(player, blueprintId, createdAtGameTime, consumedEntries),
                MaterialRefundResult.empty(),
                ""
        );
    }

    public MaterialRefundResult rollbackConsumedMaterials(
            ServerPlayer player,
            MaterialTransaction transaction,
            MaterialSourceConfig config
    ) {
        if (transaction != null && transaction.includesNearbyContainers()) {
            return refundHandler.refund(player, transaction, config);
        }
        return playerConsumer.rollbackConsumedMaterials(player, transaction);
    }

    private int consumeEntry(
            ServerPlayer player,
            MaterialSourceItemEntry entry,
            Map<String, NeoForgeContainerMaterialSource> containersById
    ) {
        MaterialSourceType type = entry.source() == null ? MaterialSourceType.PLAYER_INVENTORY : entry.source().type();
        if (type == MaterialSourceType.NEARBY_CONTAINER) {
            NeoForgeContainerMaterialSource container = containersById.get(entry.source().id());
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

    private MaterialTransaction createTransaction(
            ServerPlayer player,
            String blueprintId,
            long createdAtGameTime,
            List<ConsumedMaterialEntry> consumedEntries
    ) {
        MaterialSourceType sourceType = sourceType(consumedEntries);
        return new MaterialTransaction(
                player.getUUID(),
                blueprintId,
                consumedEntries,
                createdAtGameTime,
                false,
                sourceType,
                sourceType == MaterialSourceType.NEARBY_CONTAINER || sourceType == MaterialSourceType.MIXED
        );
    }

    private MaterialSourceType sourceType(List<ConsumedMaterialEntry> entries) {
        boolean hasPlayer = false;
        boolean hasContainer = false;
        for (ConsumedMaterialEntry entry : entries) {
            MaterialSourceType type = entry.source() == null ? MaterialSourceType.PLAYER_INVENTORY : entry.source().type();
            hasPlayer |= type == MaterialSourceType.PLAYER_INVENTORY;
            hasContainer |= type == MaterialSourceType.NEARBY_CONTAINER;
        }

        if (hasPlayer && hasContainer) {
            return MaterialSourceType.MIXED;
        }
        if (hasContainer) {
            return MaterialSourceType.NEARBY_CONTAINER;
        }
        return MaterialSourceType.PLAYER_INVENTORY;
    }

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }
}

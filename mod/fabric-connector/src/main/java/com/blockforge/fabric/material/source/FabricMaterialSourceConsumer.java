package com.blockforge.fabric.material.source;

import com.blockforge.common.material.ConsumedMaterialEntry;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceConfig;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.fabric.material.FabricMaterialConsumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricMaterialSourceConsumer {
    private final FabricMaterialConsumer playerConsumer = new FabricMaterialConsumer();
    private final FabricMaterialSourceRefundHandler refundHandler = new FabricMaterialSourceRefundHandler();

    public FabricMaterialConsumer.ConsumeResult consume(
            ServerPlayerEntity player,
            String blueprintId,
            MaterialSourceReport report,
            List<FabricContainerMaterialSource> containers,
            MaterialSourceConfig config
    ) {
        if (player == null) {
            return FabricMaterialConsumer.ConsumeResult.failed("This build requires a player.");
        }
        if (player.isCreative()) {
            return FabricMaterialConsumer.ConsumeResult.success(MaterialTransaction.creative(
                    player.getUuid(),
                    blueprintId,
                    player.getServerWorld().getTime()
            ));
        }
        if (report == null || !report.enoughMaterials()) {
            return FabricMaterialConsumer.ConsumeResult.failed("Not enough materials.");
        }

        Map<String, FabricContainerMaterialSource> containersById = new HashMap<>();
        for (FabricContainerMaterialSource container : containers == null ? List.<FabricContainerMaterialSource>of() : containers) {
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
                return FabricMaterialConsumer.ConsumeResult.failed("Could not consume enough of " + entry.itemId());
            }
        }

        return FabricMaterialConsumer.ConsumeResult.success(transaction(player, blueprintId, consumedEntries));
    }

    public MaterialRefundResult refund(ServerPlayerEntity player, MaterialTransaction transaction, MaterialSourceConfig config) {
        if (transaction != null && transaction.includesNearbyContainers()) {
            return refundHandler.refund(player, transaction, config);
        }
        return playerConsumer.refundMaterials(player, transaction);
    }

    private int consumeEntry(
            ServerPlayerEntity player,
            MaterialSourceItemEntry entry,
            Map<String, FabricContainerMaterialSource> containersById
    ) {
        MaterialSourceType type = entry.source() == null ? MaterialSourceType.PLAYER_INVENTORY : entry.source().type();
        if (type == MaterialSourceType.NEARBY_CONTAINER) {
            FabricContainerMaterialSource container = containersById.get(entry.source().id());
            return container == null ? 0 : container.extractItem(entry.itemId(), entry.reserved());
        }

        Item item = itemForId(entry.itemId());
        if (item == Items.AIR) {
            return 0;
        }
        return consumeFromPlayer(player, item, entry.reserved());
    }

    private int consumeFromPlayer(ServerPlayerEntity player, Item item, int required) {
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

    private MaterialTransaction transaction(
            ServerPlayerEntity player,
            String blueprintId,
            List<ConsumedMaterialEntry> entries
    ) {
        MaterialSourceType sourceType = sourceType(entries);
        return new MaterialTransaction(
                player.getUuid(),
                blueprintId,
                entries,
                player.getServerWorld().getTime(),
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
        Identifier identifier = Identifier.tryParse(itemId);
        if (identifier == null) {
            return Items.AIR;
        }
        return Registries.ITEM.getOrEmpty(identifier).orElse(Items.AIR);
    }
}

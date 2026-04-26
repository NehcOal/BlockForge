package com.blockforge.connector.material.source;

import com.blockforge.common.material.source.MaterialSourceRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

public class NeoForgeContainerMaterialSource {
    private final ServerLevel level;
    private final BlockPos pos;
    private final MaterialSourceRef ref;
    private final IItemHandler itemHandler;

    public NeoForgeContainerMaterialSource(
            ServerLevel level,
            BlockPos pos,
            MaterialSourceRef ref,
            IItemHandler itemHandler
    ) {
        this.level = level;
        this.pos = pos;
        this.ref = ref;
        this.itemHandler = itemHandler;
    }

    public ServerLevel level() {
        return level;
    }

    public BlockPos pos() {
        return pos;
    }

    public MaterialSourceRef ref() {
        return ref;
    }

    public IItemHandler itemHandler() {
        return itemHandler;
    }

    public int countItem(String itemId) {
        Item item = itemForId(itemId);
        if (item == Items.AIR) {
            return 0;
        }

        int count = 0;
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public int extractItem(String itemId, int amount) {
        Item item = itemForId(itemId);
        if (item == Items.AIR || amount <= 0) {
            return 0;
        }

        int remaining = amount;
        int extracted = 0;
        for (int slot = 0; slot < itemHandler.getSlots() && remaining > 0; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.isEmpty() || !stack.is(item)) {
                continue;
            }

            int requested = Math.min(remaining, stack.getCount());
            ItemStack extractedStack = itemHandler.extractItem(slot, requested, false);
            int count = extractedStack.getCount();
            extracted += count;
            remaining -= count;
        }
        return extracted;
    }

    public int insertItem(String itemId, int amount) {
        Item item = itemForId(itemId);
        if (item == Items.AIR || amount <= 0) {
            return 0;
        }

        int remaining = amount;
        for (int slot = 0; slot < itemHandler.getSlots() && remaining > 0; slot++) {
            ItemStack stack = new ItemStack(item, remaining);
            ItemStack leftover = itemHandler.insertItem(slot, stack, false);
            remaining = leftover.getCount();
        }
        return amount - remaining;
    }

    private Item itemForId(String itemId) {
        ResourceLocation location = ResourceLocation.tryParse(itemId);
        if (location == null) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR);
    }
}

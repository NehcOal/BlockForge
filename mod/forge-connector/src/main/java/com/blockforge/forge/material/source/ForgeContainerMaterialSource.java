package com.blockforge.forge.material.source;

import com.blockforge.common.material.source.MaterialSourceRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;

public class ForgeContainerMaterialSource {
    private final ServerLevel level;
    private final BlockPos pos;
    private final MaterialSourceRef ref;
    private final IItemHandler itemHandler;

    public ForgeContainerMaterialSource(ServerLevel level, BlockPos pos, MaterialSourceRef ref, IItemHandler itemHandler) {
        this.level = level;
        this.pos = pos.immutable();
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

            ItemStack extractedStack = itemHandler.extractItem(slot, Math.min(remaining, stack.getCount()), false);
            extracted += extractedStack.getCount();
            remaining -= extractedStack.getCount();
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
            ItemStack leftover = itemHandler.insertItem(slot, new ItemStack(item, remaining), false);
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

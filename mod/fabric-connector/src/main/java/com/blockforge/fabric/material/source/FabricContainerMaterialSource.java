package com.blockforge.fabric.material.source;

import com.blockforge.common.material.source.MaterialSourceRef;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;

public class FabricContainerMaterialSource {
    private final ServerWorld world;
    private final BlockPos pos;
    private final MaterialSourceRef ref;
    private final Inventory inventory;

    public FabricContainerMaterialSource(ServerWorld world, BlockPos pos, MaterialSourceRef ref, Inventory inventory) {
        this.world = world;
        this.pos = pos.toImmutable();
        this.ref = ref;
        this.inventory = inventory;
    }

    public ServerWorld world() {
        return world;
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
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (!stack.isEmpty() && stack.isOf(item)) {
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
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            ItemStack stack = inventory.getStack(slot);
            if (stack.isEmpty() || !stack.isOf(item)) {
                continue;
            }
            if (!canExtract(slot, stack)) {
                continue;
            }

            int count = Math.min(remaining, stack.getCount());
            stack.decrement(count);
            inventory.markDirty();
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
        for (int slot = 0; slot < inventory.size() && remaining > 0; slot++) {
            ItemStack existing = inventory.getStack(slot);
            if (existing.isEmpty()) {
                int inserted = Math.min(remaining, item.getMaxCount());
                ItemStack candidate = new ItemStack(item, inserted);
                if (!canInsert(slot, candidate)) {
                    continue;
                }
                inventory.setStack(slot, candidate);
                inventory.markDirty();
                remaining -= inserted;
                continue;
            }

            if (!existing.isOf(item)
                    || existing.getCount() >= Math.min(existing.getMaxCount(), inventory.getMaxCountPerStack())
                    || !canInsert(slot, new ItemStack(item, 1))) {
                continue;
            }

            int limit = Math.min(existing.getMaxCount(), inventory.getMaxCountPerStack());
            int inserted = Math.min(remaining, limit - existing.getCount());
            existing.increment(inserted);
            inventory.markDirty();
            remaining -= inserted;
        }
        return amount - remaining;
    }

    private boolean canExtract(int slot, ItemStack stack) {
        if (inventory instanceof SidedInventory sidedInventory) {
            for (Direction direction : Direction.values()) {
                if (isAvailableFromSide(sidedInventory, slot, direction)
                        && sidedInventory.canExtract(slot, stack, direction)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean canInsert(int slot, ItemStack stack) {
        if (!inventory.isValid(slot, stack)) {
            return false;
        }
        if (inventory instanceof SidedInventory sidedInventory) {
            for (Direction direction : Direction.values()) {
                if (isAvailableFromSide(sidedInventory, slot, direction)
                        && sidedInventory.canInsert(slot, stack, direction)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean isAvailableFromSide(SidedInventory inventory, int slot, Direction direction) {
        for (int availableSlot : inventory.getAvailableSlots(direction)) {
            if (availableSlot == slot) {
                return true;
            }
        }
        return false;
    }

    private Item itemForId(String itemId) {
        Identifier identifier = Identifier.tryParse(itemId);
        if (identifier == null) {
            return Items.AIR;
        }
        return Registries.ITEM.getOrEmpty(identifier).orElse(Items.AIR);
    }
}

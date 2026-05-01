package com.blockforge.common.gameplay;

import java.util.ArrayList;
import java.util.List;

public final class MaterialCacheInventory {
    public static final int DEFAULT_SLOT_COUNT = 27;
    public static final int DEFAULT_MAX_STACK_SIZE = 64;

    private final List<MaterialCacheInventorySlot> slots;

    public MaterialCacheInventory(List<MaterialCacheInventorySlot> slots) {
        this.slots = normalize(slots == null ? List.of() : slots);
    }

    public static MaterialCacheInventory empty(int slotCount) {
        List<MaterialCacheInventorySlot> slots = new ArrayList<>();
        for (int index = 0; index < Math.max(0, slotCount); index++) {
            slots.add(new MaterialCacheInventorySlot(index, "", 0, DEFAULT_MAX_STACK_SIZE));
        }
        return new MaterialCacheInventory(slots);
    }

    public List<MaterialCacheInventorySlot> slots() {
        return List.copyOf(slots);
    }

    public int usedSlots() {
        return (int) slots.stream().filter(slot -> !slot.empty()).count();
    }

    public int countItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return 0;
        }
        return slots.stream()
                .filter(slot -> itemId.equals(slot.itemId()))
                .mapToInt(MaterialCacheInventorySlot::count)
                .sum();
    }

    public InsertResult insert(String itemId, int count) {
        if (itemId == null || itemId.isBlank() || count <= 0) {
            return new InsertResult(this, 0, Math.max(0, count));
        }
        List<MaterialCacheInventorySlot> next = new ArrayList<>(slots);
        int remaining = count;
        int inserted = 0;

        for (int index = 0; index < next.size() && remaining > 0; index++) {
            MaterialCacheInventorySlot slot = next.get(index);
            if (!slot.canMerge(itemId)) {
                continue;
            }
            int moved = Math.min(remaining, slot.space());
            next.set(index, new MaterialCacheInventorySlot(slot.slot(), slot.itemId(), slot.count() + moved, slot.maxCount()));
            inserted += moved;
            remaining -= moved;
        }

        for (int index = 0; index < next.size() && remaining > 0; index++) {
            MaterialCacheInventorySlot slot = next.get(index);
            if (!slot.empty()) {
                continue;
            }
            int moved = Math.min(remaining, slot.maxCount());
            next.set(index, new MaterialCacheInventorySlot(slot.slot(), itemId, moved, slot.maxCount()));
            inserted += moved;
            remaining -= moved;
        }

        return new InsertResult(new MaterialCacheInventory(next), inserted, remaining);
    }

    public ExtractResult extract(String itemId, int count) {
        if (itemId == null || itemId.isBlank() || count <= 0) {
            return new ExtractResult(this, 0);
        }
        List<MaterialCacheInventorySlot> next = new ArrayList<>(slots);
        int remaining = count;
        int extracted = 0;
        for (int index = 0; index < next.size() && remaining > 0; index++) {
            MaterialCacheInventorySlot slot = next.get(index);
            if (!itemId.equals(slot.itemId())) {
                continue;
            }
            int moved = Math.min(remaining, slot.count());
            int nextCount = slot.count() - moved;
            next.set(index, nextCount == 0
                    ? new MaterialCacheInventorySlot(slot.slot(), "", 0, slot.maxCount())
                    : new MaterialCacheInventorySlot(slot.slot(), slot.itemId(), nextCount, slot.maxCount()));
            extracted += moved;
            remaining -= moved;
        }
        return new ExtractResult(new MaterialCacheInventory(next), extracted);
    }

    public List<MaterialCacheInventorySlot> dropsOnBreak() {
        return slots.stream().filter(slot -> !slot.empty()).toList();
    }

    private static List<MaterialCacheInventorySlot> normalize(List<MaterialCacheInventorySlot> input) {
        List<MaterialCacheInventorySlot> normalized = new ArrayList<>();
        for (int index = 0; index < input.size(); index++) {
            MaterialCacheInventorySlot slot = input.get(index);
            normalized.add(new MaterialCacheInventorySlot(index, slot.itemId(), slot.count(), slot.maxCount()));
        }
        return normalized;
    }

    public record InsertResult(MaterialCacheInventory inventory, int inserted, int remainder) {
    }

    public record ExtractResult(MaterialCacheInventory inventory, int extracted) {
    }
}

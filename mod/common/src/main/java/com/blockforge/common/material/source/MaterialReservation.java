package com.blockforge.common.material.source;

import java.util.List;
import java.util.UUID;

public record MaterialReservation(
        String reservationId,
        UUID playerId,
        String blueprintId,
        List<MaterialSourceItemEntry> reservedItems,
        long createdAtGameTime
) {
    public MaterialReservation {
        reservationId = reservationId == null ? "" : reservationId;
        blueprintId = blueprintId == null ? "" : blueprintId;
        reservedItems = reservedItems == null ? List.of() : List.copyOf(reservedItems);
    }
}

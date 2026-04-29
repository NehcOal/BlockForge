package com.blockforge.common.serverplay;

import java.util.ArrayList;
import java.util.List;

public final class BuildAuditLog {
    private final List<BuildAuditEntry> entries = new ArrayList<>();

    public void record(BuildAuditEntry entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    public List<BuildAuditEntry> entries() {
        return List.copyOf(entries);
    }

    public List<BuildAuditEntry> byPlayer(String playerId) {
        String resolved = playerId == null ? "" : playerId;
        return entries.stream()
                .filter(entry -> entry.playerId().equals(resolved) || entry.playerName().equals(resolved))
                .toList();
    }
}

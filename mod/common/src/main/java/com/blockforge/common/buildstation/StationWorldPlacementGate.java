package com.blockforge.common.buildstation;

public final class StationWorldPlacementGate {
    private StationWorldPlacementGate() {
    }

    public static StationWorldPlacementDecision evaluate(StationWorldPlacementContext context) {
        StationWorldPlacementContext resolved = context == null ? StationWorldPlacementContext.ready() : context;
        if (!resolved.sameDimension()) {
            return StationWorldPlacementDecision.fail("wrong_dimension", "Station jobs cannot place blocks across dimensions.");
        }
        if (!resolved.loadedChunk()) {
            return StationWorldPlacementDecision.pause("chunk_unloaded", "Target chunk is not loaded; station will not force-load chunks.");
        }
        if (!resolved.protectionAllowed()) {
            return StationWorldPlacementDecision.fail("protection_denied", "Protection preflight denied this station batch.");
        }
        if (!resolved.quotaAllowed()) {
            return StationWorldPlacementDecision.pause("quota_denied", "Server quota denied this station batch.");
        }
        if (!resolved.cooldownReady()) {
            return StationWorldPlacementDecision.pause("cooldown", "Station cooldown is still active.");
        }
        if (!resolved.materialsReserved()) {
            return StationWorldPlacementDecision.pause("materials_missing", "Required materials were not reserved for this batch.");
        }
        if (!resolved.replacePolicyAllowed()) {
            return StationWorldPlacementDecision.pause("replace_policy", "Replace policy skipped this station batch.");
        }
        if (resolved.blockEntityProtected()) {
            return StationWorldPlacementDecision.pause("block_entity_protected", "Target position contains a protected block entity.");
        }
        return StationWorldPlacementDecision.allow();
    }
}

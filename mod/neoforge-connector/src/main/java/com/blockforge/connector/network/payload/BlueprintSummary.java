package com.blockforge.connector.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;

public record BlueprintSummary(
        String id,
        String name,
        int schemaVersion,
        int width,
        int height,
        int depth,
        int blockCount,
        boolean hasBlockStates
) {
    public static BlueprintSummary read(RegistryFriendlyByteBuf buffer) {
        return new BlueprintSummary(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean()
        );
    }

    public static void write(RegistryFriendlyByteBuf buffer, BlueprintSummary summary) {
        buffer.writeUtf(summary.id());
        buffer.writeUtf(summary.name());
        buffer.writeVarInt(summary.schemaVersion());
        buffer.writeVarInt(summary.width());
        buffer.writeVarInt(summary.height());
        buffer.writeVarInt(summary.depth());
        buffer.writeVarInt(summary.blockCount());
        buffer.writeBoolean(summary.hasBlockStates());
    }

    public String sizeLabel() {
        return width + " x " + height + " x " + depth;
    }
}

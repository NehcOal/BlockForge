package com.blockforge.connector.network.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record BlueprintSummary(
        String id,
        String name,
        int schemaVersion,
        int width,
        int height,
        int depth,
        int blockCount,
        boolean hasBlockStates,
        String sourceType,
        String sourceId,
        int warningCount,
        List<String> tags
) {
    public BlueprintSummary {
        sourceType = sourceType == null || sourceType.isBlank() ? "loose" : sourceType;
        sourceId = sourceId == null ? "" : sourceId;
        tags = tags == null ? List.of() : List.copyOf(tags);
    }

    public static BlueprintSummary read(RegistryFriendlyByteBuf buffer) {
        String id = buffer.readUtf();
        String name = buffer.readUtf();
        int schemaVersion = buffer.readVarInt();
        int width = buffer.readVarInt();
        int height = buffer.readVarInt();
        int depth = buffer.readVarInt();
        int blockCount = buffer.readVarInt();
        boolean hasBlockStates = buffer.readBoolean();
        String sourceType = buffer.readUtf();
        String sourceId = buffer.readUtf();
        int warningCount = buffer.readVarInt();
        int tagCount = buffer.readVarInt();
        List<String> tags = new ArrayList<>(tagCount);
        for (int index = 0; index < tagCount; index++) {
            tags.add(buffer.readUtf());
        }
        return new BlueprintSummary(
                id,
                name,
                schemaVersion,
                width,
                height,
                depth,
                blockCount,
                hasBlockStates,
                sourceType,
                sourceId,
                warningCount,
                tags
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
        buffer.writeUtf(summary.sourceType());
        buffer.writeUtf(summary.sourceId());
        buffer.writeVarInt(summary.warningCount());
        buffer.writeVarInt(summary.tags().size());
        for (String tag : summary.tags()) {
            buffer.writeUtf(tag);
        }
    }

    public String sizeLabel() {
        return width + " x " + height + " x " + depth;
    }
}

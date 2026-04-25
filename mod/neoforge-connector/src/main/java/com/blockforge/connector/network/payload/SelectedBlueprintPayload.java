package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectedBlueprintPayload(
        String blueprintId,
        String blueprintName,
        int width,
        int height,
        int depth,
        int rotation
) implements CustomPacketPayload {
    public static final Type<SelectedBlueprintPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "selected_blueprint")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectedBlueprintPayload> STREAM_CODEC = StreamCodec.of(
            SelectedBlueprintPayload::write,
            SelectedBlueprintPayload::read
    );

    private static SelectedBlueprintPayload read(RegistryFriendlyByteBuf buffer) {
        return new SelectedBlueprintPayload(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt()
        );
    }

    private static void write(RegistryFriendlyByteBuf buffer, SelectedBlueprintPayload payload) {
        buffer.writeUtf(payload.blueprintId());
        buffer.writeUtf(payload.blueprintName());
        buffer.writeVarInt(payload.width());
        buffer.writeVarInt(payload.height());
        buffer.writeVarInt(payload.depth());
        buffer.writeVarInt(payload.rotation());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

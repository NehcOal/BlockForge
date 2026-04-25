package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClearPreviewPayload(String reason) implements CustomPacketPayload {
    public static final Type<ClearPreviewPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "clear_preview")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearPreviewPayload> STREAM_CODEC = StreamCodec.of(
            ClearPreviewPayload::write,
            ClearPreviewPayload::read
    );

    public ClearPreviewPayload() {
        this("");
    }

    private static ClearPreviewPayload read(RegistryFriendlyByteBuf buffer) {
        return new ClearPreviewPayload(buffer.readUtf());
    }

    private static void write(RegistryFriendlyByteBuf buffer, ClearPreviewPayload payload) {
        buffer.writeUtf(payload.reason());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

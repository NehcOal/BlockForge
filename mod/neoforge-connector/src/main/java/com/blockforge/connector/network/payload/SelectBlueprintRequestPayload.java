package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectBlueprintRequestPayload(
        String blueprintId,
        int rotation
) implements CustomPacketPayload {
    public static final Type<SelectBlueprintRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "select_blueprint_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectBlueprintRequestPayload> STREAM_CODEC = StreamCodec.of(
            SelectBlueprintRequestPayload::write,
            SelectBlueprintRequestPayload::read
    );

    private static SelectBlueprintRequestPayload read(RegistryFriendlyByteBuf buffer) {
        return new SelectBlueprintRequestPayload(buffer.readUtf(), buffer.readVarInt());
    }

    private static void write(RegistryFriendlyByteBuf buffer, SelectBlueprintRequestPayload payload) {
        buffer.writeUtf(payload.blueprintId());
        buffer.writeVarInt(payload.rotation());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

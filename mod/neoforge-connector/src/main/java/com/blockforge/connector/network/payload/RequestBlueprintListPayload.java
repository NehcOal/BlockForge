package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestBlueprintListPayload(boolean openScreen) implements CustomPacketPayload {
    public static final Type<RequestBlueprintListPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "request_blueprint_list")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlueprintListPayload> STREAM_CODEC = StreamCodec.of(
            RequestBlueprintListPayload::write,
            RequestBlueprintListPayload::read
    );

    private static RequestBlueprintListPayload read(RegistryFriendlyByteBuf buffer) {
        return new RequestBlueprintListPayload(buffer.readBoolean());
    }

    private static void write(RegistryFriendlyByteBuf buffer, RequestBlueprintListPayload payload) {
        buffer.writeBoolean(payload.openScreen());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

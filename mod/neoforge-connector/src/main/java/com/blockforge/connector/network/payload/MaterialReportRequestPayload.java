package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MaterialReportRequestPayload(String blueprintId) implements CustomPacketPayload {
    public static final Type<MaterialReportRequestPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "material_report_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialReportRequestPayload> STREAM_CODEC = StreamCodec.of(
            MaterialReportRequestPayload::write,
            MaterialReportRequestPayload::read
    );

    private static MaterialReportRequestPayload read(RegistryFriendlyByteBuf buffer) {
        return new MaterialReportRequestPayload(buffer.readUtf());
    }

    private static void write(RegistryFriendlyByteBuf buffer, MaterialReportRequestPayload payload) {
        buffer.writeUtf(payload.blueprintId());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

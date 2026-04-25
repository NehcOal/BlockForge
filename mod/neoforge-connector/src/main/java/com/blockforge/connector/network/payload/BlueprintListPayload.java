package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record BlueprintListPayload(
        List<BlueprintSummary> blueprints,
        boolean openScreen
) implements CustomPacketPayload {
    public static final Type<BlueprintListPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "blueprint_list")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintListPayload> STREAM_CODEC = StreamCodec.of(
            BlueprintListPayload::write,
            BlueprintListPayload::read
    );

    public BlueprintListPayload {
        blueprints = List.copyOf(blueprints);
    }

    private static BlueprintListPayload read(RegistryFriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        List<BlueprintSummary> summaries = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            summaries.add(BlueprintSummary.read(buffer));
        }
        return new BlueprintListPayload(summaries, buffer.readBoolean());
    }

    private static void write(RegistryFriendlyByteBuf buffer, BlueprintListPayload payload) {
        buffer.writeVarInt(payload.blueprints().size());
        for (BlueprintSummary summary : payload.blueprints()) {
            BlueprintSummary.write(buffer, summary);
        }
        buffer.writeBoolean(payload.openScreen());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

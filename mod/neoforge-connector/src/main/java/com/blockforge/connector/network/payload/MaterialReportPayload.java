package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record MaterialReportPayload(
        String blueprintId,
        boolean enoughMaterials,
        int totalRequiredItems,
        int totalAvailableItems,
        List<MaterialRequirementSummary> requirements
) implements CustomPacketPayload {
    public static final Type<MaterialReportPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "material_report")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialReportPayload> STREAM_CODEC = StreamCodec.of(
            MaterialReportPayload::write,
            MaterialReportPayload::read
    );

    public MaterialReportPayload {
        requirements = List.copyOf(requirements);
    }

    private static MaterialReportPayload read(RegistryFriendlyByteBuf buffer) {
        String blueprintId = buffer.readUtf();
        boolean enough = buffer.readBoolean();
        int totalRequired = buffer.readVarInt();
        int totalAvailable = buffer.readVarInt();
        int count = buffer.readVarInt();
        List<MaterialRequirementSummary> requirements = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            requirements.add(MaterialRequirementSummary.read(buffer));
        }
        return new MaterialReportPayload(blueprintId, enough, totalRequired, totalAvailable, requirements);
    }

    private static void write(RegistryFriendlyByteBuf buffer, MaterialReportPayload payload) {
        buffer.writeUtf(payload.blueprintId());
        buffer.writeBoolean(payload.enoughMaterials());
        buffer.writeVarInt(payload.totalRequiredItems());
        buffer.writeVarInt(payload.totalAvailableItems());
        buffer.writeVarInt(payload.requirements().size());
        for (MaterialRequirementSummary requirement : payload.requirements()) {
            MaterialRequirementSummary.write(buffer, requirement);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

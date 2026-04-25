package com.blockforge.connector.network.payload;

import com.blockforge.connector.material.MaterialRequirement;
import net.minecraft.network.RegistryFriendlyByteBuf;

public record MaterialRequirementSummary(
        String itemId,
        int required,
        int available,
        int missing
) {
    public static MaterialRequirementSummary fromRequirement(MaterialRequirement requirement) {
        return new MaterialRequirementSummary(
                requirement.itemId(),
                requirement.required(),
                requirement.available(),
                requirement.missing()
        );
    }

    public static MaterialRequirementSummary read(RegistryFriendlyByteBuf buffer) {
        return new MaterialRequirementSummary(
                buffer.readUtf(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt()
        );
    }

    public static void write(RegistryFriendlyByteBuf buffer, MaterialRequirementSummary summary) {
        buffer.writeUtf(summary.itemId());
        buffer.writeVarInt(summary.required());
        buffer.writeVarInt(summary.available());
        buffer.writeVarInt(summary.missing());
    }
}

package com.blockforge.connector.network.payload;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.common.gui.BlueprintGuiQuery;
import com.blockforge.common.gui.BlueprintSortMode;
import com.blockforge.common.gui.BlueprintSourceFilter;
import com.blockforge.common.gui.BlueprintWarningFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestBlueprintListPayload(
        boolean openScreen,
        String searchText,
        BlueprintSourceFilter sourceFilter,
        BlueprintWarningFilter warningFilter,
        BlueprintSortMode sortMode,
        int page,
        int pageSize
) implements CustomPacketPayload {
    public static final Type<RequestBlueprintListPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(BlockForgeConnector.MOD_ID, "request_blueprint_list")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestBlueprintListPayload> STREAM_CODEC = StreamCodec.of(
            RequestBlueprintListPayload::write,
            RequestBlueprintListPayload::read
    );

    public RequestBlueprintListPayload(boolean openScreen) {
        this(
                openScreen,
                "",
                BlueprintSourceFilter.ALL,
                BlueprintWarningFilter.ALL,
                BlueprintSortMode.NAME_ASC,
                0,
                BlueprintGuiQuery.DEFAULT_PAGE_SIZE
        );
    }

    public static BlueprintGuiQuery defaultQuery() {
        return BlueprintGuiQuery.firstPage();
    }

    public BlueprintGuiQuery query() {
        return new BlueprintGuiQuery(searchText, sourceFilter, warningFilter, sortMode, page, pageSize);
    }

    private static RequestBlueprintListPayload read(RegistryFriendlyByteBuf buffer) {
        return new RequestBlueprintListPayload(
                buffer.readBoolean(),
                buffer.readUtf(),
                buffer.readEnum(BlueprintSourceFilter.class),
                buffer.readEnum(BlueprintWarningFilter.class),
                buffer.readEnum(BlueprintSortMode.class),
                buffer.readVarInt(),
                buffer.readVarInt()
        );
    }

    private static void write(RegistryFriendlyByteBuf buffer, RequestBlueprintListPayload payload) {
        buffer.writeBoolean(payload.openScreen());
        buffer.writeUtf(payload.searchText());
        buffer.writeEnum(payload.sourceFilter());
        buffer.writeEnum(payload.warningFilter());
        buffer.writeEnum(payload.sortMode());
        buffer.writeVarInt(payload.page());
        buffer.writeVarInt(payload.pageSize());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

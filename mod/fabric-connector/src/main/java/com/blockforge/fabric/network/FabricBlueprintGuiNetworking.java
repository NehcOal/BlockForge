package com.blockforge.fabric.network;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.gui.BlueprintGuiQuery;
import com.blockforge.common.gui.BlueprintGuiQueryService;
import com.blockforge.common.gui.BlueprintListView;
import com.blockforge.common.gui.BlueprintSortMode;
import com.blockforge.common.gui.BlueprintSourceFilter;
import com.blockforge.common.gui.BlueprintSummary;
import com.blockforge.common.gui.BlueprintWarningFilter;
import com.blockforge.common.gui.PagedBlueprintResult;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.common.selection.SelectionRequest;
import com.blockforge.fabric.BlockForgeFabric;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.player.FabricPlayerSelectionManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class FabricBlueprintGuiNetworking {
    public static final CustomPayload.Id<BlueprintListRequestPayload> BLUEPRINT_LIST_REQUEST_ID = id("blueprint_list_request");
    public static final CustomPayload.Id<BlueprintListPayload> BLUEPRINT_LIST_ID = id("blueprint_list");
    public static final CustomPayload.Id<SelectBlueprintRequestPayload> SELECT_BLUEPRINT_REQUEST_ID = id("select_blueprint_request");
    public static final CustomPayload.Id<SelectionResultPayload> SELECTION_RESULT_ID = id("selection_result");
    public static final CustomPayload.Id<PreviewSelectionPayload> PREVIEW_SELECTION_ID = id("preview_selection");
    public static final CustomPayload.Id<ClearPreviewPayload> CLEAR_PREVIEW_ID = id("clear_preview");

    private FabricBlueprintGuiNetworking() {
    }

    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(BLUEPRINT_LIST_REQUEST_ID, BlueprintListRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BLUEPRINT_LIST_ID, BlueprintListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SELECT_BLUEPRINT_REQUEST_ID, SelectBlueprintRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SELECTION_RESULT_ID, SelectionResultPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PREVIEW_SELECTION_ID, PreviewSelectionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CLEAR_PREVIEW_ID, ClearPreviewPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                BLUEPRINT_LIST_REQUEST_ID,
                (payload, context) -> sendBlueprintList(context.player(), payload.openScreen(), payload.query())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                SELECT_BLUEPRINT_REQUEST_ID,
                (payload, context) -> handleSelectionRequest(context.player(), payload)
        );
    }

    public static void sendBlueprintList(ServerPlayerEntity player, boolean openScreen) {
        sendBlueprintList(player, openScreen, BlueprintGuiQuery.firstPage());
    }

    public static void sendBlueprintList(ServerPlayerEntity player, boolean openScreen, BlueprintGuiQuery query) {
        ServerPlayNetworking.send(player, new BlueprintListPayload(
                createListView(BlockForgeFabric.BLUEPRINTS, BlockForgeFabric.SELECTIONS, player, query),
                openScreen
        ));
        syncPreviewSelection(player);
    }

    public static void syncPreviewSelection(ServerPlayerEntity player) {
        PlayerSelection selection = BlockForgeFabric.SELECTIONS.get(player.getUuid()).orElse(null);
        if (selection == null) {
            clearPreview(player, "No BlockForge Fabric blueprint selected.");
            return;
        }

        Blueprint blueprint = BlockForgeFabric.BLUEPRINTS.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            BlockForgeFabric.SELECTIONS.clear(player.getUuid());
            clearPreview(player, "Selected BlockForge Fabric blueprint no longer exists.");
            return;
        }

        syncPreviewSelection(player, blueprint, selection.rotationDegrees());
    }

    public static void syncPreviewSelection(ServerPlayerEntity player, Blueprint blueprint, int rotationDegrees) {
        ServerPlayNetworking.send(player, new PreviewSelectionPayload(
                blueprint.getId(),
                blueprint.getName(),
                blueprint.getSize().width(),
                blueprint.getSize().height(),
                blueprint.getSize().depth(),
                rotationDegrees
        ));
    }

    public static void clearPreview(ServerPlayerEntity player, String reason) {
        ServerPlayNetworking.send(player, new ClearPreviewPayload(reason == null ? "" : reason));
    }

    private static void handleSelectionRequest(ServerPlayerEntity player, SelectBlueprintRequestPayload payload) {
        SelectionRequest request;
        try {
            request = new SelectionRequest(payload.blueprintId(), payload.rotationDegrees());
        } catch (IllegalArgumentException error) {
            clearPreview(player, error.getMessage());
            sendSelectionResult(player, false, error.getMessage(), "", 0);
            return;
        }

        Blueprint blueprint = BlockForgeFabric.BLUEPRINTS.get(request.blueprintId()).orElse(null);
        if (blueprint == null) {
            clearPreview(player, "Unknown BlockForge Fabric blueprint id: " + request.blueprintId());
            sendSelectionResult(player, false, "Unknown BlockForge Fabric blueprint id: " + request.blueprintId(), "", 0);
            return;
        }

        BlockForgeFabric.SELECTIONS.select(player.getUuid(), blueprint.getId());
        BlockForgeFabric.SELECTIONS.rotate(player.getUuid(), request.rotationDegrees());
        player.sendMessage(Text.literal("Selected BlockForge Fabric blueprint from GUI: "
                + blueprint.getId()
                + " | rotation="
                + request.rotationDegrees()
                + "."), false);
        syncPreviewSelection(player, blueprint, request.rotationDegrees());
        sendSelectionResult(player, true, "Selected " + blueprint.getId(), blueprint.getId(), request.rotationDegrees());
    }

    private static BlueprintListView createListView(
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager,
            ServerPlayerEntity player,
            BlueprintGuiQuery query
    ) {
        List<BlueprintSummary> summaries = registry.getBlueprints()
                .stream()
                .map(FabricBlueprintGuiNetworking::summary)
                .toList();
        PagedBlueprintResult page = BlueprintGuiQueryService.query(summaries, query);
        PlayerSelection selection = selectionManager.get(player.getUuid()).orElse(null);
        String selectedId = "";
        int rotation = 0;

        if (selection != null) {
            if (registry.get(selection.selectedBlueprintId()).isPresent()) {
                selectedId = selection.selectedBlueprintId();
                rotation = selection.rotationDegrees();
            } else {
                selectionManager.clear(player.getUuid());
            }
        }

        return BlueprintListView.from(page, selectedId, rotation);
    }

    private static BlueprintSummary summary(Blueprint blueprint) {
        return new BlueprintSummary(
                blueprint.getId(),
                blueprint.getName(),
                blueprint.getSchemaVersion(),
                blueprint.getSize().width(),
                blueprint.getSize().height(),
                blueprint.getSize().depth(),
                blueprint.getBlockCount(),
                blueprint.getPalette().values().stream().anyMatch(entry -> !entry.properties().isEmpty()),
                sourceType(blueprint.getId()),
                sourceId(blueprint.getId()),
                0,
                List.of(sourceType(blueprint.getId()), blueprint.getSchemaVersion() == 2 ? "v2" : "v1")
        );
    }

    private static void sendSelectionResult(
            ServerPlayerEntity player,
            boolean success,
            String message,
            String blueprintId,
            int rotationDegrees
    ) {
        ServerPlayNetworking.send(player, new SelectionResultPayload(success, message, blueprintId, rotationDegrees));
    }

    private static <T extends CustomPayload> CustomPayload.Id<T> id(String path) {
        return new CustomPayload.Id<>(Identifier.of(BlockForgeFabric.MOD_ID, path));
    }

    public record BlueprintListRequestPayload(
            boolean openScreen,
            String searchText,
            BlueprintSourceFilter sourceFilter,
            BlueprintWarningFilter warningFilter,
            BlueprintSortMode sortMode,
            int page,
            int pageSize
    ) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, BlueprintListRequestPayload> CODEC = PacketCodec.of(
                BlueprintListRequestPayload::write,
                BlueprintListRequestPayload::read
        );

        public BlueprintListRequestPayload(boolean openScreen) {
            this(openScreen, "", BlueprintSourceFilter.ALL, BlueprintWarningFilter.ALL, BlueprintSortMode.NAME_ASC, 0, BlueprintGuiQuery.DEFAULT_PAGE_SIZE);
        }

        public BlueprintGuiQuery query() {
            return new BlueprintGuiQuery(searchText, sourceFilter, warningFilter, sortMode, page, pageSize);
        }

        private static BlueprintListRequestPayload read(RegistryByteBuf buffer) {
            return new BlueprintListRequestPayload(
                    buffer.readBoolean(),
                    buffer.readString(),
                    buffer.readEnumConstant(BlueprintSourceFilter.class),
                    buffer.readEnumConstant(BlueprintWarningFilter.class),
                    buffer.readEnumConstant(BlueprintSortMode.class),
                    buffer.readVarInt(),
                    buffer.readVarInt()
            );
        }

        private static void write(BlueprintListRequestPayload payload, RegistryByteBuf buffer) {
            buffer.writeBoolean(payload.openScreen());
            buffer.writeString(payload.searchText());
            buffer.writeEnumConstant(payload.sourceFilter());
            buffer.writeEnumConstant(payload.warningFilter());
            buffer.writeEnumConstant(payload.sortMode());
            buffer.writeVarInt(payload.page());
            buffer.writeVarInt(payload.pageSize());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return BLUEPRINT_LIST_REQUEST_ID;
        }
    }

    public record BlueprintListPayload(BlueprintListView view, boolean openScreen) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, BlueprintListPayload> CODEC = PacketCodec.of(
                BlueprintListPayload::write,
                BlueprintListPayload::read
        );

        private static BlueprintListPayload read(RegistryByteBuf buffer) {
            int count = buffer.readVarInt();
            List<BlueprintSummary> summaries = new ArrayList<>(count);
            for (int index = 0; index < count; index++) {
                summaries.add(readSummary(buffer));
            }
            int page = buffer.readVarInt();
            int pageSize = buffer.readVarInt();
            int totalItems = buffer.readVarInt();
            int totalPages = buffer.readVarInt();
            boolean hasPrevious = buffer.readBoolean();
            boolean hasNext = buffer.readBoolean();
            String selected = buffer.readString();
            int rotation = buffer.readVarInt();
            boolean openScreen = buffer.readBoolean();
            return new BlueprintListPayload(new BlueprintListView(summaries, page, pageSize, totalItems, totalPages, hasPrevious, hasNext, selected, rotation), openScreen);
        }

        private static void write(BlueprintListPayload payload, RegistryByteBuf buffer) {
            buffer.writeVarInt(payload.view().blueprints().size());
            for (BlueprintSummary summary : payload.view().blueprints()) {
                writeSummary(buffer, summary);
            }
            buffer.writeVarInt(payload.view().page());
            buffer.writeVarInt(payload.view().pageSize());
            buffer.writeVarInt(payload.view().totalItems());
            buffer.writeVarInt(payload.view().totalPages());
            buffer.writeBoolean(payload.view().hasPrevious());
            buffer.writeBoolean(payload.view().hasNext());
            buffer.writeString(payload.view().selectedBlueprintId());
            buffer.writeVarInt(payload.view().rotationDegrees());
            buffer.writeBoolean(payload.openScreen());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return BLUEPRINT_LIST_ID;
        }
    }

    public record SelectBlueprintRequestPayload(String blueprintId, int rotationDegrees) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, SelectBlueprintRequestPayload> CODEC = PacketCodec.of(
                SelectBlueprintRequestPayload::write,
                SelectBlueprintRequestPayload::read
        );

        private static SelectBlueprintRequestPayload read(RegistryByteBuf buffer) {
            return new SelectBlueprintRequestPayload(buffer.readString(), buffer.readVarInt());
        }

        private static void write(SelectBlueprintRequestPayload payload, RegistryByteBuf buffer) {
            buffer.writeString(payload.blueprintId());
            buffer.writeVarInt(payload.rotationDegrees());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return SELECT_BLUEPRINT_REQUEST_ID;
        }
    }

    public record SelectionResultPayload(
            boolean success,
            String message,
            String selectedBlueprintId,
            int rotationDegrees
    ) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, SelectionResultPayload> CODEC = PacketCodec.of(
                SelectionResultPayload::write,
                SelectionResultPayload::read
        );

        private static SelectionResultPayload read(RegistryByteBuf buffer) {
            return new SelectionResultPayload(
                    buffer.readBoolean(),
                    buffer.readString(),
                    buffer.readString(),
                    buffer.readVarInt()
            );
        }

        private static void write(SelectionResultPayload payload, RegistryByteBuf buffer) {
            buffer.writeBoolean(payload.success());
            buffer.writeString(payload.message());
            buffer.writeString(payload.selectedBlueprintId());
            buffer.writeVarInt(payload.rotationDegrees());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return SELECTION_RESULT_ID;
        }
    }

    public record PreviewSelectionPayload(
            String blueprintId,
            String blueprintName,
            int width,
            int height,
            int depth,
            int rotationDegrees
    ) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, PreviewSelectionPayload> CODEC = PacketCodec.of(
                PreviewSelectionPayload::write,
                PreviewSelectionPayload::read
        );

        private static PreviewSelectionPayload read(RegistryByteBuf buffer) {
            return new PreviewSelectionPayload(
                    buffer.readString(),
                    buffer.readString(),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readVarInt()
            );
        }

        private static void write(PreviewSelectionPayload payload, RegistryByteBuf buffer) {
            buffer.writeString(payload.blueprintId());
            buffer.writeString(payload.blueprintName());
            buffer.writeVarInt(payload.width());
            buffer.writeVarInt(payload.height());
            buffer.writeVarInt(payload.depth());
            buffer.writeVarInt(payload.rotationDegrees());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return PREVIEW_SELECTION_ID;
        }
    }

    public record ClearPreviewPayload(String reason) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, ClearPreviewPayload> CODEC = PacketCodec.of(
                ClearPreviewPayload::write,
                ClearPreviewPayload::read
        );

        private static ClearPreviewPayload read(RegistryByteBuf buffer) {
            return new ClearPreviewPayload(buffer.readString());
        }

        private static void write(ClearPreviewPayload payload, RegistryByteBuf buffer) {
            buffer.writeString(payload.reason());
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return CLEAR_PREVIEW_ID;
        }
    }

    private static BlueprintSummary readSummary(RegistryByteBuf buffer) {
        return new BlueprintSummary(
                buffer.readString(),
                buffer.readString(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                buffer.readString(),
                buffer.readString(),
                buffer.readVarInt(),
                readTags(buffer)
        );
    }

    private static void writeSummary(RegistryByteBuf buffer, BlueprintSummary summary) {
        buffer.writeString(summary.id());
        buffer.writeString(summary.name());
        buffer.writeVarInt(summary.schemaVersion());
        buffer.writeVarInt(summary.width());
        buffer.writeVarInt(summary.height());
        buffer.writeVarInt(summary.depth());
        buffer.writeVarInt(summary.blockCount());
        buffer.writeBoolean(summary.hasBlockStates());
        buffer.writeString(summary.sourceType());
        buffer.writeString(summary.sourceId());
        buffer.writeVarInt(summary.warningCount());
        buffer.writeVarInt(summary.tags().size());
        for (String tag : summary.tags()) {
            buffer.writeString(tag);
        }
    }

    private static List<String> readTags(RegistryByteBuf buffer) {
        int count = buffer.readVarInt();
        List<String> tags = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            tags.add(buffer.readString());
        }
        return tags;
    }

    private static String sourceType(String id) {
        if (id.startsWith("schem/") || id.endsWith(".schem")) {
            return "schematic";
        }
        return id.contains("/") ? "pack" : "loose";
    }

    private static String sourceId(String id) {
        int separator = id.indexOf('/');
        return separator > 0 ? id.substring(0, separator) : "";
    }
}

package com.blockforge.forge.network;

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
import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.player.ForgePlayerSelectionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class ForgeBlueprintGuiNetworking {
    private static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(BlockForgeForge.MOD_ID, "gui"))
            .networkProtocolVersion(1)
            .optional()
            .simpleChannel();

    private ForgeBlueprintGuiNetworking() {
    }

    public static void register() {
        CHANNEL.messageBuilder(BlueprintListRequestPayload.class)
                .encoder(BlueprintListRequestPayload::write)
                .decoder(BlueprintListRequestPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleListRequest)
                .add();
        CHANNEL.messageBuilder(BlueprintListPayload.class)
                .encoder(BlueprintListPayload::write)
                .decoder(BlueprintListPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleClientBlueprintList)
                .add();
        CHANNEL.messageBuilder(SelectBlueprintRequestPayload.class)
                .encoder(SelectBlueprintRequestPayload::write)
                .decoder(SelectBlueprintRequestPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleSelectionRequest)
                .add();
        CHANNEL.messageBuilder(SelectionResultPayload.class)
                .encoder(SelectionResultPayload::write)
                .decoder(SelectionResultPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleClientSelectionResult)
                .add();
        CHANNEL.messageBuilder(PreviewSelectionPayload.class)
                .encoder(PreviewSelectionPayload::write)
                .decoder(PreviewSelectionPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleClientPreviewSelection)
                .add();
        CHANNEL.messageBuilder(ClearPreviewPayload.class)
                .encoder(ClearPreviewPayload::write)
                .decoder(ClearPreviewPayload::read)
                .consumerMainThread(ForgeBlueprintGuiNetworking::handleClientClearPreview)
                .add();
        CHANNEL.build();
    }

    public static void requestBlueprintList(boolean openScreen) {
        CHANNEL.send(new BlueprintListRequestPayload(openScreen), PacketDistributor.SERVER.noArg());
    }

    public static void requestBlueprintList(
            boolean openScreen,
            String searchText,
            BlueprintSourceFilter sourceFilter,
            BlueprintWarningFilter warningFilter,
            BlueprintSortMode sortMode,
            int page,
            int pageSize
    ) {
        CHANNEL.send(new BlueprintListRequestPayload(openScreen, searchText, sourceFilter, warningFilter, sortMode, page, pageSize), PacketDistributor.SERVER.noArg());
    }

    public static void requestSelection(String blueprintId, int rotationDegrees) {
        CHANNEL.send(new SelectBlueprintRequestPayload(blueprintId, rotationDegrees), PacketDistributor.SERVER.noArg());
    }

    public static void sendBlueprintList(ServerPlayer player, boolean openScreen) {
        sendBlueprintList(player, openScreen, BlueprintGuiQuery.firstPage());
    }

    public static void sendBlueprintList(ServerPlayer player, boolean openScreen, BlueprintGuiQuery query) {
        CHANNEL.send(new BlueprintListPayload(
                createListView(BlockForgeForge.BLUEPRINTS, BlockForgeForge.SELECTIONS, player, query),
                openScreen
        ), PacketDistributor.PLAYER.with(player));
        syncPreviewSelection(player);
    }

    public static void syncPreviewSelection(ServerPlayer player) {
        PlayerSelection selection = BlockForgeForge.SELECTIONS.get(player.getUUID()).orElse(null);
        if (selection == null) {
            clearPreview(player, "No BlockForge Forge blueprint selected.");
            return;
        }

        Blueprint blueprint = BlockForgeForge.BLUEPRINTS.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            BlockForgeForge.SELECTIONS.clear(player.getUUID());
            clearPreview(player, "Selected BlockForge Forge blueprint no longer exists.");
            return;
        }

        syncPreviewSelection(player, blueprint, selection.rotationDegrees());
    }

    public static void syncPreviewSelection(ServerPlayer player, Blueprint blueprint, int rotationDegrees) {
        CHANNEL.send(new PreviewSelectionPayload(
                blueprint.getId(),
                blueprint.getName(),
                blueprint.getSize().width(),
                blueprint.getSize().height(),
                blueprint.getSize().depth(),
                rotationDegrees
        ), PacketDistributor.PLAYER.with(player));
    }

    public static void clearPreview(ServerPlayer player, String reason) {
        CHANNEL.send(new ClearPreviewPayload(reason == null ? "" : reason), PacketDistributor.PLAYER.with(player));
    }

    private static void handleListRequest(BlueprintListRequestPayload payload, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player != null) {
            sendBlueprintList(player, payload.openScreen(), payload.query());
        }
    }

    private static void handleSelectionRequest(SelectBlueprintRequestPayload payload, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }

        SelectionRequest request;
        try {
            request = new SelectionRequest(payload.blueprintId(), payload.rotationDegrees());
        } catch (IllegalArgumentException error) {
            clearPreview(player, error.getMessage());
            sendSelectionResult(player, false, error.getMessage(), "", 0);
            return;
        }

        Blueprint blueprint = BlockForgeForge.BLUEPRINTS.get(request.blueprintId()).orElse(null);
        if (blueprint == null) {
            clearPreview(player, "Unknown BlockForge Forge blueprint id: " + request.blueprintId());
            sendSelectionResult(player, false, "Unknown BlockForge Forge blueprint id: " + request.blueprintId(), "", 0);
            return;
        }

        BlockForgeForge.SELECTIONS.select(player.getUUID(), blueprint.getId());
        BlockForgeForge.SELECTIONS.rotate(player.getUUID(), request.rotationDegrees());
        player.sendSystemMessage(Component.literal("Selected BlockForge Forge blueprint from GUI: "
                + blueprint.getId()
                + " | rotation="
                + request.rotationDegrees()
                + "."));
        syncPreviewSelection(player, blueprint, request.rotationDegrees());
        sendSelectionResult(player, true, "Selected " + blueprint.getId(), blueprint.getId(), request.rotationDegrees());
    }

    private static BlueprintListView createListView(
            ForgeBlueprintRegistry registry,
            ForgePlayerSelectionManager selectionManager,
            ServerPlayer player,
            BlueprintGuiQuery query
    ) {
        List<BlueprintSummary> summaries = registry.getBlueprints()
                .stream()
                .map(ForgeBlueprintGuiNetworking::summary)
                .toList();
        PagedBlueprintResult page = BlueprintGuiQueryService.query(summaries, query);
        PlayerSelection selection = selectionManager.get(player.getUUID()).orElse(null);
        String selectedId = "";
        int rotation = 0;

        if (selection != null) {
            if (registry.get(selection.selectedBlueprintId()).isPresent()) {
                selectedId = selection.selectedBlueprintId();
                rotation = selection.rotationDegrees();
            } else {
                selectionManager.clear(player.getUUID());
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
            ServerPlayer player,
            boolean success,
            String message,
            String blueprintId,
            int rotationDegrees
    ) {
        CHANNEL.send(new SelectionResultPayload(success, message, blueprintId, rotationDegrees), PacketDistributor.PLAYER.with(player));
    }

    private static void handleClientBlueprintList(BlueprintListPayload payload, CustomPayloadEvent.Context context) {
        invokeClient("handleBlueprintList", new Class<?>[]{BlueprintListPayload.class}, payload);
    }

    private static void handleClientSelectionResult(SelectionResultPayload payload, CustomPayloadEvent.Context context) {
        invokeClient("handleSelectionResult", new Class<?>[]{SelectionResultPayload.class}, payload);
    }

    private static void handleClientPreviewSelection(PreviewSelectionPayload payload, CustomPayloadEvent.Context context) {
        invokeClient("handlePreviewSelection", new Class<?>[]{PreviewSelectionPayload.class}, payload);
    }

    private static void handleClientClearPreview(ClearPreviewPayload payload, CustomPayloadEvent.Context context) {
        invokeClient("handleClearPreview", new Class<?>[]{ClearPreviewPayload.class}, payload);
    }

    private static void invokeClient(String methodName, Class<?>[] parameterTypes, Object... arguments) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }

        try {
            Class<?> handler = Class.forName("com.blockforge.forge.client.ForgeClientPayloadHandler");
            handler.getMethod(methodName, parameterTypes).invoke(null, arguments);
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException error) {
            BlockForgeForge.LOGGER.warn("Failed to handle client BlockForge Forge payload", error);
        }
    }

    public record BlueprintListRequestPayload(
            boolean openScreen,
            String searchText,
            BlueprintSourceFilter sourceFilter,
            BlueprintWarningFilter warningFilter,
            BlueprintSortMode sortMode,
            int page,
            int pageSize
    ) {
        public BlueprintListRequestPayload(boolean openScreen) {
            this(openScreen, "", BlueprintSourceFilter.ALL, BlueprintWarningFilter.ALL, BlueprintSortMode.NAME_ASC, 0, BlueprintGuiQuery.DEFAULT_PAGE_SIZE);
        }

        public BlueprintGuiQuery query() {
            return new BlueprintGuiQuery(searchText, sourceFilter, warningFilter, sortMode, page, pageSize);
        }

        private static BlueprintListRequestPayload read(FriendlyByteBuf buffer) {
            return new BlueprintListRequestPayload(
                    buffer.readBoolean(),
                    buffer.readUtf(),
                    buffer.readEnum(BlueprintSourceFilter.class),
                    buffer.readEnum(BlueprintWarningFilter.class),
                    buffer.readEnum(BlueprintSortMode.class),
                    buffer.readVarInt(),
                    buffer.readVarInt()
            );
        }

        private static void write(BlueprintListRequestPayload payload, FriendlyByteBuf buffer) {
            buffer.writeBoolean(payload.openScreen());
            buffer.writeUtf(payload.searchText());
            buffer.writeEnum(payload.sourceFilter());
            buffer.writeEnum(payload.warningFilter());
            buffer.writeEnum(payload.sortMode());
            buffer.writeVarInt(payload.page());
            buffer.writeVarInt(payload.pageSize());
        }
    }

    public record BlueprintListPayload(BlueprintListView view, boolean openScreen) {
        private static BlueprintListPayload read(FriendlyByteBuf buffer) {
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
            String selected = buffer.readUtf();
            int rotation = buffer.readVarInt();
            boolean openScreen = buffer.readBoolean();
            return new BlueprintListPayload(new BlueprintListView(summaries, page, pageSize, totalItems, totalPages, hasPrevious, hasNext, selected, rotation), openScreen);
        }

        private static void write(BlueprintListPayload payload, FriendlyByteBuf buffer) {
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
            buffer.writeUtf(payload.view().selectedBlueprintId());
            buffer.writeVarInt(payload.view().rotationDegrees());
            buffer.writeBoolean(payload.openScreen());
        }
    }

    public record SelectBlueprintRequestPayload(String blueprintId, int rotationDegrees) {
        private static SelectBlueprintRequestPayload read(FriendlyByteBuf buffer) {
            return new SelectBlueprintRequestPayload(buffer.readUtf(), buffer.readVarInt());
        }

        private static void write(SelectBlueprintRequestPayload payload, FriendlyByteBuf buffer) {
            buffer.writeUtf(payload.blueprintId());
            buffer.writeVarInt(payload.rotationDegrees());
        }
    }

    public record SelectionResultPayload(
            boolean success,
            String message,
            String selectedBlueprintId,
            int rotationDegrees
    ) {
        private static SelectionResultPayload read(FriendlyByteBuf buffer) {
            return new SelectionResultPayload(
                    buffer.readBoolean(),
                    buffer.readUtf(),
                    buffer.readUtf(),
                    buffer.readVarInt()
            );
        }

        private static void write(SelectionResultPayload payload, FriendlyByteBuf buffer) {
            buffer.writeBoolean(payload.success());
            buffer.writeUtf(payload.message());
            buffer.writeUtf(payload.selectedBlueprintId());
            buffer.writeVarInt(payload.rotationDegrees());
        }
    }

    public record PreviewSelectionPayload(
            String blueprintId,
            String blueprintName,
            int width,
            int height,
            int depth,
            int rotationDegrees
    ) {
        private static PreviewSelectionPayload read(FriendlyByteBuf buffer) {
            return new PreviewSelectionPayload(
                    buffer.readUtf(),
                    buffer.readUtf(),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readVarInt(),
                    buffer.readVarInt()
            );
        }

        private static void write(PreviewSelectionPayload payload, FriendlyByteBuf buffer) {
            buffer.writeUtf(payload.blueprintId());
            buffer.writeUtf(payload.blueprintName());
            buffer.writeVarInt(payload.width());
            buffer.writeVarInt(payload.height());
            buffer.writeVarInt(payload.depth());
            buffer.writeVarInt(payload.rotationDegrees());
        }
    }

    public record ClearPreviewPayload(String reason) {
        private static ClearPreviewPayload read(FriendlyByteBuf buffer) {
            return new ClearPreviewPayload(buffer.readUtf());
        }

        private static void write(ClearPreviewPayload payload, FriendlyByteBuf buffer) {
            buffer.writeUtf(payload.reason());
        }
    }

    private static BlueprintSummary readSummary(FriendlyByteBuf buffer) {
        return new BlueprintSummary(
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                readTags(buffer)
        );
    }

    private static void writeSummary(FriendlyByteBuf buffer, BlueprintSummary summary) {
        buffer.writeUtf(summary.id());
        buffer.writeUtf(summary.name());
        buffer.writeVarInt(summary.schemaVersion());
        buffer.writeVarInt(summary.width());
        buffer.writeVarInt(summary.height());
        buffer.writeVarInt(summary.depth());
        buffer.writeVarInt(summary.blockCount());
        buffer.writeBoolean(summary.hasBlockStates());
        buffer.writeUtf(summary.sourceType());
        buffer.writeUtf(summary.sourceId());
        buffer.writeVarInt(summary.warningCount());
        buffer.writeVarInt(summary.tags().size());
        for (String tag : summary.tags()) {
            buffer.writeUtf(tag);
        }
    }

    private static List<String> readTags(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        List<String> tags = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            tags.add(buffer.readUtf());
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

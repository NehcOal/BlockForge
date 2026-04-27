package com.blockforge.connector.network;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.material.MaterialBuildGate;
import com.blockforge.connector.material.MaterialReport;
import com.blockforge.common.gui.BlueprintGuiQueryService;
import com.blockforge.common.gui.PagedBlueprintResult;
import com.blockforge.connector.network.payload.BlueprintListPayload;
import com.blockforge.connector.network.payload.BlueprintSummary;
import com.blockforge.connector.player.PlayerBlueprintSelection;
import com.blockforge.connector.network.payload.ClearPreviewPayload;
import com.blockforge.connector.network.payload.MaterialReportPayload;
import com.blockforge.connector.network.payload.MaterialReportRequestPayload;
import com.blockforge.connector.network.payload.MaterialRequirementSummary;
import com.blockforge.connector.network.payload.RequestBlueprintListPayload;
import com.blockforge.connector.network.payload.SelectBlueprintRequestPayload;
import com.blockforge.connector.network.payload.SelectedBlueprintPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class BlockForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final MaterialBuildGate MATERIALS = new MaterialBuildGate();

    private BlockForgeNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("blockforge_connector").versioned(PROTOCOL_VERSION).optional();
        registrar.playToClient(
                SelectedBlueprintPayload.TYPE,
                SelectedBlueprintPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientSelected(payload))
        );
        registrar.playToClient(
                ClearPreviewPayload.TYPE,
                ClearPreviewPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientClear(payload))
        );
        registrar.playToClient(
                BlueprintListPayload.TYPE,
                BlueprintListPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientBlueprintList(payload))
        );
        registrar.playToClient(
                MaterialReportPayload.TYPE,
                MaterialReportPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleClientMaterialReport(payload))
        );
        registrar.playToServer(
                RequestBlueprintListPayload.TYPE,
                RequestBlueprintListPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleListRequest(context.player(), payload))
        );
        registrar.playToServer(
                SelectBlueprintRequestPayload.TYPE,
                SelectBlueprintRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleSelectRequest(context.player(), payload))
        );
        registrar.playToServer(
                MaterialReportRequestPayload.TYPE,
                MaterialReportRequestPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleMaterialReportRequest(context.player(), payload))
        );
    }

    public static void sendSelectedBlueprint(
            ServerPlayer player,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        PacketDistributor.sendToPlayer(
                player,
                new SelectedBlueprintPayload(
                        blueprint.getId(),
                        blueprint.getName(),
                        blueprint.getSize().width(),
                        blueprint.getSize().height(),
                        blueprint.getSize().depth(),
                        rotation.degrees()
                )
        );
    }

    public static void clearPreview(ServerPlayer player, String reason) {
        PacketDistributor.sendToPlayer(player, new ClearPreviewPayload(reason));
    }

    public static void sendBlueprintList(ServerPlayer player, boolean openScreen) {
        sendBlueprintList(player, openScreen, RequestBlueprintListPayload.defaultQuery());
    }

    public static void sendBlueprintList(ServerPlayer player, boolean openScreen, com.blockforge.common.gui.BlueprintGuiQuery query) {
        PagedBlueprintResult page = BlueprintGuiQueryService.query(
                createCommonBlueprintSummaries(),
                query
        );
        PlayerBlueprintSelection selection = com.blockforge.connector.BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        String selectedId = "";
        int rotation = 0;
        if (selection.hasSelection()
                && com.blockforge.connector.BlockForgeConnector.BLUEPRINTS.get(selection.getSelectedBlueprintId()).isPresent()) {
            selectedId = selection.getSelectedBlueprintId();
            rotation = selection.getRotation().degrees();
        }
        PacketDistributor.sendToPlayer(player, new BlueprintListPayload(
                page.items().stream().map(BlockForgeNetwork::toPayloadSummary).toList(),
                page.page(),
                page.pageSize(),
                page.totalItems(),
                page.totalPages(),
                page.hasPrevious(),
                page.hasNext(),
                selectedId,
                rotation,
                openScreen
        ));
    }

    private static List<com.blockforge.common.gui.BlueprintSummary> createCommonBlueprintSummaries() {
        return com.blockforge.connector.BlockForgeConnector.BLUEPRINTS.getBlueprints()
                .stream()
                .map(blueprint -> new com.blockforge.common.gui.BlueprintSummary(
                        blueprint.getId(),
                        blueprint.getName(),
                        blueprint.getSchemaVersion(),
                        blueprint.getSize().width(),
                        blueprint.getSize().height(),
                        blueprint.getSize().depth(),
                        blueprint.getBlockCount(),
                        blueprint.getPalettePropertyCount() > 0,
                        sourceType(blueprint.getId()),
                        sourceId(blueprint.getId()),
                        0,
                        List.of(sourceType(blueprint.getId()), blueprint.getSchemaVersion() == 2 ? "v2" : "v1")
                ))
                .toList();
    }

    private static BlueprintSummary toPayloadSummary(com.blockforge.common.gui.BlueprintSummary summary) {
        return new BlueprintSummary(
                summary.id(),
                summary.name(),
                summary.schemaVersion(),
                summary.width(),
                summary.height(),
                summary.depth(),
                summary.blockCount(),
                summary.hasBlockStates(),
                summary.sourceType(),
                summary.sourceId(),
                summary.warningCount(),
                summary.tags()
        );
    }

    private static void handleListRequest(Player player, RequestBlueprintListPayload payload) {
        if (player instanceof ServerPlayer serverPlayer) {
            sendBlueprintList(serverPlayer, payload.openScreen(), payload.query());
        }
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

    private static void handleSelectRequest(Player player, SelectBlueprintRequestPayload payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Blueprint blueprint = com.blockforge.connector.BlockForgeConnector.BLUEPRINTS
                .get(payload.blueprintId())
                .orElse(null);
        if (blueprint == null) {
            serverPlayer.sendSystemMessage(Component.literal("Unknown BlockForge blueprint id: " + payload.blueprintId()));
            clearPreview(serverPlayer, "Unknown blueprint id.");
            return;
        }

        BlueprintRotation rotation;
        try {
            rotation = BlueprintRotation.fromDegrees(Integer.toString(payload.rotation()));
        } catch (IllegalArgumentException error) {
            serverPlayer.sendSystemMessage(Component.literal(error.getMessage()));
            return;
        }

        com.blockforge.connector.BlockForgeConnector.SELECTIONS.select(serverPlayer.getUUID(), blueprint.getId());
        com.blockforge.connector.BlockForgeConnector.SELECTIONS.rotate(serverPlayer.getUUID(), rotation);
        sendSelectedBlueprint(serverPlayer, blueprint, rotation);
        serverPlayer.sendSystemMessage(Component.literal("Selected BlockForge blueprint from GUI: "
                + blueprint.getId()
                + " | rotation="
                + rotation.degrees()
                + "."));
    }

    private static void handleMaterialReportRequest(Player player, MaterialReportRequestPayload payload) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Blueprint blueprint = com.blockforge.connector.BlockForgeConnector.BLUEPRINTS
                .get(payload.blueprintId())
                .orElse(null);
        if (blueprint == null) {
            serverPlayer.sendSystemMessage(Component.literal("Unknown BlockForge blueprint id: " + payload.blueprintId()));
            return;
        }

        sendMaterialReport(serverPlayer, blueprint);
    }

    public static void sendMaterialReport(ServerPlayer player, Blueprint blueprint) {
        MaterialReport report = MATERIALS.report(blueprint, player);
        PacketDistributor.sendToPlayer(
                player,
                new MaterialReportPayload(
                        blueprint.getId(),
                        report.enoughMaterials(),
                        report.totalRequiredItems(),
                        report.totalAvailableItems(),
                        report.requirements()
                                .stream()
                                .map(MaterialRequirementSummary::fromRequirement)
                                .toList()
                )
        );
    }

    private static void handleClientSelected(SelectedBlueprintPayload payload) {
        invokeClient("handleSelectedBlueprint", new Class<?>[]{SelectedBlueprintPayload.class}, payload);
    }

    private static void handleClientClear(ClearPreviewPayload payload) {
        invokeClient("handleClearPreview", new Class<?>[]{ClearPreviewPayload.class}, payload);
    }

    private static void handleClientBlueprintList(BlueprintListPayload payload) {
        invokeClient("handleBlueprintList", new Class<?>[]{BlueprintListPayload.class}, payload);
    }

    private static void handleClientMaterialReport(MaterialReportPayload payload) {
        invokeClient("handleMaterialReport", new Class<?>[]{MaterialReportPayload.class}, payload);
    }

    private static void invokeClient(String methodName, Class<?>[] parameterTypes, Object... arguments) {
        if (!FMLEnvironment.dist.isClient()) {
            return;
        }

        try {
            Class<?> handler = Class.forName("com.blockforge.connector.client.ClientPayloadHandler");
            handler.getMethod(methodName, parameterTypes).invoke(null, arguments);
        } catch (ClassNotFoundException
                 | NoSuchMethodException
                 | IllegalAccessException
                 | InvocationTargetException error) {
            com.blockforge.connector.BlockForgeConnector.LOGGER.warn("Failed to handle client BlockForge payload", error);
        }
    }
}

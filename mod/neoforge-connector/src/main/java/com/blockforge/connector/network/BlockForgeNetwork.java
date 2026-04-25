package com.blockforge.connector.network;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.network.payload.BlueprintListPayload;
import com.blockforge.connector.network.payload.BlueprintSummary;
import com.blockforge.connector.network.payload.ClearPreviewPayload;
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
import java.util.Comparator;
import java.util.List;

public final class BlockForgeNetwork {
    private static final String PROTOCOL_VERSION = "1";

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
        PacketDistributor.sendToPlayer(player, new BlueprintListPayload(createBlueprintSummaries(), openScreen));
    }

    private static List<BlueprintSummary> createBlueprintSummaries() {
        return com.blockforge.connector.BlockForgeConnector.BLUEPRINTS.getBlueprints()
                .stream()
                .sorted(Comparator.comparing(Blueprint::getId))
                .map(blueprint -> new BlueprintSummary(
                        blueprint.getId(),
                        blueprint.getName(),
                        blueprint.getSchemaVersion(),
                        blueprint.getSize().width(),
                        blueprint.getSize().height(),
                        blueprint.getSize().depth(),
                        blueprint.getBlockCount(),
                        blueprint.getPalettePropertyCount() > 0
                ))
                .toList();
    }

    private static void handleListRequest(Player player, RequestBlueprintListPayload payload) {
        if (player instanceof ServerPlayer serverPlayer) {
            sendBlueprintList(serverPlayer, payload.openScreen());
        }
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

    private static void handleClientSelected(SelectedBlueprintPayload payload) {
        invokeClient("handleSelectedBlueprint", new Class<?>[]{SelectedBlueprintPayload.class}, payload);
    }

    private static void handleClientClear(ClearPreviewPayload payload) {
        invokeClient("handleClearPreview", new Class<?>[]{ClearPreviewPayload.class}, payload);
    }

    private static void handleClientBlueprintList(BlueprintListPayload payload) {
        invokeClient("handleBlueprintList", new Class<?>[]{BlueprintListPayload.class}, payload);
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

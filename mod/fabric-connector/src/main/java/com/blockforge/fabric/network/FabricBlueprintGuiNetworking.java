package com.blockforge.fabric.network;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.gui.BlueprintListView;
import com.blockforge.common.gui.BlueprintSummary;
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
import java.util.Comparator;
import java.util.List;

public final class FabricBlueprintGuiNetworking {
    public static final CustomPayload.Id<BlueprintListRequestPayload> BLUEPRINT_LIST_REQUEST_ID = id("blueprint_list_request");
    public static final CustomPayload.Id<BlueprintListPayload> BLUEPRINT_LIST_ID = id("blueprint_list");
    public static final CustomPayload.Id<SelectBlueprintRequestPayload> SELECT_BLUEPRINT_REQUEST_ID = id("select_blueprint_request");
    public static final CustomPayload.Id<SelectionResultPayload> SELECTION_RESULT_ID = id("selection_result");

    private FabricBlueprintGuiNetworking() {
    }

    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(BLUEPRINT_LIST_REQUEST_ID, BlueprintListRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BLUEPRINT_LIST_ID, BlueprintListPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SELECT_BLUEPRINT_REQUEST_ID, SelectBlueprintRequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SELECTION_RESULT_ID, SelectionResultPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                BLUEPRINT_LIST_REQUEST_ID,
                (payload, context) -> sendBlueprintList(context.player(), payload.openScreen())
        );
        ServerPlayNetworking.registerGlobalReceiver(
                SELECT_BLUEPRINT_REQUEST_ID,
                (payload, context) -> handleSelectionRequest(context.player(), payload)
        );
    }

    public static void sendBlueprintList(ServerPlayerEntity player, boolean openScreen) {
        ServerPlayNetworking.send(player, new BlueprintListPayload(
                createListView(BlockForgeFabric.BLUEPRINTS, BlockForgeFabric.SELECTIONS, player),
                openScreen
        ));
    }

    private static void handleSelectionRequest(ServerPlayerEntity player, SelectBlueprintRequestPayload payload) {
        SelectionRequest request;
        try {
            request = new SelectionRequest(payload.blueprintId(), payload.rotationDegrees());
        } catch (IllegalArgumentException error) {
            sendSelectionResult(player, false, error.getMessage(), "", 0);
            return;
        }

        Blueprint blueprint = BlockForgeFabric.BLUEPRINTS.get(request.blueprintId()).orElse(null);
        if (blueprint == null) {
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
        sendSelectionResult(player, true, "Selected " + blueprint.getId(), blueprint.getId(), request.rotationDegrees());
    }

    private static BlueprintListView createListView(
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager,
            ServerPlayerEntity player
    ) {
        List<BlueprintSummary> summaries = registry.getBlueprints()
                .stream()
                .sorted(Comparator.comparing(Blueprint::getId))
                .map(FabricBlueprintGuiNetworking::summary)
                .toList();
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

        return new BlueprintListView(summaries, selectedId, rotation);
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
                blueprint.getPalette().values().stream().anyMatch(entry -> !entry.properties().isEmpty())
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

    public record BlueprintListRequestPayload(boolean openScreen) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, BlueprintListRequestPayload> CODEC = PacketCodec.of(
                BlueprintListRequestPayload::write,
                BlueprintListRequestPayload::read
        );

        private static BlueprintListRequestPayload read(RegistryByteBuf buffer) {
            return new BlueprintListRequestPayload(buffer.readBoolean());
        }

        private static void write(BlueprintListRequestPayload payload, RegistryByteBuf buffer) {
            buffer.writeBoolean(payload.openScreen());
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
            String selected = buffer.readString();
            int rotation = buffer.readVarInt();
            boolean openScreen = buffer.readBoolean();
            return new BlueprintListPayload(new BlueprintListView(summaries, selected, rotation), openScreen);
        }

        private static void write(BlueprintListPayload payload, RegistryByteBuf buffer) {
            buffer.writeVarInt(payload.view().blueprints().size());
            for (BlueprintSummary summary : payload.view().blueprints()) {
                writeSummary(buffer, summary);
            }
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

    private static BlueprintSummary readSummary(RegistryByteBuf buffer) {
        return new BlueprintSummary(
                buffer.readString(),
                buffer.readString(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readVarInt(),
                buffer.readBoolean()
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
    }
}

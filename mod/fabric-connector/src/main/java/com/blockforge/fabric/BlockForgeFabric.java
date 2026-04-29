package com.blockforge.fabric;

import com.blockforge.fabric.blueprint.FabricBlueprintLoader;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.buildplan.FabricBuildPlanManager;
import com.blockforge.fabric.command.FabricBlockForgeCommands;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import com.blockforge.fabric.player.FabricPlayerSelectionManager;
import com.blockforge.fabric.registry.FabricModItems;
import com.blockforge.fabric.security.FabricProtectionService;
import com.blockforge.fabric.undo.FabricUndoManager;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockForgeFabric implements ModInitializer {
    public static final String MOD_ID = "blockforge_connector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final FabricBlueprintRegistry BLUEPRINTS = new FabricBlueprintRegistry(
            FabricBlueprintLoader.defaultBlueprintDirectory()
    );
    public static final FabricUndoManager UNDO = new FabricUndoManager();
    public static final FabricPlayerSelectionManager SELECTIONS = new FabricPlayerSelectionManager();
    public static final FabricProtectionService PROTECTION = new FabricProtectionService();
    public static final BuilderWandStateStore WAND_STATES = new BuilderWandStateStore();
    public static final FabricBuildPlanManager BUILD_PLANS = new FabricBuildPlanManager();

    @Override
    public void onInitialize() {
        FabricModItems.register();
        registerGameplayBlockInteractions();
        FabricBlueprintGuiNetworking.registerServer();
        BLUEPRINTS.reload();
        PROTECTION.reload();
        FabricBlockForgeCommands.register(BLUEPRINTS, UNDO, SELECTIONS);
        LOGGER.info(
                "Loaded {} BlockForge Fabric blueprint(s) from {}",
                BLUEPRINTS.getBlueprints().size(),
                BLUEPRINTS.getDirectory()
        );
    }

    private void registerGameplayBlockInteractions() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!(player instanceof ServerPlayerEntity serverPlayer)) {
                return ActionResult.PASS;
            }

            var state = world.getBlockState(hitResult.getBlockPos());
            if (state.isOf(FabricModItems.BLUEPRINT_TABLE)) {
                FabricBlueprintGuiNetworking.sendBlueprintList(serverPlayer, true);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.BUILDER_ANCHOR)) {
                var pos = hitResult.getBlockPos();
                var wandState = WAND_STATES.update(serverPlayer.getUuid(), current -> current.withAnchor(
                        pos.getX() + "," + pos.getY() + "," + pos.getZ(),
                        world.getTime()
                ));
                serverPlayer.sendMessage(Text.literal("BlockForge Builder Wand bound to anchor " + wandState.anchorId() + "."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.MATERIAL_CACHE)) {
                serverPlayer.sendMessage(Text.literal("BlockForge Material Cache alpha block registered. Inventory-backed sourcing is planned for a later v3.1 alpha polish commit."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.BUILDER_STATION)) {
                serverPlayer.sendMessage(Text.literal("BlockForge Builder Station alpha scaffold. Use station commands for command-driven jobs."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.MATERIAL_LINK)) {
                serverPlayer.sendMessage(Text.literal("BlockForge Material Link alpha scaffold. Links expose Material Cache sources to station jobs."), false);
                return ActionResult.SUCCESS;
            }

            if (state.isOf(FabricModItems.CONSTRUCTION_CORE)) {
                serverPlayer.sendMessage(Text.literal("BlockForge Construction Core alpha scaffold. Multi-station project coordination remains planned."), false);
                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        });
    }
}

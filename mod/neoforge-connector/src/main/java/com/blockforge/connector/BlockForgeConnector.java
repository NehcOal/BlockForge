package com.blockforge.connector;

import com.blockforge.connector.blueprint.BlueprintLoader;
import com.blockforge.connector.blueprint.BlueprintRegistry;
import com.blockforge.connector.buildplan.NeoForgeBuildPlanManager;
import com.blockforge.connector.command.BlockForgeCommands;
import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.network.BlockForgeNetwork;
import com.blockforge.connector.player.PlayerSelectionManager;
import com.blockforge.connector.registry.ModBlocks;
import com.blockforge.connector.registry.ModItems;
import com.blockforge.connector.security.NeoForgePermissionService;
import com.blockforge.connector.security.NeoForgeProtectionService;
import com.blockforge.connector.undo.UndoManager;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.permission.PermissionCheckResult;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(BlockForgeConnector.MOD_ID)
public class BlockForgeConnector {
    public static final String MOD_ID = "blockforge_connector";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final BlueprintRegistry BLUEPRINTS = new BlueprintRegistry(
            BlueprintLoader.defaultBlueprintDirectory()
    );
    public static final PlayerSelectionManager SELECTIONS = new PlayerSelectionManager();
    public static final UndoManager UNDO = new UndoManager();
    public static final NeoForgePermissionService PERMISSIONS = new NeoForgePermissionService();
    public static final NeoForgeProtectionService PROTECTION = new NeoForgeProtectionService();
    public static final BuilderWandStateStore WAND_STATES = new BuilderWandStateStore();
    public static final NeoForgeBuildPlanManager BUILD_PLANS = new NeoForgeBuildPlanManager();

    public BlockForgeConnector(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BlockForgeConfig.SPEC);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        modEventBus.addListener(BlockForgeNetwork::register);
        modEventBus.addListener(this::addCreativeTabItems);
        NeoForge.EVENT_BUS.register(this);
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.BUILDER_WAND.get());
            event.accept(ModItems.BLUEPRINT_TABLE.get());
            event.accept(ModItems.MATERIAL_CACHE.get());
            event.accept(ModItems.BUILDER_ANCHOR.get());
            event.accept(ModItems.BUILDER_STATION.get());
            event.accept(ModItems.MATERIAL_LINK.get());
            event.accept(ModItems.CONSTRUCTION_CORE.get());
        }
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ModBlocks.BLUEPRINT_TABLE.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_BLUEPRINT_TABLE_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            BlockForgeNetwork.sendBlueprintList(player, true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ModBlocks.BUILDER_ANCHOR.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_ANCHOR_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            var wandState = WAND_STATES.update(player.getUUID(), current -> current.withAnchor(anchorId(event.getPos()), event.getLevel().getGameTime()));
            player.sendSystemMessage(Component.literal("BlockForge Builder Wand bound to anchor " + wandState.anchorId() + "."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ModBlocks.MATERIAL_CACHE.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_CACHE_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Material Cache alpha block registered. Inventory-backed sourcing is planned for a later v3.1 alpha polish commit."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ModBlocks.BUILDER_STATION.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_STATION_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Builder Station alpha scaffold. Use /blockforge station status or /blockforge station step for command-driven jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ModBlocks.MATERIAL_LINK.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_MATERIAL_LINK_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Material Link alpha scaffold. Links expose Material Cache sources to Builder Station jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ModBlocks.CONSTRUCTION_CORE.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_CONSTRUCTION_CORE_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Construction Core alpha scaffold. Multi-station project coordination remains planned."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    private static String anchorId(net.minecraft.core.BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static boolean checkGameplayPermission(ServerPlayer player, BlockForgePermissionAction action) {
        PermissionCheckResult result = PERMISSIONS.check(player, action);
        if (!result.allowed()) {
            player.sendSystemMessage(Component.literal(result.reason()));
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        BlockForgeCommands.register(event.getDispatcher(), BLUEPRINTS);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        BLUEPRINTS.reload();
        PROTECTION.reload();
        LOGGER.info(
                "Loaded {} BlockForge blueprints from {}",
                BLUEPRINTS.getBlueprints().size(),
                BLUEPRINTS.getDirectory()
        );
    }
}

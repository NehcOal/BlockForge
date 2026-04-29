package com.blockforge.forge;

import com.blockforge.forge.blueprint.ForgeBlueprintLoader;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.buildplan.ForgeBuildPlanManager;
import com.blockforge.forge.command.ForgeBlockForgeCommands;
import com.blockforge.forge.network.ForgeBlueprintGuiNetworking;
import com.blockforge.forge.player.ForgePlayerSelectionManager;
import com.blockforge.forge.registry.ForgeModBlocks;
import com.blockforge.forge.registry.ForgeModItems;
import com.blockforge.forge.security.ForgePermissionService;
import com.blockforge.forge.security.ForgeProtectionService;
import com.blockforge.forge.undo.ForgeUndoManager;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.permission.PermissionCheckResult;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BlockForgeForge.MOD_ID)
public class BlockForgeForge {
    public static final String MOD_ID = "blockforge_connector";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ForgeBlueprintRegistry BLUEPRINTS = new ForgeBlueprintRegistry(
            ForgeBlueprintLoader.defaultBlueprintDirectory()
    );
    public static final ForgeUndoManager UNDO = new ForgeUndoManager();
    public static final ForgePlayerSelectionManager SELECTIONS = new ForgePlayerSelectionManager();
    public static final ForgePermissionService PERMISSIONS = new ForgePermissionService();
    public static final ForgeProtectionService PROTECTION = new ForgeProtectionService();
    public static final BuilderWandStateStore WAND_STATES = new BuilderWandStateStore();
    public static final ForgeBuildPlanManager BUILD_PLANS = new ForgeBuildPlanManager();

    public BlockForgeForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeModBlocks.register(modEventBus);
        ForgeModItems.register(modEventBus);
        modEventBus.addListener(this::addCreativeTabItems);
        ForgeBlueprintGuiNetworking.register();
        BLUEPRINTS.reload();
        PROTECTION.reload();
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info(
                "Loaded {} BlockForge Forge blueprint(s) from {}",
                BLUEPRINTS.getBlueprints().size(),
                BLUEPRINTS.getDirectory()
        );
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ForgeModItems.BUILDER_WAND.get());
            event.accept(ForgeModItems.BLUEPRINT_TABLE.get());
            event.accept(ForgeModItems.MATERIAL_CACHE.get());
            event.accept(ForgeModItems.BUILDER_ANCHOR.get());
            event.accept(ForgeModItems.BUILDER_STATION.get());
            event.accept(ForgeModItems.MATERIAL_LINK.get());
            event.accept(ForgeModItems.CONSTRUCTION_CORE.get());
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ForgeBlockForgeCommands.register(event.getDispatcher(), BLUEPRINTS, UNDO, SELECTIONS);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        var state = event.getLevel().getBlockState(event.getPos());
        if (state.is(ForgeModBlocks.BLUEPRINT_TABLE.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_BLUEPRINT_TABLE_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            ForgeBlueprintGuiNetworking.sendBlueprintList(player, true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.BUILDER_ANCHOR.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_ANCHOR_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            var pos = event.getPos();
            var wandState = WAND_STATES.update(player.getUUID(), current -> current.withAnchor(
                    pos.getX() + "," + pos.getY() + "," + pos.getZ(),
                    event.getLevel().getGameTime()
            ));
            player.sendSystemMessage(Component.literal("BlockForge Builder Wand bound to anchor " + wandState.anchorId() + "."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.MATERIAL_CACHE.get())) {
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

        if (state.is(ForgeModBlocks.BUILDER_STATION.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_STATION_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Builder Station alpha scaffold. Use station commands for command-driven jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.MATERIAL_LINK.get())) {
            if (!checkGameplayPermission(player, BlockForgePermissionAction.GAMEPLAY_MATERIAL_LINK_USE)) {
                event.setCancellationResult(InteractionResult.FAIL);
                event.setCanceled(true);
                return;
            }
            player.sendSystemMessage(Component.literal("BlockForge Material Link alpha scaffold. Links expose Material Cache sources to station jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.CONSTRUCTION_CORE.get())) {
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

    private static boolean checkGameplayPermission(ServerPlayer player, BlockForgePermissionAction action) {
        PermissionCheckResult result = PERMISSIONS.check(player, action);
        if (!result.allowed()) {
            player.sendSystemMessage(Component.literal(result.reason()));
            return false;
        }
        return true;
    }
}

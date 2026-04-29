package com.blockforge.forge;

import com.blockforge.forge.blueprint.ForgeBlueprintLoader;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.buildplan.ForgeBuildPlanManager;
import com.blockforge.forge.command.ForgeBlockForgeCommands;
import com.blockforge.forge.network.ForgeBlueprintGuiNetworking;
import com.blockforge.forge.player.ForgePlayerSelectionManager;
import com.blockforge.forge.registry.ForgeModBlocks;
import com.blockforge.forge.registry.ForgeModItems;
import com.blockforge.forge.security.ForgeProtectionService;
import com.blockforge.forge.undo.ForgeUndoManager;
import com.blockforge.common.gameplay.BuilderWandStateStore;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
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
    public static final ForgeProtectionService PROTECTION = new ForgeProtectionService();
    public static final BuilderWandStateStore WAND_STATES = new BuilderWandStateStore();
    public static final ForgeBuildPlanManager BUILD_PLANS = new ForgeBuildPlanManager();

    public BlockForgeForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeModBlocks.register(modEventBus);
        ForgeModItems.register(modEventBus);
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
            ForgeBlueprintGuiNetworking.sendBlueprintList(player, true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.BUILDER_ANCHOR.get())) {
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
            player.sendSystemMessage(Component.literal("BlockForge Material Cache alpha block registered. Inventory-backed sourcing is planned for a later v3.1 alpha polish commit."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.BUILDER_STATION.get())) {
            player.sendSystemMessage(Component.literal("BlockForge Builder Station alpha scaffold. Use station commands for command-driven jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.MATERIAL_LINK.get())) {
            player.sendSystemMessage(Component.literal("BlockForge Material Link alpha scaffold. Links expose Material Cache sources to station jobs."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if (state.is(ForgeModBlocks.CONSTRUCTION_CORE.get())) {
            player.sendSystemMessage(Component.literal("BlockForge Construction Core alpha scaffold. Multi-station project coordination remains planned."));
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}

package com.blockforge.forge;

import com.blockforge.forge.blueprint.ForgeBlueprintLoader;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.command.ForgeBlockForgeCommands;
import com.blockforge.forge.player.ForgePlayerSelectionManager;
import com.blockforge.forge.registry.ForgeModItems;
import com.blockforge.forge.undo.ForgeUndoManager;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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

    public BlockForgeForge(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ForgeModItems.register(modEventBus);
        BLUEPRINTS.reload();
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
}

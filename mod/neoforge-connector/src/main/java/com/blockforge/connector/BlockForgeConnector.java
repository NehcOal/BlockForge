package com.blockforge.connector;

import com.blockforge.connector.blueprint.BlueprintLoader;
import com.blockforge.connector.blueprint.BlueprintRegistry;
import com.blockforge.connector.command.BlockForgeCommands;
import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.network.BlockForgeNetwork;
import com.blockforge.connector.player.PlayerSelectionManager;
import com.blockforge.connector.registry.ModItems;
import com.blockforge.connector.security.NeoForgeProtectionService;
import com.blockforge.connector.undo.UndoManager;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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
    public static final NeoForgeProtectionService PROTECTION = new NeoForgeProtectionService();

    public BlockForgeConnector(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, BlockForgeConfig.SPEC);
        ModItems.register(modEventBus);
        modEventBus.addListener(BlockForgeNetwork::register);
        modEventBus.addListener(this::addCreativeTabItems);
        NeoForge.EVENT_BUS.register(this);
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.BUILDER_WAND.get());
        }
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

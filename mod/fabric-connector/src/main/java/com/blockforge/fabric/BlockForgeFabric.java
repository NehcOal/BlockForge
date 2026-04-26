package com.blockforge.fabric;

import com.blockforge.fabric.blueprint.FabricBlueprintLoader;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.command.FabricBlockForgeCommands;
import com.blockforge.fabric.player.FabricPlayerSelectionManager;
import com.blockforge.fabric.registry.FabricModItems;
import com.blockforge.fabric.undo.FabricUndoManager;
import net.fabricmc.api.ModInitializer;
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

    @Override
    public void onInitialize() {
        FabricModItems.register();
        BLUEPRINTS.reload();
        FabricBlockForgeCommands.register(BLUEPRINTS, UNDO, SELECTIONS);
        LOGGER.info(
                "Loaded {} BlockForge Fabric blueprint(s) from {}",
                BLUEPRINTS.getBlueprints().size(),
                BLUEPRINTS.getDirectory()
        );
    }
}

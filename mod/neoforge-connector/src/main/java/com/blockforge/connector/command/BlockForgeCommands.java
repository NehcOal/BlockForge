package com.blockforge.connector.command;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.ExampleBlueprintInstaller;
import com.blockforge.connector.blueprint.BlueprintRegistry;
import com.blockforge.connector.builder.BlueprintPlacer;
import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.network.BlockForgeNetwork;
import com.blockforge.connector.player.PlayerBlueprintSelection;
import com.blockforge.connector.registry.ModItems;
import com.blockforge.connector.undo.PlacementSnapshot;
import com.blockforge.connector.undo.UndoManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class BlockForgeCommands {
    private static final BlueprintPlacer PLACER = new BlueprintPlacer();
    private static final ExampleBlueprintInstaller EXAMPLES = new ExampleBlueprintInstaller();

    private BlockForgeCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            BlueprintRegistry registry
    ) {
        dispatcher.register(Commands.literal("blockforge")
                .then(Commands.literal("folder")
                        .executes(context -> showFolder(context, registry)))
                .then(Commands.literal("examples")
                        .then(Commands.literal("list")
                                .executes(context -> listExamples(context)))
                        .then(Commands.literal("install")
                                .executes(context -> installExamples(context, registry))))
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reload(context, registry)))
                .then(Commands.literal("list")
                        .executes(context -> list(context, registry)))
                .then(Commands.literal("select")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> select(context, registry))))
                .then(Commands.literal("selected")
                        .executes(context -> selected(context, registry)))
                .then(Commands.literal("rotate")
                        .then(rotationArgument()
                                .executes(context -> rotateSelection(context, registry))))
                .then(Commands.literal("wand")
                        .requires(source -> source.hasPermission(2))
                        .executes(BlockForgeCommands::giveWand))
                .then(Commands.literal("gui")
                        .executes(BlockForgeCommands::openGui))
                .then(Commands.literal("undo")
                        .executes(BlockForgeCommands::undo)
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::listUndo))
                        .then(Commands.literal("clear")
                                .executes(BlockForgeCommands::clearUndo)))
                .then(Commands.literal("info")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> info(context, registry))))
                .then(Commands.literal("dryrun")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> dryRun(context, registry))))
                .then(Commands.literal("build")
                        .requires(source -> source.hasPermission(2))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> buildAtPlayer(context, registry))
                                .then(Commands.literal("rotate")
                                        .then(rotationArgument()
                                                .executes(context -> buildAtPlayer(context, registry, getRotation(context)))))
                                .then(Commands.literal("at")
                                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                                .then(Commands.argument("y", IntegerArgumentType.integer())
                                                        .then(Commands.argument("z", IntegerArgumentType.integer())
                                                                .executes(context -> buildAtCoordinates(context, registry, BlueprintRotation.NONE))
                                                                .then(Commands.literal("rotate")
                                                                        .then(rotationArgument()
                                                                                .executes(context -> buildAtCoordinates(context, registry, getRotation(context)))))))))
                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                        .executes(context -> buildAtCoordinates(context, registry))))))));
    }

    private static int listExamples(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Built-in BlockForge example blueprints:"),
                false
        );

        for (ExampleBlueprintInstaller.ExampleBlueprint example : EXAMPLES.getExamples()) {
            context.getSource().sendSuccess(
                    () -> Component.literal("- " + example.id() + " | " + example.name() + " | " + example.fileName()),
                    false
            );
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Install them with /blockforge examples install, then run /blockforge reload."),
                false
        );
        return EXAMPLES.getExamples().size();
    }

    private static int installExamples(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        ExampleBlueprintInstaller.InstallResult result = EXAMPLES.install(registry.getDirectory());

        if (result.hasError()) {
            context.getSource().sendFailure(Component.literal(result.error()));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Installed BlockForge examples: installed="
                        + result.installed()
                        + ", skipped="
                        + result.skipped()
                        + ", missing="
                        + result.missing()
                        + ". Run /blockforge reload next."),
                true
        );
        return result.installed();
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> blueprintIdArgument(
            BlueprintRegistry registry
    ) {
        return Commands.argument("id", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(registry.getIds(), builder));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> rotationArgument() {
        return Commands.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("0", "90", "180", "270"), builder));
    }

    private static int showFolder(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge blueprint folder: " + registry.getDirectory()),
                false
        );
        return 1;
    }

    private static int reload(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        BlueprintRegistry.LoadSummary summary = registry.reload();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded " + summary.loadedCount() + " BlockForge blueprint(s)."),
                true
        );

        for (String warning : summary.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }

        ServerPlayer player = getPlayerOrNull(context);
        if (player != null) {
            PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
            if (selection.hasSelection() && registry.get(selection.getSelectedBlueprintId()).isEmpty()) {
                BlockForgeNetwork.clearPreview(player, "Selected blueprint is not loaded after reload.");
            }
        }

        return summary.loadedCount();
    }

    private static int list(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        if (registry.getBlueprints().isEmpty()) {
            context.getSource().sendFailure(Component.literal(
                    "No blueprints loaded. Put JSON files in " + registry.getDirectory() + " and run /blockforge reload."
            ));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Loaded BlockForge blueprints:"),
                false
        );

        for (Blueprint blueprint : registry.getBlueprints()) {
            context.getSource().sendSuccess(
                    () -> Component.literal("- " + describe(blueprint)),
                    false
            );
        }

        return registry.getBlueprints().size();
    }

    private static int info(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal(describe(blueprint) + " | " + blueprint.getDescription()),
                false
        );
        return 1;
    }

    private static int select(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        BlockForgeConnector.SELECTIONS.select(player.getUUID(), blueprint.getId());
        BlockForgeNetwork.sendSelectedBlueprint(player, blueprint, BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID()).getRotation());
        context.getSource().sendSuccess(
                () -> Component.literal("Selected BlockForge blueprint: " + blueprint.getId()
                        + ". Use /blockforge rotate <0|90|180|270> to set wand rotation."),
                false
        );
        return 1;
    }

    private static int selected(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        if (!selection.hasSelection()) {
            context.getSource().sendFailure(Component.literal("No BlockForge blueprint selected. Run /blockforge select <id> first."));
            return 0;
        }

        Blueprint blueprint = registry.get(selection.getSelectedBlueprintId()).orElse(null);
        if (blueprint != null) {
            BlockForgeNetwork.sendSelectedBlueprint(player, blueprint, selection.getRotation());
        } else {
            BlockForgeNetwork.clearPreview(player, "Selected blueprint is not loaded.");
        }

        String blueprintLabel = blueprint == null
                ? selection.getSelectedBlueprintId() + " (not loaded)"
                : describe(blueprint);

        context.getSource().sendSuccess(
                () -> Component.literal("Selected BlockForge blueprint: "
                        + blueprintLabel
                        + " | rotation="
                        + selection.getRotation().degrees()),
                false
        );
        if (blueprint != null) {
            context.getSource().sendSuccess(
                    () -> Component.literal("If you hold the Builder Wand, Ghost Preview follows the block you are looking at."),
                    false
            );
        }
        return 1;
    }

    private static int rotateSelection(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BlueprintRotation rotation = getRotation(context);
        if (rotation == null) {
            return 0;
        }

        BlockForgeConnector.SELECTIONS.rotate(player.getUUID(), rotation);
        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        if (selection.hasSelection()) {
            Blueprint blueprint = registry.get(selection.getSelectedBlueprintId()).orElse(null);
            if (blueprint != null) {
                BlockForgeNetwork.sendSelectedBlueprint(player, blueprint, rotation);
            } else {
                BlockForgeNetwork.clearPreview(player, "Selected blueprint is not loaded.");
            }
        }
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand rotation set to " + rotation.degrees() + "."),
                false
        );
        return 1;
    }

    private static int undo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        if (!context.getSource().hasPermission(2)) {
            context.getSource().sendFailure(Component.literal("BlockForge undo requires permission level 2."));
            return 0;
        }

        PlacementSnapshot snapshot = BlockForgeConnector.UNDO.popLatest(player.getUUID()).orElse(null);
        if (snapshot == null) {
            context.getSource().sendFailure(Component.literal("No BlockForge undo snapshot available for this player."));
            return 0;
        }

        UndoManager.UndoResult result = BlockForgeConnector.UNDO.restore(context.getSource().getLevel(), snapshot);
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge undo restored "
                        + result.restoredBlocks()
                        + " blocks from blueprint "
                        + result.blueprintId()
                        + "."),
                true
        );
        return result.restoredBlocks();
    }

    private static int listUndo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        List<PlacementSnapshot> snapshots = BlockForgeConnector.UNDO.list(player.getUUID());
        if (snapshots.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No BlockForge undo snapshots available for this player."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge undo snapshots:"),
                false
        );

        long now = context.getSource().getLevel().getGameTime();
        for (PlacementSnapshot snapshot : snapshots) {
            long ageSeconds = Math.max(0L, (now - snapshot.createdAtGameTime()) / 20L);
            context.getSource().sendSuccess(
                    () -> Component.literal("- "
                            + snapshot.blueprintId()
                            + " | placed="
                            + snapshot.placedBlocks()
                            + " | age="
                            + ageSeconds
                            + "s"),
                    false
            );
        }

        return snapshots.size();
    }

    private static int clearUndo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        if (!context.getSource().hasPermission(2)) {
            context.getSource().sendFailure(Component.literal("BlockForge undo clear requires permission level 2."));
            return 0;
        }

        int cleared = BlockForgeConnector.UNDO.clear(player.getUUID());
        context.getSource().sendSuccess(
                () -> Component.literal("Cleared " + cleared + " BlockForge undo snapshot(s)."),
                true
        );
        return cleared;
    }

    private static int giveWand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ItemStack stack = new ItemStack(ModItems.BUILDER_WAND.get());
        boolean added = player.getInventory().add(stack);
        if (!added) {
            player.drop(stack, false);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Given BlockForge Builder Wand. Select a blueprint with /blockforge select <id>, then right-click a block."),
                true
        );
        return 1;
    }

    private static int openGui(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BlockForgeNetwork.sendBlueprintList(player, true);
        context.getSource().sendSuccess(
                () -> Component.literal("Opening BlockForge Blueprint Selector."),
                false
        );
        return 1;
    }

    private static int dryRun(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        BlueprintPlacer.PlacementResult result = PLACER.dryRun(blueprint);
        sendDryRunResult(context.getSource(), blueprint, result);
        return result.placedBlocks();
    }

    private static int buildAtPlayer(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        return buildAtPlayer(context, registry, BlueprintRotation.NONE);
    }

    private static int buildAtPlayer(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry,
            BlueprintRotation rotation
    ) {
        if (rotation == null) {
            return 0;
        }

        try {
            ServerPlayer player = context.getSource().getPlayerOrException();
            return build(context, registry, player.blockPosition(), rotation);
        } catch (Exception error) {
            context.getSource().sendFailure(Component.literal("This build form requires a player source."));
            return 0;
        }
    }

    private static int buildAtCoordinates(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        return buildAtCoordinates(context, registry, BlueprintRotation.NONE);
    }

    private static int buildAtCoordinates(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry,
            BlueprintRotation rotation
    ) {
        if (rotation == null) {
            return 0;
        }

        BlockPos basePos = new BlockPos(
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "z")
        );
        return build(context, registry, basePos, rotation);
    }

    private static int build(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry,
            BlockPos basePos,
            BlueprintRotation rotation
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        BlueprintPlacer.PlacementResult result = PLACER.place(
                context.getSource().getLevel(),
                basePos,
                blueprint,
                rotation,
                getPlayerOrNull(context)
        );

        if (result.snapshot() != null) {
            BlockForgeConnector.UNDO.record(result.snapshot());
        }

        sendPlacementResult(context.getSource(), "Build complete", result);
        return result.placedBlocks();
    }

    private static ServerPlayer getPlayerOrNull(CommandContext<CommandSourceStack> context) {
        try {
            return context.getSource().getPlayerOrException();
        } catch (Exception error) {
            return null;
        }
    }

    private static BlueprintRotation getRotation(CommandContext<CommandSourceStack> context) {
        try {
            return BlueprintRotation.fromDegrees(StringArgumentType.getString(context, "rotation"));
        } catch (IllegalArgumentException error) {
            context.getSource().sendFailure(Component.literal(error.getMessage()));
            return null;
        }
    }

    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        try {
            return context.getSource().getPlayerOrException();
        } catch (Exception error) {
            context.getSource().sendFailure(Component.literal("This command requires a player source."));
            return null;
        }
    }

    private static Blueprint findBlueprint(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        String id = StringArgumentType.getString(context, "id");
        return registry.get(id).orElseGet(() -> {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge blueprint id: " + id));
            return null;
        });
    }

    private static void sendPlacementResult(
            CommandSourceStack source,
            String label,
            BlueprintPlacer.PlacementResult result
    ) {
        if (result.tooLarge()) {
            source.sendFailure(Component.literal(
                    "Blueprint has " + result.totalBlocks() + " blocks, which exceeds the "
                            + result.maxBlocks() + " block safety limit."
            ));
            return;
        }

        if (result.empty()) {
            source.sendFailure(Component.literal("Blueprint has no blocks and cannot be built."));
            return;
        }

        source.sendSuccess(
                () -> Component.literal("BlockForge " + label
                        + ": placed " + result.placedBlocks()
                        + " blocks. skipped: missingPalette=" + result.skippedMissingPalette()
                        + ", invalidBlockId=" + result.skippedInvalidBlockIds()
                        + ", invalidProperties=" + result.skippedInvalidProperties()
                        + ", outOfWorld=" + result.skippedOutOfWorld()
                        + ", protected=" + result.skippedProtected()
                        + ", nonReplaceable=" + result.skippedNonReplaceable()
                        + ". appliedProperties=" + result.appliedProperties()
                        + ". totalBlocks=" + result.totalBlocks()
                        + ". Use /blockforge undo to revert."),
                true
        );
    }

    private static void sendDryRunResult(
            CommandSourceStack source,
            Blueprint blueprint,
            BlueprintPlacer.PlacementResult result
    ) {
        source.sendSuccess(
                () -> Component.literal("BlockForge dryrun: "
                        + blueprint.getId()
                        + " | "
                        + blueprint.getName()
                        + " | size="
                        + blueprint.getSize().format()
                        + " | totalBlocks="
                        + result.totalBlocks()
                        + " | paletteEntries="
                        + blueprint.getPalette().size()
                        + " | properties="
                        + blueprint.getPalettePropertyCount()
                        + " | blocksWithProperties="
                        + blueprint.getBlocksWithPropertiesCount()
                        + " | estimatedPlacedBlocks="
                        + result.placedBlocks()
                        + " | missingPalette="
                        + result.skippedMissingPalette()
                        + " | invalidBlockId="
                        + result.skippedInvalidBlockIds()
                        + " | invalidProperties="
                        + result.skippedInvalidProperties()
                        + " | protected="
                        + result.skippedProtected()
                        + " | nonReplaceable="
                        + result.skippedNonReplaceable()
                        + " | exceedsMaxBlockLimit="
                        + result.tooLarge()),
                false
        );

        if (result.empty()) {
            source.sendFailure(Component.literal("Blueprint has no blocks and cannot be built."));
        }
    }

    private static String describe(Blueprint blueprint) {
        return blueprint.getId()
                + " | "
                + blueprint.getName()
                + " | "
                + blueprint.getSize().format()
                + " | blocks="
                + blueprint.getBlockCount();
    }
}

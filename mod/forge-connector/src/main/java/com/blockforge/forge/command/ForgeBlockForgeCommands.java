package com.blockforge.forge.command;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.blueprint.ForgeExampleBlueprintInstaller;
import com.blockforge.forge.builder.ForgeBlueprintPlacer;
import com.blockforge.forge.network.ForgeBlueprintGuiNetworking;
import com.blockforge.forge.player.ForgePlayerSelectionManager;
import com.blockforge.forge.registry.ForgeModItems;
import com.blockforge.forge.undo.ForgeUndoManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ForgeBlockForgeCommands {
    private static final ForgeBlueprintPlacer PLACER = new ForgeBlueprintPlacer();
    private static final ForgeExampleBlueprintInstaller EXAMPLES = new ForgeExampleBlueprintInstaller();

    private ForgeBlockForgeCommands() {
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            ForgeBlueprintRegistry registry,
            ForgeUndoManager undoManager,
            ForgePlayerSelectionManager selectionManager
    ) {
        dispatcher.register(Commands.literal("blockforge")
                .then(Commands.literal("folder")
                        .executes(context -> showFolder(context, registry)))
                .then(Commands.literal("examples")
                        .then(Commands.literal("list")
                                .executes(ForgeBlockForgeCommands::listExamples))
                        .then(Commands.literal("install")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> installExamples(context, registry))))
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> reload(context, registry)))
                .then(Commands.literal("list")
                        .executes(context -> list(context, registry)))
                .then(Commands.literal("select")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> select(context, registry, selectionManager))))
                .then(Commands.literal("selected")
                        .executes(context -> selected(context, registry, selectionManager)))
                .then(Commands.literal("rotate")
                        .then(rotationArgument()
                                .executes(context -> rotate(context, selectionManager))))
                .then(Commands.literal("wand")
                        .requires(source -> source.hasPermission(2))
                        .executes(ForgeBlockForgeCommands::giveWand))
                .then(Commands.literal("gui")
                        .executes(ForgeBlockForgeCommands::openGui))
                .then(Commands.literal("info")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> info(context, registry))))
                .then(Commands.literal("dryrun")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> dryRun(context, registry))))
                .then(Commands.literal("build")
                        .requires(source -> source.hasPermission(2))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> buildAtPlayer(context, registry, undoManager, BlueprintRotation.NONE))
                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                        .executes(context -> buildAtCoordinates(context, registry, undoManager, BlueprintRotation.NONE)))))
                                .then(Commands.literal("rotate")
                                        .then(rotationArgument()
                                                .executes(context -> buildAtPlayer(context, registry, undoManager, getRotation(context)))))))
                .then(Commands.literal("undo")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> undo(context, undoManager))));
    }

    private static int select(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgePlayerSelectionManager selectionManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.select(player.getUUID(), blueprint.getId());
        context.getSource().sendSuccess(
                () -> Component.literal("Selected BlockForge Forge blueprint: "
                        + blueprint.getId()
                        + " | rotation="
                        + selection.rotationDegrees()),
                false
        );
        return 1;
    }

    private static int selected(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgePlayerSelectionManager selectionManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.get(player.getUUID()).orElse(null);
        if (selection == null) {
            context.getSource().sendFailure(Component.literal("No BlockForge Forge blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        if (registry.get(selection.selectedBlueprintId()).isEmpty()) {
            selectionManager.clear(player.getUUID());
            context.getSource().sendFailure(Component.literal("Selected BlockForge Forge blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Selected BlockForge Forge blueprint: "
                        + selection.selectedBlueprintId()
                        + " | rotation="
                        + selection.rotationDegrees()),
                false
        );
        return 1;
    }

    private static int openGui(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ForgeBlueprintGuiNetworking.sendBlueprintList(player, true);
        return 1;
    }

    private static int rotate(
            CommandContext<CommandSourceStack> context,
            ForgePlayerSelectionManager selectionManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Integer degrees = getRotationDegrees(context);
        if (degrees == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.rotate(player.getUUID(), degrees).orElse(null);
        if (selection == null) {
            context.getSource().sendFailure(Component.literal("No BlockForge Forge blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge rotation set to " + selection.rotationDegrees() + "."),
                false
        );
        return 1;
    }

    private static int giveWand(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ItemStack stack = new ItemStack(ForgeModItems.BUILDER_WAND.get());
        boolean inserted = player.addItem(stack);
        if (!inserted) {
            player.drop(stack, false);
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Gave BlockForge Forge Builder Wand."),
                true
        );
        return 1;
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> blueprintIdArgument(
            ForgeBlueprintRegistry registry
    ) {
        return Commands.argument("id", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(registry.getIds(), builder));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> rotationArgument() {
        return Commands.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("0", "90", "180", "270"), builder));
    }

    private static int showFolder(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge blueprint folder: " + registry.getDirectory()),
                false
        );
        return 1;
    }

    private static int listExamples(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Built-in BlockForge Forge example blueprints:"), false);

        for (ForgeExampleBlueprintInstaller.ExampleBlueprint example : EXAMPLES.getExamples()) {
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
            ForgeBlueprintRegistry registry
    ) {
        ForgeExampleBlueprintInstaller.InstallResult result = EXAMPLES.install(registry.getDirectory());

        if (result.hasError()) {
            context.getSource().sendFailure(Component.literal(result.error()));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("Installed BlockForge Forge examples: installed="
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

    private static int reload(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        ForgeBlueprintRegistry.LoadSummary summary = registry.reload();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded " + summary.loadedCount() + " BlockForge Forge blueprint(s)."),
                true
        );

        for (String warning : summary.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }

        return summary.loadedCount();
    }

    private static int list(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        if (registry.getBlueprints().isEmpty()) {
            context.getSource().sendFailure(Component.literal(
                    "No blueprints loaded. Put JSON files in " + registry.getDirectory() + " and run /blockforge reload."
            ));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Loaded BlockForge Forge blueprints:"), false);

        for (Blueprint blueprint : registry.getBlueprints()) {
            context.getSource().sendSuccess(() -> Component.literal("- " + describe(blueprint)), false);
        }

        return registry.getBlueprints().size();
    }

    private static int info(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal(describe(blueprint)
                        + " | schemaVersion="
                        + blueprint.getSchemaVersion()
                        + " | description="
                        + blueprint.getDescription()),
                false
        );
        return 1;
    }

    private static int dryRun(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        ForgeBlueprintPlacer.PlacementResult result = PLACER.dryRun(
                context.getSource().getLevel(),
                getBasePosition(context),
                blueprint,
                BlueprintRotation.NONE
        );
        sendDryRunResult(context.getSource(), blueprint, result);
        return result.placedBlocks();
    }

    private static int buildAtPlayer(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgeUndoManager undoManager,
            BlueprintRotation rotation
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null || rotation == null) {
            return 0;
        }

        return build(context, registry, undoManager, player, player.blockPosition(), rotation);
    }

    private static int buildAtCoordinates(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgeUndoManager undoManager,
            BlueprintRotation rotation
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null || rotation == null) {
            return 0;
        }

        BlockPos basePos = new BlockPos(
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "z")
        );
        return build(context, registry, undoManager, player, basePos, rotation);
    }

    private static int build(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgeUndoManager undoManager,
            ServerPlayer player,
            BlockPos basePos,
            BlueprintRotation rotation
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        ForgeBlueprintPlacer.PlacementResult result = PLACER.place(
                context.getSource().getLevel(),
                player,
                basePos,
                blueprint,
                rotation
        );

        if (result.snapshot() != null) {
            undoManager.record(result.snapshot());
        }

        sendPlacementResult(context.getSource(), result);
        return result.placedBlocks();
    }

    private static int undo(
            CommandContext<CommandSourceStack> context,
            ForgeUndoManager undoManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ForgeUndoManager.PlacementSnapshot snapshot = undoManager.popLatest(player.getUUID()).orElse(null);
        if (snapshot == null) {
            context.getSource().sendFailure(Component.literal("No BlockForge Forge undo snapshot available for this player."));
            return 0;
        }

        ForgeUndoManager.UndoResult result = undoManager.restore(context.getSource().getLevel(), player, snapshot);
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge undo restored " + result.restoredBlocks() + " blocks."),
                true
        );
        return result.restoredBlocks();
    }

    private static void sendPlacementResult(
            CommandSourceStack source,
            ForgeBlueprintPlacer.PlacementResult result
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
                () -> Component.literal("BlockForge Forge build complete: placed "
                        + result.placedBlocks()
                        + " blocks. skipped: missingPalette="
                        + result.skippedMissingPalette()
                        + ", invalidBlockId="
                        + result.skippedInvalidBlockIds()
                        + ", invalidProperties="
                        + result.skippedInvalidProperties()
                        + ", outOfWorld="
                        + result.skippedOutOfWorld()
                        + ". appliedProperties="
                        + result.appliedProperties()
                        + ". totalBlocks="
                        + result.totalBlocks()
                        + ". Use /blockforge undo to revert blocks."),
                true
        );
    }

    private static void sendDryRunResult(
            CommandSourceStack source,
            Blueprint blueprint,
            ForgeBlueprintPlacer.PlacementResult result
    ) {
        source.sendSuccess(
                () -> Component.literal("BlockForge Forge dryrun: "
                        + blueprint.getId()
                        + " | "
                        + blueprint.getName()
                        + " | schemaVersion="
                        + blueprint.getSchemaVersion()
                        + " | size="
                        + blueprint.getSize().format()
                        + " | blockCount="
                        + result.totalBlocks()
                        + " | paletteCount="
                        + blueprint.getPalette().size()
                        + " | validBuildPlan="
                        + result.validBuildPlan()
                        + " | estimatedPlacedBlocks="
                        + result.placedBlocks()
                        + " | missingPalette="
                        + result.skippedMissingPalette()
                        + " | invalidBlockId="
                        + result.skippedInvalidBlockIds()
                        + " | invalidProperties="
                        + result.skippedInvalidProperties()
                        + " | outOfWorld="
                        + result.skippedOutOfWorld()
                        + " | exceedsMaxBlockLimit="
                        + result.tooLarge()),
                false
        );

        if (result.empty()) {
            source.sendFailure(Component.literal("Blueprint has no blocks and cannot be built."));
        }
    }

    private static BlockPos getBasePosition(CommandContext<CommandSourceStack> context) {
        try {
            return context.getSource().getPlayerOrException().blockPosition();
        } catch (Exception error) {
            return BlockPos.ZERO;
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

    private static BlueprintRotation getRotation(CommandContext<CommandSourceStack> context) {
        try {
            return BlueprintRotation.fromDegrees(StringArgumentType.getString(context, "rotation"));
        } catch (IllegalArgumentException error) {
            context.getSource().sendFailure(Component.literal(error.getMessage()));
            return null;
        }
    }

    private static Integer getRotationDegrees(CommandContext<CommandSourceStack> context) {
        try {
            String value = StringArgumentType.getString(context, "rotation");
            BlueprintRotation.fromDegrees(value);
            return Integer.parseInt(value);
        } catch (IllegalArgumentException error) {
            context.getSource().sendFailure(Component.literal(error.getMessage()));
            return null;
        }
    }

    private static Blueprint findBlueprint(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        String id = StringArgumentType.getString(context, "id");
        return registry.get(id).orElseGet(() -> {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge Forge blueprint id: " + id));
            return null;
        });
    }

    private static String describe(Blueprint blueprint) {
        return blueprint.getId()
                + " | "
                + blueprint.getName()
                + " | schemaVersion="
                + blueprint.getSchemaVersion()
                + " | size="
                + blueprint.getSize().format()
                + " | blocks="
                + blueprint.getBlockCount();
    }
}

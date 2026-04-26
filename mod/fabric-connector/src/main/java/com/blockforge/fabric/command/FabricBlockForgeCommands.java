package com.blockforge.fabric.command;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.blueprint.FabricExampleBlueprintInstaller;
import com.blockforge.fabric.builder.FabricBlueprintPlacer;
import com.blockforge.fabric.undo.FabricUndoManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public final class FabricBlockForgeCommands {
    private static final FabricBlueprintPlacer PLACER = new FabricBlueprintPlacer();
    private static final FabricExampleBlueprintInstaller EXAMPLES = new FabricExampleBlueprintInstaller();

    private FabricBlockForgeCommands() {
    }

    public static void register(FabricBlueprintRegistry registry, FabricUndoManager undoManager) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("blockforge")
                .then(CommandManager.literal("folder")
                        .executes(context -> showFolder(context, registry)))
                .then(CommandManager.literal("examples")
                        .then(CommandManager.literal("list")
                                .executes(FabricBlockForgeCommands::listExamples))
                        .then(CommandManager.literal("install")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> installExamples(context, registry))))
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> reload(context, registry)))
                .then(CommandManager.literal("list")
                        .executes(context -> list(context, registry)))
                .then(CommandManager.literal("info")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> info(context, registry))))
                .then(CommandManager.literal("dryrun")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> dryRun(context, registry))))
                .then(CommandManager.literal("build")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> buildAtPlayer(context, registry, undoManager, BlueprintRotation.NONE))
                                .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                        .then(CommandManager.argument("y", IntegerArgumentType.integer())
                                                .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                                        .executes(context -> buildAtCoordinates(context, registry, undoManager, BlueprintRotation.NONE)))))
                                .then(CommandManager.literal("rotate")
                                        .then(rotationArgument()
                                                .executes(context -> buildAtPlayer(context, registry, undoManager, getRotation(context)))))))
                .then(CommandManager.literal("undo")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(context -> undo(context, undoManager)))));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> blueprintIdArgument(
            FabricBlueprintRegistry registry
    ) {
        return CommandManager.argument("id", StringArgumentType.word())
                .suggests((context, builder) -> CommandSource.suggestMatching(registry.getIds(), builder));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> rotationArgument() {
        return CommandManager.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> CommandSource.suggestMatching(List.of("0", "90", "180", "270"), builder));
    }

    private static int showFolder(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        context.getSource().sendFeedback(
                () -> Text.literal("BlockForge Fabric blueprint folder: " + registry.getDirectory()),
                false
        );
        return 1;
    }

    private static int listExamples(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Built-in BlockForge Fabric example blueprints:"), false);

        for (FabricExampleBlueprintInstaller.ExampleBlueprint example : EXAMPLES.getExamples()) {
            context.getSource().sendFeedback(
                    () -> Text.literal("- " + example.id() + " | " + example.name() + " | " + example.fileName()),
                    false
            );
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Install them with /blockforge examples install, then run /blockforge reload."),
                false
        );
        return EXAMPLES.getExamples().size();
    }

    private static int installExamples(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        FabricExampleBlueprintInstaller.InstallResult result = EXAMPLES.install(registry.getDirectory());

        if (result.hasError()) {
            context.getSource().sendError(Text.literal(result.error()));
            return 0;
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Installed BlockForge Fabric examples: installed="
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
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        FabricBlueprintRegistry.LoadSummary summary = registry.reload();
        context.getSource().sendFeedback(
                () -> Text.literal("Loaded " + summary.loadedCount() + " BlockForge Fabric blueprint(s)."),
                true
        );

        for (String warning : summary.warnings()) {
            context.getSource().sendError(Text.literal("Warning: " + warning));
        }

        return summary.loadedCount();
    }

    private static int list(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        if (registry.getBlueprints().isEmpty()) {
            context.getSource().sendError(Text.literal(
                    "No blueprints loaded. Put JSON files in " + registry.getDirectory() + " and run /blockforge reload."
            ));
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.literal("Loaded BlockForge Fabric blueprints:"), false);

        for (Blueprint blueprint : registry.getBlueprints()) {
            context.getSource().sendFeedback(() -> Text.literal("- " + describe(blueprint)), false);
        }

        return registry.getBlueprints().size();
    }

    private static int info(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        context.getSource().sendFeedback(
                () -> Text.literal(describe(blueprint)
                        + " | schemaVersion="
                        + blueprint.getSchemaVersion()
                        + " | description="
                        + blueprint.getDescription()),
                false
        );
        return 1;
    }

    private static int dryRun(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        BlockPos basePos = getBasePosition(context);
        FabricBlueprintPlacer.PlacementResult result = PLACER.dryRun(
                context.getSource().getWorld(),
                basePos,
                blueprint,
                BlueprintRotation.NONE
        );
        sendDryRunResult(context.getSource(), blueprint, result);
        return result.placedBlocks();
    }

    private static int buildAtPlayer(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricUndoManager undoManager,
            BlueprintRotation rotation
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null || rotation == null) {
            return 0;
        }

        return build(context, registry, undoManager, player, player.getBlockPos(), rotation);
    }

    private static int buildAtCoordinates(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricUndoManager undoManager,
            BlueprintRotation rotation
    ) {
        ServerPlayerEntity player = getPlayer(context);
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
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricUndoManager undoManager,
            ServerPlayerEntity player,
            BlockPos basePos,
            BlueprintRotation rotation
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        FabricBlueprintPlacer.PlacementResult result = PLACER.place(
                context.getSource().getWorld(),
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
            CommandContext<ServerCommandSource> context,
            FabricUndoManager undoManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        FabricUndoManager.PlacementSnapshot snapshot = undoManager.popLatest(player.getUuid()).orElse(null);
        if (snapshot == null) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric undo snapshot available for this player."));
            return 0;
        }

        FabricUndoManager.UndoResult result = undoManager.restore(context.getSource().getWorld(), player, snapshot);
        context.getSource().sendFeedback(
                () -> Text.literal("BlockForge Fabric undo restored " + result.restoredBlocks() + " blocks."),
                true
        );
        return result.restoredBlocks();
    }

    private static void sendPlacementResult(
            ServerCommandSource source,
            FabricBlueprintPlacer.PlacementResult result
    ) {
        if (result.tooLarge()) {
            source.sendError(Text.literal(
                    "Blueprint has " + result.totalBlocks() + " blocks, which exceeds the "
                            + result.maxBlocks() + " block safety limit."
            ));
            return;
        }

        if (result.empty()) {
            source.sendError(Text.literal("Blueprint has no blocks and cannot be built."));
            return;
        }

        source.sendFeedback(
                () -> Text.literal("BlockForge Fabric build complete: placed "
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
            ServerCommandSource source,
            Blueprint blueprint,
            FabricBlueprintPlacer.PlacementResult result
    ) {
        source.sendFeedback(
                () -> Text.literal("BlockForge Fabric dryrun: "
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
            source.sendError(Text.literal("Blueprint has no blocks and cannot be built."));
        }
    }

    private static BlockPos getBasePosition(CommandContext<ServerCommandSource> context) {
        try {
            return context.getSource().getPlayer().getBlockPos();
        } catch (Exception error) {
            return BlockPos.ORIGIN;
        }
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context) {
        try {
            return context.getSource().getPlayer();
        } catch (Exception error) {
            context.getSource().sendError(Text.literal("This command requires a player source."));
            return null;
        }
    }

    private static BlueprintRotation getRotation(CommandContext<ServerCommandSource> context) {
        try {
            return BlueprintRotation.fromDegrees(StringArgumentType.getString(context, "rotation"));
        } catch (IllegalArgumentException error) {
            context.getSource().sendError(Text.literal(error.getMessage()));
            return null;
        }
    }

    private static Blueprint findBlueprint(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        String id = StringArgumentType.getString(context, "id");
        return registry.get(id).orElseGet(() -> {
            context.getSource().sendError(Text.literal("Unknown BlockForge Fabric blueprint id: " + id));
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

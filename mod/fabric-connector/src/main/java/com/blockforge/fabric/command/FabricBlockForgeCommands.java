package com.blockforge.fabric.command;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRequirement;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourcePriority;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceScanResult;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.common.pack.BlueprintPackRegistryEntry;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.blockforge.common.rotation.BlueprintRotation;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.fabric.blueprint.FabricBlueprintRegistry;
import com.blockforge.fabric.blueprint.FabricExampleBlueprintInstaller;
import com.blockforge.fabric.builder.FabricBlueprintPlacer;
import com.blockforge.fabric.material.FabricMaterialBuildGate;
import com.blockforge.fabric.material.source.FabricMaterialSourceScanner;
import com.blockforge.fabric.material.source.FabricMaterialSourceSettings;
import com.blockforge.fabric.network.FabricBlueprintGuiNetworking;
import com.blockforge.fabric.player.FabricPlayerSelectionManager;
import com.blockforge.fabric.registry.FabricModItems;
import com.blockforge.fabric.undo.FabricUndoManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public final class FabricBlockForgeCommands {
    private static final FabricBlueprintPlacer PLACER = new FabricBlueprintPlacer();
    private static final FabricExampleBlueprintInstaller EXAMPLES = new FabricExampleBlueprintInstaller();
    private static final FabricMaterialBuildGate MATERIALS = new FabricMaterialBuildGate();
    private static final FabricMaterialSourceScanner SOURCE_SCANNER = new FabricMaterialSourceScanner();

    private FabricBlockForgeCommands() {
    }

    public static void register(
            FabricBlueprintRegistry registry,
            FabricUndoManager undoManager,
            FabricPlayerSelectionManager selectionManager
    ) {
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
                .then(CommandManager.literal("packs")
                        .then(CommandManager.literal("folder")
                                .executes(context -> packsFolder(context, registry)))
                        .then(CommandManager.literal("reload")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> reload(context, registry)))
                        .then(CommandManager.literal("list")
                                .executes(context -> packsList(context, registry)))
                        .then(CommandManager.literal("info")
                                .then(packIdArgument(registry)
                                        .executes(context -> packsInfo(context, registry))))
                        .then(CommandManager.literal("blueprints")
                                .then(packIdArgument(registry)
                                        .executes(context -> packsBlueprints(context, registry))))
                        .then(CommandManager.literal("validate")
                                .executes(context -> packsValidate(context, registry))))
                .then(CommandManager.literal("list")
                        .executes(context -> list(context, registry)))
                .then(CommandManager.literal("select")
                        .then(blueprintIdArgument(registry)
                                .executes(context -> select(context, registry, selectionManager))))
                .then(CommandManager.literal("selected")
                        .executes(context -> selected(context, registry, selectionManager)))
                .then(CommandManager.literal("rotate")
                        .then(rotationArgument()
                                .executes(context -> rotate(context, selectionManager))))
                .then(CommandManager.literal("wand")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(FabricBlockForgeCommands::giveWand))
                .then(CommandManager.literal("gui")
                        .executes(FabricBlockForgeCommands::openGui))
                .then(CommandManager.literal("materials")
                        .then(CommandManager.literal("selected")
                                .executes(context -> materialsSelected(context, registry, selectionManager)))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> materials(context, registry))))
                .then(CommandManager.literal("sources")
                        .then(CommandManager.literal("status")
                                .executes(FabricBlockForgeCommands::sourcesStatus))
                        .then(CommandManager.literal("enable")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> setSourcesEnabled(context, true)))
                        .then(CommandManager.literal("disable")
                                .requires(source -> source.hasPermissionLevel(2))
                                .executes(context -> setSourcesEnabled(context, false)))
                        .then(CommandManager.literal("priority")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(sourcePriorityArgument()
                                        .executes(FabricBlockForgeCommands::setSourcePriority)))
                        .then(CommandManager.literal("radius")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 32))
                                        .executes(FabricBlockForgeCommands::setSourceRadius)))
                        .then(CommandManager.literal("scan")
                                .executes(FabricBlockForgeCommands::sourcesScan))
                        .then(CommandManager.literal("selected")
                                .executes(context -> sourcesSelected(context, registry, selectionManager))))
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

    private static int select(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.select(player.getUuid(), blueprint.getId());
        FabricBlueprintGuiNetworking.syncPreviewSelection(player, blueprint, selection.rotationDegrees());
        context.getSource().sendFeedback(
                () -> Text.literal("Selected BlockForge Fabric blueprint: "
                        + blueprint.getId()
                        + " | rotation="
                        + selection.rotationDegrees()),
                false
        );
        return 1;
    }

    private static int selected(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.get(player.getUuid()).orElse(null);
        if (selection == null) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        if (registry.get(selection.selectedBlueprintId()).isEmpty()) {
            selectionManager.clear(player.getUuid());
            FabricBlueprintGuiNetworking.clearPreview(player, "Selected BlockForge Fabric blueprint no longer exists.");
            context.getSource().sendError(Text.literal("Selected BlockForge Fabric blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        FabricBlueprintGuiNetworking.syncPreviewSelection(player);
        context.getSource().sendFeedback(
                () -> Text.literal("Selected BlockForge Fabric blueprint: "
                        + selection.selectedBlueprintId()
                        + " | rotation="
                        + selection.rotationDegrees()
                        + ". Hold Builder Wand and look at a block to see Ghost Preview."),
                false
        );
        return 1;
    }

    private static int openGui(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        FabricBlueprintGuiNetworking.sendBlueprintList(player, true);
        return 1;
    }

    private static int rotate(
            CommandContext<ServerCommandSource> context,
            FabricPlayerSelectionManager selectionManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Integer degrees = getRotationDegrees(context);
        if (degrees == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.rotate(player.getUuid(), degrees).orElse(null);
        if (selection == null) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        FabricBlueprintGuiNetworking.syncPreviewSelection(player);
        context.getSource().sendFeedback(
                () -> Text.literal("BlockForge Fabric rotation set to " + selection.rotationDegrees() + "."),
                false
        );
        return 1;
    }

    private static int giveWand(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ItemStack stack = new ItemStack(FabricModItems.BUILDER_WAND);
        boolean inserted = player.giveItemStack(stack);
        if (!inserted) {
            player.dropItem(stack, false);
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Gave BlockForge Fabric Builder Wand."),
                true
        );
        return 1;
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> blueprintIdArgument(
            FabricBlueprintRegistry registry
    ) {
        return CommandManager.argument("id", BlueprintIdArgumentType.id())
                .suggests((context, builder) -> CommandSource.suggestMatching(registry.getIds(), builder));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> rotationArgument() {
        return CommandManager.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> CommandSource.suggestMatching(List.of("0", "90", "180", "270"), builder));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> sourcePriorityArgument() {
        return CommandManager.argument("priority", StringArgumentType.word())
                .suggests((context, builder) -> CommandSource.suggestMatching(List.of(
                        "PLAYER_FIRST",
                        "CONTAINER_FIRST",
                        "PLAYER_ONLY",
                        "CONTAINER_ONLY"
                ), builder));
    }

    private static RequiredArgumentBuilder<ServerCommandSource, String> packIdArgument(
            FabricBlueprintRegistry registry
    ) {
        return CommandManager.argument("packId", StringArgumentType.word())
                .suggests((context, builder) -> CommandSource.suggestMatching(
                        registry.getPacks().stream().map(pack -> pack.manifest().packId()).toList(),
                        builder
                ));
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
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            FabricBlueprintGuiNetworking.syncPreviewSelection(player);
        } catch (Exception ignored) {
            // Console reloads have no client preview to update.
        }
        context.getSource().sendFeedback(
                () -> Text.literal("Loaded "
                        + summary.loadedCount()
                        + " BlockForge Fabric blueprint(s) from loose files and "
                        + summary.packCount()
                        + " pack(s)."),
                true
        );

        for (String warning : summary.warnings()) {
            context.getSource().sendError(Text.literal("Warning: " + warning));
        }

        return summary.loadedCount();
    }

    private static int packsFolder(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        context.getSource().sendFeedback(
                () -> Text.literal("BlockForge Fabric pack folder: " + registry.getPackDirectory()),
                false
        );
        return 1;
    }

    private static int packsList(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        if (registry.getPacks().isEmpty()) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric packs loaded from " + registry.getPackDirectory()));
            return 0;
        }

        context.getSource().sendFeedback(() -> Text.literal("Loaded BlockForge Fabric packs:"), false);
        for (LoadedBlueprintPack pack : registry.getPacks()) {
            context.getSource().sendFeedback(
                    () -> Text.literal("- "
                            + pack.manifest().packId()
                            + " | "
                            + pack.manifest().name()
                            + " | version="
                            + pack.manifest().version()
                            + " | blueprints="
                            + pack.entries().size()
                            + " | warnings="
                            + pack.warnings().size()),
                    false
            );
        }
        return registry.getPacks().size();
    }

    private static int packsInfo(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        LoadedBlueprintPack pack = findPack(context, registry);
        if (pack == null) {
            return 0;
        }

        context.getSource().sendFeedback(
                () -> Text.literal(pack.manifest().packId()
                        + " | "
                        + pack.manifest().name()
                        + " | version="
                        + pack.manifest().version()
                        + " | author="
                        + pack.manifest().author()
                        + " | tags="
                        + String.join(",", pack.manifest().tags())
                        + " | description="
                        + pack.manifest().description()),
                false
        );
        return 1;
    }

    private static int packsBlueprints(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        LoadedBlueprintPack pack = findPack(context, registry);
        if (pack == null) {
            return 0;
        }

        for (BlueprintPackRegistryEntry entry : pack.entries()) {
            context.getSource().sendFeedback(
                    () -> Text.literal("- " + entry.registryId() + " | " + entry.name() + " | " + entry.path()),
                    false
            );
        }
        return pack.entries().size();
    }

    private static int packsValidate(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        var result = registry.validatePacks();
        context.getSource().sendFeedback(
                () -> Text.literal("Validated BlockForge Fabric packs: packs="
                        + result.packs().size()
                        + " | blueprints="
                        + result.blueprints().size()
                        + " | warnings="
                        + result.warnings().size()),
                false
        );
        for (String warning : result.warnings()) {
            context.getSource().sendError(Text.literal("Warning: " + warning));
        }
        return result.packs().size();
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
        sendSourceSettings(context.getSource());
        ServerPlayerEntity player = getPlayerOrNull(context);
        if (player != null) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getWorld(), basePos));
        }
        return result.placedBlocks();
    }

    private static int materials(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        sendMaterialReport(context.getSource(), report);
        if (FabricMaterialSourceSettings.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getWorld(), player.getBlockPos()));
        }
        return 1;
    }

    private static int materialsSelected(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.get(player.getUuid()).orElse(null);
        if (selection == null) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        Blueprint blueprint = registry.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            selectionManager.clear(player.getUuid());
            FabricBlueprintGuiNetworking.clearPreview(player, "Selected BlockForge Fabric blueprint no longer exists.");
            context.getSource().sendError(Text.literal("Selected BlockForge Fabric blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        sendMaterialReport(context.getSource(), report);
        if (FabricMaterialSourceSettings.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getWorld(), player.getBlockPos()));
        }
        return 1;
    }

    private static int sourcesStatus(CommandContext<ServerCommandSource> context) {
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int setSourcesEnabled(CommandContext<ServerCommandSource> context, boolean enabled) {
        FabricMaterialSourceSettings.setEnableNearbyContainers(enabled);
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int setSourcePriority(CommandContext<ServerCommandSource> context) {
        try {
            MaterialSourcePriority priority = MaterialSourcePriority.valueOf(StringArgumentType.getString(context, "priority").toUpperCase());
            FabricMaterialSourceSettings.setMaterialSourcePriority(priority);
            sendSourceSettings(context.getSource());
            return 1;
        } catch (IllegalArgumentException error) {
            context.getSource().sendError(Text.literal("Unsupported material source priority. Use PLAYER_FIRST, CONTAINER_FIRST, PLAYER_ONLY, or CONTAINER_ONLY."));
            return 0;
        }
    }

    private static int setSourceRadius(CommandContext<ServerCommandSource> context) {
        FabricMaterialSourceSettings.setNearbyContainerSearchRadius(IntegerArgumentType.getInteger(context, "radius"));
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int sourcesScan(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        FabricMaterialSourceScanner.Scan scan = SOURCE_SCANNER.scan(
                player,
                context.getSource().getWorld(),
                player.getBlockPos(),
                FabricMaterialSourceSettings.config()
        );
        MaterialSourceScanResult result = scan.result();
        context.getSource().sendFeedback(
                () -> Text.literal("BlockForge Fabric material sources scan: enabled="
                        + FabricMaterialSourceSettings.enableNearbyContainers()
                        + " | radius="
                        + FabricMaterialSourceSettings.nearbyContainerSearchRadius()
                        + " | foundContainers="
                        + result.foundContainers()
                        + " | scannedBlocks="
                        + result.scannedBlocks()),
                false
        );
        result.sources()
                .stream()
                .limit(12)
                .forEach(source -> context.getSource().sendFeedback(
                        () -> Text.literal("- " + source.displayName() + " @ " + source.x() + "," + source.y() + "," + source.z()),
                        false
                ));
        result.warnings().forEach(warning -> context.getSource().sendError(Text.literal("Warning: " + warning)));
        return result.foundContainers();
    }

    private static int sourcesSelected(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry,
            FabricPlayerSelectionManager selectionManager
    ) {
        ServerPlayerEntity player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        PlayerSelection selection = selectionManager.get(player.getUuid()).orElse(null);
        if (selection == null) {
            context.getSource().sendError(Text.literal("No BlockForge Fabric blueprint selected. Use /blockforge select <id> first."));
            return 0;
        }

        Blueprint blueprint = registry.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            context.getSource().sendError(Text.literal("Selected BlockForge Fabric blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        MaterialSourceReport report = MATERIALS.sourceReport(blueprint, player, context.getSource().getWorld(), player.getBlockPos());
        sendMaterialSourceReport(context.getSource(), report);
        return report.totalRequiredItems();
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

        FabricBlueprintPlacer.PlacementResult dryRun = PLACER.dryRun(
                context.getSource().getWorld(),
                basePos,
                blueprint,
                rotation
        );
        if (dryRun.tooLarge() || dryRun.empty()) {
            sendPlacementResult(context.getSource(), dryRun);
            return 0;
        }
        if (dryRun.placedBlocks() == 0) {
            context.getSource().sendError(Text.literal("Blueprint has no valid placeable blocks and cannot be built."));
            return 0;
        }

        FabricMaterialBuildGate.BuildMaterialResult materialResult = MATERIALS.prepare(player, context.getSource().getWorld(), basePos, blueprint);
        if (!materialResult.allowed()) {
            context.getSource().sendError(Text.literal(materialResult.message()));
            sendMissingMaterials(context.getSource(), materialResult.report());
            if (materialResult.sourceReport() != null) {
                sendMaterialSourceReport(context.getSource(), materialResult.sourceReport());
            }
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
            undoManager.record(result.snapshot().withMaterialTransaction(materialResult.transaction()));
        } else if (materialResult.transaction() != null) {
            MaterialRefundResult rollbackResult = MATERIALS.rollback(player, materialResult.transaction());
            sendPlacementResult(context.getSource(), result);
            if (materialResult.transaction().hasConsumedItems()) {
                context.getSource().sendError(Text.literal("Build placed no blocks; rolled back "
                        + rollbackResult.refundedItems()
                        + " consumed items"
                        + (rollbackResult.droppedItems() > 0
                        ? ", dropped " + rollbackResult.droppedItems() + " items near player."
                        : ".")));
            } else {
                sendMaterialResult(context.getSource(), materialResult);
            }
            return 0;
        }

        sendPlacementResult(context.getSource(), result);
        sendMaterialResult(context.getSource(), materialResult);
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
        MaterialRefundResult refundResult = MATERIALS.refund(player, snapshot.materialTransaction());
        sendUndoResult(context.getSource(), result, refundResult, snapshot);
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
                        + ". Use /blockforge undo to restore blocks and refund materials."),
                true
        );
    }

    private static void sendUndoResult(
            ServerCommandSource source,
            FabricUndoManager.UndoResult result,
            MaterialRefundResult refundResult,
            FabricUndoManager.PlacementSnapshot snapshot
    ) {
        if (snapshot.materialTransaction() == null || snapshot.materialTransaction().creativeBypass()) {
            source.sendFeedback(
                    () -> Text.literal("Undo complete. Restored "
                            + result.restoredBlocks()
                            + " blocks. No materials were consumed."),
                    true
            );
            return;
        }

        source.sendFeedback(
                () -> Text.literal("Undo complete. Restored "
                        + result.restoredBlocks()
                        + " blocks and refunded "
                        + refundResult.refundedItems()
                        + " items"
                        + (refundResult.droppedItems() > 0
                        ? ", dropped " + refundResult.droppedItems() + " items near player."
                        : ".")),
                true
        );

        for (String warning : refundResult.warnings()) {
            source.sendError(Text.literal("Warning: " + warning));
        }
    }

    private static void sendMaterialResult(
            ServerCommandSource source,
            FabricMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        if (materialResult.creativeBypass()) {
            source.sendFeedback(() -> Text.literal("Creative mode: no materials consumed."), false);
            return;
        }

        source.sendFeedback(
                () -> Text.literal("Consumed "
                        + materialResult.consumedItems()
                        + (materialResult.consumedFromNearbyContainers() > 0
                        ? " items (nearbyContainers=" + materialResult.consumedFromNearbyContainers() + ")"
                        : " items")
                        + ". Use /blockforge undo to restore blocks and refund materials."),
                true
        );
    }

    private static void sendMaterialReport(ServerCommandSource source, MaterialReport report) {
        source.sendFeedback(
                () -> Text.literal("BlockForge Fabric materials: "
                        + report.blueprintId()
                        + " | requiredItems="
                        + report.totalRequiredItems()
                        + " | availableItems="
                        + report.totalAvailableItems()
                        + " | missingItemTypes="
                        + report.missingItemTypes()
                        + " | enoughMaterials="
                        + report.enoughMaterials()),
                false
        );
        sendMissingMaterials(source, report);
    }

    private static void sendSourceSettings(ServerCommandSource source) {
        source.sendFeedback(
                () -> Text.literal("BlockForge Fabric material sources settings: enabled="
                        + FabricMaterialSourceSettings.enableNearbyContainers()
                        + " | priority="
                        + FabricMaterialSourceSettings.materialSourcePriority()
                        + " | radius="
                        + FabricMaterialSourceSettings.nearbyContainerSearchRadius()
                        + " | maxContainers="
                        + FabricMaterialSourceSettings.nearbyContainerMaxScanned()
                        + " | returnRefundsToOriginalSource="
                        + FabricMaterialSourceSettings.returnRefundsToOriginalSource()
                        + " | allowPartialFromContainers="
                        + FabricMaterialSourceSettings.allowPartialFromContainers()),
                false
        );
    }

    private static void sendMaterialSourceReport(ServerCommandSource source, MaterialSourceReport report) {
        source.sendFeedback(
                () -> Text.literal("BlockForge Fabric material sources: "
                        + report.blueprintId()
                        + " | enabled="
                        + FabricMaterialSourceSettings.enableNearbyContainers()
                        + " | priority="
                        + FabricMaterialSourceSettings.materialSourcePriority()
                        + " | radius="
                        + FabricMaterialSourceSettings.nearbyContainerSearchRadius()
                        + " | requiredItems="
                        + report.totalRequiredItems()
                        + " | availableItems="
                        + report.totalAvailableItems()
                        + " | missingItems="
                        + report.totalMissingItems()
                        + " | enoughMaterials="
                        + report.enoughMaterials()
                        + " | playerReserved="
                        + reservedFrom(report, MaterialSourceType.PLAYER_INVENTORY)
                        + " | containerReserved="
                        + reservedFrom(report, MaterialSourceType.NEARBY_CONTAINER)),
                false
        );

        int shown = 0;
        for (MaterialSourceItemEntry entry : report.entries()) {
            if (shown >= 8) {
                source.sendFeedback(() -> Text.literal("... more material source entries omitted."), false);
                break;
            }
            if (entry.reserved() <= 0 && entry.available() <= 0) {
                continue;
            }
            source.sendFeedback(
                    () -> Text.literal("- "
                            + entry.itemId()
                            + " source="
                            + (entry.source() == null ? "unknown" : entry.source().displayName())
                            + " available="
                            + entry.available()
                            + ", reserved="
                            + entry.reserved()),
                    false
            );
            shown++;
        }
        report.warnings().forEach(warning -> source.sendError(Text.literal("Warning: " + warning)));
    }

    private static int reservedFrom(MaterialSourceReport report, MaterialSourceType type) {
        return report.entries()
                .stream()
                .filter(entry -> entry.source() != null && entry.source().type() == type)
                .mapToInt(MaterialSourceItemEntry::reserved)
                .sum();
    }

    private static void sendMissingMaterials(ServerCommandSource source, MaterialReport report) {
        if (report == null) {
            return;
        }

        report.requirements()
                .stream()
                .filter(requirement -> requirement.missing() > 0)
                .limit(10)
                .forEach(requirement -> source.sendError(Text.literal("Missing materials: " + describeRequirement(requirement))));
    }

    private static String describeRequirement(MaterialRequirement requirement) {
        return requirement.itemId()
                + " missing="
                + requirement.missing()
                + " required="
                + requirement.required()
                + " available="
                + requirement.available();
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

    private static ServerPlayerEntity getPlayerOrNull(CommandContext<ServerCommandSource> context) {
        try {
            return context.getSource().getPlayer();
        } catch (Exception error) {
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

    private static Integer getRotationDegrees(CommandContext<ServerCommandSource> context) {
        try {
            String value = StringArgumentType.getString(context, "rotation");
            BlueprintRotation.fromDegrees(value);
            return Integer.parseInt(value);
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

    private static LoadedBlueprintPack findPack(
            CommandContext<ServerCommandSource> context,
            FabricBlueprintRegistry registry
    ) {
        String packId = StringArgumentType.getString(context, "packId");
        return registry.getPacks()
                .stream()
                .filter(pack -> pack.manifest().packId().equals(packId))
                .findFirst()
                .orElseGet(() -> {
                    context.getSource().sendError(Text.literal("Unknown BlockForge Fabric pack id: " + packId));
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

    private static final class BlueprintIdArgumentType implements ArgumentType<String> {
        private static BlueprintIdArgumentType id() {
            return new BlueprintIdArgumentType();
        }

        @Override
        public String parse(StringReader reader) {
            int start = reader.getCursor();
            while (reader.canRead() && reader.peek() != ' ') {
                reader.skip();
            }
            return reader.getString().substring(start, reader.getCursor());
        }
    }
}

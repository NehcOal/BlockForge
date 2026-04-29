package com.blockforge.connector.command;

import com.blockforge.connector.BlockForgeConnector;
import com.blockforge.connector.build.BuildService;
import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.blueprint.ExampleBlueprintInstaller;
import com.blockforge.connector.blueprint.BlueprintRegistry;
import com.blockforge.connector.builder.BlueprintPlacer;
import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.material.MaterialBuildGate;
import com.blockforge.connector.material.MaterialRefundResult;
import com.blockforge.connector.material.MaterialReport;
import com.blockforge.connector.material.MaterialRequirement;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceAdapter;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceScanner;
import com.blockforge.connector.network.BlockForgeNetwork;
import com.blockforge.connector.player.PlayerBlueprintSelection;
import com.blockforge.connector.registry.ModItems;
import com.blockforge.connector.undo.PlacementSnapshot;
import com.blockforge.connector.undo.UndoManager;
import com.blockforge.common.gameplay.BuilderWandMode;
import com.blockforge.common.gameplay.BuilderWandState;
import com.blockforge.common.buildplan.BuildPlan;
import com.blockforge.common.buildplan.BuildPlanFactory;
import com.blockforge.common.buildplan.BuildPlanOptions;
import com.blockforge.common.buildplan.BuildPlanStatus;
import com.blockforge.common.buildplan.BuildPlanValidator;
import com.blockforge.common.buildplan.BuildProgress;
import com.blockforge.common.buildplan.BuildStepStatus;
import com.blockforge.common.buildplan.BuildPlanStepper;
import com.blockforge.common.material.source.MaterialSourceItemEntry;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.material.source.MaterialSourceScanResult;
import com.blockforge.common.material.source.MaterialSourceType;
import com.blockforge.common.pack.BlueprintPackRegistryEntry;
import com.blockforge.common.pack.LoadedBlueprintPack;
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.protection.BlockForgeProtectionRegion;
import com.blockforge.common.security.protection.ProtectionPreflightReport;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
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
    private static final MaterialBuildGate MATERIALS = new MaterialBuildGate();
    private static final BuildService BUILDS = new BuildService(BlockForgeConnector.UNDO);
    private static final NeoForgeMaterialSourceScanner SOURCE_SCANNER = new NeoForgeMaterialSourceScanner();
    private static final NeoForgeMaterialSourceAdapter SOURCE_ADAPTER = new NeoForgeMaterialSourceAdapter();

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
                .then(Commands.literal("packs")
                        .then(Commands.literal("folder")
                                .executes(context -> packsFolder(context, registry)))
                        .then(Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> reload(context, registry)))
                        .then(Commands.literal("list")
                                .executes(context -> packsList(context, registry)))
                        .then(Commands.literal("info")
                                .then(packIdArgument(registry)
                                        .executes(context -> packsInfo(context, registry))))
                        .then(Commands.literal("blueprints")
                                .then(packIdArgument(registry)
                                        .executes(context -> packsBlueprints(context, registry))))
                        .then(Commands.literal("validate")
                                .executes(context -> packsValidate(context, registry))))
                .then(Commands.literal("schematics")
                        .then(Commands.literal("folder")
                                .executes(context -> schematicsFolder(context, registry)))
                        .then(Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> reload(context, registry)))
                        .then(Commands.literal("list")
                                .executes(context -> schematicsList(context, registry)))
                        .then(Commands.literal("info")
                                .then(blueprintIdArgument(registry)
                                        .executes(context -> schematicsInfo(context, registry))))
                        .then(Commands.literal("validate")
                                .executes(context -> schematicsValidate(context, registry))))
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
                        .executes(BlockForgeCommands::giveWand)
                        .then(Commands.literal("mode")
                                .executes(BlockForgeCommands::showWandMode)
                                .then(Commands.argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                java.util.Arrays.stream(BuilderWandMode.values()).map(BuilderWandMode::id),
                                                builder
                                        ))
                                        .executes(BlockForgeCommands::setWandMode)))
                        .then(Commands.literal("cycle")
                                .executes(BlockForgeCommands::cycleWandMode))
                        .then(Commands.literal("options")
                                .executes(BlockForgeCommands::showWandOptions))
                        .then(Commands.literal("offset")
                                .then(Commands.argument("x", IntegerArgumentType.integer(-64, 64))
                                        .then(Commands.argument("y", IntegerArgumentType.integer(-64, 64))
                                                .then(Commands.argument("z", IntegerArgumentType.integer(-64, 64))
                                                        .executes(BlockForgeCommands::setWandOffset)))))
                        .then(Commands.literal("mirror")
                                .then(Commands.argument("axis", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("x", "z", "none"), builder))
                                        .executes(BlockForgeCommands::setWandMirror)))
                        .then(Commands.literal("replace")
                                .then(Commands.argument("mode", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("air_only", "allow_replace"), builder))
                                        .executes(BlockForgeCommands::setWandReplaceMode)))
                        .then(Commands.literal("anchor")
                                .then(Commands.literal("clear")
                                        .executes(BlockForgeCommands::clearWandAnchor))))
                .then(Commands.literal("gui")
                        .executes(BlockForgeCommands::openGui))
                .then(Commands.literal("buildplan")
                        .then(Commands.literal("create")
                                .then(blueprintIdArgument(registry)
                                        .executes(context -> buildPlanCreateAtPlayer(context, registry))
                                        .then(Commands.literal("at")
                                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                                        .executes(context -> buildPlanCreateAtCoordinates(context, registry))))))))
                        .then(Commands.literal("preview")
                                .executes(BlockForgeCommands::buildPlanPreview))
                        .then(Commands.literal("status")
                                .executes(BlockForgeCommands::buildPlanStatus))
                        .then(Commands.literal("start")
                                .executes(context -> buildPlanSetStatus(context, BuildPlanStatus.RUNNING)))
                        .then(Commands.literal("pause")
                                .executes(context -> buildPlanSetStatus(context, BuildPlanStatus.PAUSED)))
                        .then(Commands.literal("resume")
                                .executes(context -> buildPlanSetStatus(context, BuildPlanStatus.RUNNING)))
                        .then(Commands.literal("cancel")
                                .executes(context -> buildPlanSetStatus(context, BuildPlanStatus.CANCELLED)))
                        .then(Commands.literal("step")
                                .executes(BlockForgeCommands::buildPlanStep))
                        .then(Commands.literal("repair")
                                .executes(BlockForgeCommands::buildPlanRepair))
                        .then(Commands.literal("clear")
                                .executes(BlockForgeCommands::buildPlanClear)))
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
                .then(Commands.literal("materials")
                        .then(Commands.literal("selected")
                                .executes(context -> materialsSelected(context, registry)))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> materials(context, registry))))
                .then(Commands.literal("sources")
                        .then(Commands.literal("scan")
                                .executes(BlockForgeCommands::sourcesScan))
                        .then(Commands.literal("selected")
                                .executes(context -> sourcesSelected(context, registry))))
                .then(Commands.literal("protection")
                        .then(Commands.literal("folder")
                                .executes(BlockForgeCommands::protectionFolder))
                        .then(Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(BlockForgeCommands::protectionReload))
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::protectionList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("region", StringArgumentType.word())
                                        .executes(BlockForgeCommands::protectionInfo)))
                        .then(Commands.literal("check")
                                .then(blueprintIdArgument(registry)
                                        .executes(context -> protectionCheckAtPlayer(context, registry))
                                        .then(Commands.literal("at")
                                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                                        .executes(context -> protectionCheckAtCoordinates(context, registry)))))))))
                .then(Commands.literal("permissions")
                        .then(Commands.literal("check")
                                .then(Commands.argument("node", StringArgumentType.word())
                                        .executes(BlockForgeCommands::permissionCheck))))
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
        return Commands.argument("id", BlueprintIdArgumentType.id())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(registry.getIds(), builder));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> rotationArgument() {
        return Commands.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("0", "90", "180", "270"), builder));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> packIdArgument(
            BlueprintRegistry registry
    ) {
        return Commands.argument("packId", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        registry.getPacks().stream().map(pack -> pack.manifest().packId()).toList(),
                        builder
                ));
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
                () -> Component.literal("Loaded "
                        + summary.loadedCount()
                        + " BlockForge blueprint(s) from loose files and "
                        + summary.packCount()
                        + " pack(s)."),
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

    private static int packsFolder(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge pack folder: " + registry.getPackDirectory()),
                false
        );
        return 1;
    }

    private static int packsList(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        if (registry.getPacks().isEmpty()) {
            context.getSource().sendFailure(Component.literal("No BlockForge packs loaded from " + registry.getPackDirectory()));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Loaded BlockForge packs:"), false);
        for (LoadedBlueprintPack pack : registry.getPacks()) {
            context.getSource().sendSuccess(
                    () -> Component.literal("- "
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
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        LoadedBlueprintPack pack = findPack(context, registry);
        if (pack == null) {
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal(pack.manifest().packId()
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
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        LoadedBlueprintPack pack = findPack(context, registry);
        if (pack == null) {
            return 0;
        }

        for (BlueprintPackRegistryEntry entry : pack.entries()) {
            context.getSource().sendSuccess(
                    () -> Component.literal("- " + entry.registryId() + " | " + entry.name() + " | " + entry.path()),
                    false
            );
        }
        return pack.entries().size();
    }

    private static int packsValidate(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        var result = registry.validatePacks();
        context.getSource().sendSuccess(
                () -> Component.literal("Validated BlockForge packs: packs="
                        + result.packs().size()
                        + " | blueprints="
                        + result.blueprints().size()
                        + " | warnings="
                        + result.warnings().size()),
                false
        );
        for (String warning : result.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }
        return result.packs().size();
    }

    private static int schematicsFolder(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge schematic folder: " + registry.getSchematicDirectory()),
                false
        );
        return 1;
    }

    private static int schematicsList(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        var schematics = registry.getSchematics();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded BlockForge schematics: " + schematics.size()),
                false
        );
        schematics.stream().limit(20).forEach(result -> context.getSource().sendSuccess(
                () -> Component.literal("- " + result.blueprint().getId()
                        + " | size=" + result.blueprint().getSize().format()
                        + " | palette=" + result.blueprint().getPalette().size()
                        + " | warnings=" + result.warnings().size()),
                false
        ));
        return schematics.size();
    }

    private static int schematicsInfo(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null || !blueprint.getId().startsWith("schem/")) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge schematic id."));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge schematic: " + describe(blueprint)
                        + " | palette=" + blueprint.getPalette().size()),
                false
        );
        return 1;
    }

    private static int schematicsValidate(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        var result = registry.validateSchematics();
        context.getSource().sendSuccess(
                () -> Component.literal("Validated BlockForge schematics: loaded="
                        + result.schematics().size()
                        + ", warnings="
                        + result.warnings().size()),
                false
        );
        result.warnings().forEach(warning -> context.getSource().sendFailure(Component.literal("Warning: " + warning)));
        return result.schematics().size();
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

        UndoManager.UndoResult result = BlockForgeConnector.UNDO.restore(context.getSource().getLevel(), player, snapshot);
        context.getSource().sendSuccess(
                () -> Component.literal(formatUndoResult(result)),
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
                            + " | consumedItems="
                            + snapshot.consumedItemCount()
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

    private static int showWandMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BuilderWandState state = BlockForgeConnector.WAND_STATES.getOrCreate(player.getUUID());
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand mode: " + state.mode().id()),
                false
        );
        return 1;
    }

    private static int setWandMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        String value = StringArgumentType.getString(context, "mode");
        BuilderWandMode mode;
        try {
            mode = BuilderWandMode.parse(value);
        } catch (IllegalArgumentException error) {
            context.getSource().sendFailure(Component.literal("Unknown Builder Wand mode: " + value));
            return 0;
        }

        BuilderWandState state = BlockForgeConnector.WAND_STATES.setMode(player.getUUID(), mode, player.level().getGameTime());
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand mode set to " + state.mode().id() + "."),
                true
        );
        return 1;
    }

    private static int cycleWandMode(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BuilderWandState state = BlockForgeConnector.WAND_STATES.cycle(player.getUUID(), player.level().getGameTime());
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand mode: " + state.mode().id()),
                true
        );
        return 1;
    }

    private static int showWandOptions(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BuilderWandState state = BlockForgeConnector.WAND_STATES.getOrCreate(player.getUUID());
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand options: mode="
                        + state.mode().id()
                        + ", mirrorX=" + state.mirroredX()
                        + ", mirrorZ=" + state.mirroredZ()
                        + ", offset=" + state.offsetX() + "," + state.offsetY() + "," + state.offsetZ()
                        + ", anchor=" + (state.anchorId() == null || state.anchorId().isBlank() ? "none" : state.anchorId())
                        + ", replace=air_only"),
                false
        );
        return 1;
    }

    private static int setWandOffset(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        int x = IntegerArgumentType.getInteger(context, "x");
        int y = IntegerArgumentType.getInteger(context, "y");
        int z = IntegerArgumentType.getInteger(context, "z");
        BuilderWandState state = BlockForgeConnector.WAND_STATES.update(
                player.getUUID(),
                current -> current.withOffset(x, y, z, player.level().getGameTime())
        );
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand offset set to "
                        + state.offsetX() + "," + state.offsetY() + "," + state.offsetZ() + "."),
                true
        );
        return 1;
    }

    private static int setWandMirror(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        String axis = StringArgumentType.getString(context, "axis").toLowerCase(java.util.Locale.ROOT);
        boolean mirrorX = "x".equals(axis);
        boolean mirrorZ = "z".equals(axis);
        if (!mirrorX && !mirrorZ && !"none".equals(axis)) {
            context.getSource().sendFailure(Component.literal("Mirror axis must be x, z, or none."));
            return 0;
        }

        BuilderWandState state = BlockForgeConnector.WAND_STATES.update(
                player.getUUID(),
                current -> current.withMirror(mirrorX, mirrorZ, player.level().getGameTime())
        );
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand mirror set: mirrorX="
                        + state.mirroredX()
                        + ", mirrorZ="
                        + state.mirroredZ()
                        + "."),
                true
        );
        return 1;
    }

    private static int setWandReplaceMode(CommandContext<CommandSourceStack> context) {
        String mode = StringArgumentType.getString(context, "mode");
        if (!"air_only".equals(mode) && !"allow_replace".equals(mode)) {
            context.getSource().sendFailure(Component.literal("Replace mode must be air_only or allow_replace."));
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand replace mode is tracked as alpha placement metadata: " + mode + "."),
                true
        );
        return 1;
    }

    private static int clearWandAnchor(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        BlockForgeConnector.WAND_STATES.update(
                player.getUUID(),
                current -> current.withAnchor("", player.level().getGameTime())
        );
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Wand anchor cleared."),
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

    private static int buildPlanCreateAtPlayer(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        BlockPos pos = player.blockPosition();
        return buildPlanCreate(context, registry, pos.getX(), pos.getY(), pos.getZ());
    }

    private static int buildPlanCreateAtCoordinates(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        return buildPlanCreate(
                context,
                registry,
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "z")
        );
    }

    private static int buildPlanCreate(CommandContext<CommandSourceStack> context, BlueprintRegistry registry, int x, int y, int z) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        Blueprint blueprint = registry.get(StringArgumentType.getString(context, "id")).orElse(null);
        if (blueprint == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge blueprint id."));
            return 0;
        }
        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        BuilderWandState wand = BlockForgeConnector.WAND_STATES.getOrCreate(player.getUUID());
        BuildPlan plan = BuildPlanFactory.create(
                toCommonBlueprint(blueprint),
                player.getUUID(),
                player.level().dimension().location().toString(),
                x,
                y,
                z,
                selection.getRotation().degrees(),
                wand.mirroredX(),
                wand.mirroredZ(),
                wand.offsetX(),
                wand.offsetY(),
                wand.offsetZ(),
                BuildPlanOptions.defaults(),
                player.level().getGameTime()
        );
        BlockForgeConnector.BUILD_PLANS.delegate().save(player.getUUID(), plan);
        context.getSource().sendSuccess(
                () -> Component.literal("Created BuildPlan "
                        + plan.planId()
                        + " blocks="
                        + plan.totalBlocks()
                        + " layers="
                        + plan.totalLayers()
                        + " status="
                        + plan.status()),
                true
        );
        return plan.totalBlocks();
    }

    private static int buildPlanPreview(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        BuildPlan plan = BlockForgeConnector.BUILD_PLANS.delegate().get(player.getUUID()).orElse(null);
        if (plan == null) {
            context.getSource().sendFailure(Component.literal("No active BlockForge BuildPlan."));
            return 0;
        }
        int issues = BuildPlanValidator.validate(plan, player.level().getMinBuildHeight(), player.level().getMaxBuildHeight()).size();
        context.getSource().sendSuccess(
                () -> Component.literal("BuildPlan preview: blueprint="
                        + plan.blueprintId()
                        + ", base="
                        + plan.baseX()
                        + ","
                        + plan.baseY()
                        + ","
                        + plan.baseZ()
                        + ", blocks="
                        + plan.totalBlocks()
                        + ", layers="
                        + plan.totalLayers()
                        + ", issues="
                        + issues
                        + ". Collision/material/protection preview is command-alpha and does not place or consume."),
                false
        );
        return issues;
    }

    private static int buildPlanStatus(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        BuildPlan plan = BlockForgeConnector.BUILD_PLANS.delegate().get(player.getUUID()).orElse(null);
        if (plan == null) {
            context.getSource().sendFailure(Component.literal("No active BlockForge BuildPlan."));
            return 0;
        }
        BuildProgress progress = BuildProgress.fromPlan(plan);
        context.getSource().sendSuccess(
                () -> Component.literal("BuildPlan status: "
                        + progress.status()
                        + " placed="
                        + progress.placedBlocks()
                        + " skipped="
                        + progress.skippedBlocks()
                        + " failed="
                        + progress.failedBlocks()
                        + " total="
                        + progress.totalBlocks()
                        + " percent="
                        + String.format("%.1f", progress.percent())
                        + "%"),
                false
        );
        return progress.placedBlocks();
    }

    private static int buildPlanSetStatus(CommandContext<CommandSourceStack> context, BuildPlanStatus status) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        var updated = BlockForgeConnector.BUILD_PLANS.delegate().setStatus(player.getUUID(), status);
        if (updated.isEmpty()) {
            context.getSource().sendFailure(Component.literal("No active BlockForge BuildPlan."));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("BuildPlan status set to " + status + "."), true);
        return 1;
    }

    private static int buildPlanStep(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        BuildPlan plan = BlockForgeConnector.BUILD_PLANS.delegate().get(player.getUUID()).orElse(null);
        if (plan == null) {
            context.getSource().sendFailure(Component.literal("No active BlockForge BuildPlan."));
            return 0;
        }
        BuildPlan next = BuildPlanStepper.markNextBatch(plan, BuildPlanOptions.defaults().maxBlocksPerStep(), BuildStepStatus.SKIPPED);
        BlockForgeConnector.BUILD_PLANS.delegate().save(player.getUUID(), next.status() == BuildPlanStatus.COMPLETED ? next.withStatus(BuildPlanStatus.COMPLETED) : next.withStatus(BuildPlanStatus.PAUSED));
        context.getSource().sendSuccess(
                () -> Component.literal("BuildPlan step simulated a safe command-alpha batch. Real per-step placement remains pending; use direct build or wand BUILD for actual placement."),
                true
        );
        return BuildPlanOptions.defaults().maxBlocksPerStep();
    }

    private static com.blockforge.common.blueprint.Blueprint toCommonBlueprint(Blueprint blueprint) {
        java.util.Map<String, com.blockforge.common.blueprint.BlueprintPaletteEntry> palette = blueprint.getPalette()
                .entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        java.util.Map.Entry::getKey,
                        entry -> new com.blockforge.common.blueprint.BlueprintPaletteEntry(
                                entry.getValue().name(),
                                entry.getValue().properties()
                        )
                ));
        java.util.List<com.blockforge.common.blueprint.BlueprintBlock> blocks = blueprint.getBlocks()
                .stream()
                .map(block -> new com.blockforge.common.blueprint.BlueprintBlock(
                        block.getX(),
                        block.getY(),
                        block.getZ(),
                        block.getState()
                ))
                .toList();
        return new com.blockforge.common.blueprint.Blueprint(
                blueprint.getSchemaVersion(),
                blueprint.getId(),
                blueprint.getName(),
                blueprint.getDescription(),
                blueprint.getMinecraftVersion(),
                blueprint.getGenerator(),
                new com.blockforge.common.blueprint.BlueprintSize(
                        blueprint.getSize().width(),
                        blueprint.getSize().height(),
                        blueprint.getSize().depth()
                ),
                palette,
                blocks
        );
    }

    private static int buildPlanRepair(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BuildPlan repair alpha is available in common pure logic. World diff repair is pending loader integration."),
                false
        );
        return 1;
    }

    private static int buildPlanClear(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        boolean cleared = BlockForgeConnector.BUILD_PLANS.delegate().clear(player.getUUID()).isPresent();
        context.getSource().sendSuccess(
                () -> Component.literal(cleared ? "Cleared current BuildPlan." : "No non-running BuildPlan was cleared."),
                true
        );
        return cleared ? 1 : 0;
    }

    private static int protectionFolder(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge protection file: " + BlockForgeConnector.PROTECTION.file()),
                false
        );
        return 1;
    }

    private static int protectionReload(CommandContext<CommandSourceStack> context) {
        var config = BlockForgeConnector.PROTECTION.reload();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded " + config.regions().size() + " BlockForge protection region(s)."),
                true
        );
        for (String warning : config.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }
        return config.regions().size();
    }

    private static int protectionList(CommandContext<CommandSourceStack> context) {
        if (BlockForgeConnector.PROTECTION.regions().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("No BlockForge protection regions loaded."), false);
            return 0;
        }
        for (BlockForgeProtectionRegion region : BlockForgeConnector.PROTECTION.regions()) {
            context.getSource().sendSuccess(() -> Component.literal("- " + describeRegion(region)), false);
        }
        return BlockForgeConnector.PROTECTION.regions().size();
    }

    private static int protectionInfo(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "region");
        BlockForgeProtectionRegion region = BlockForgeConnector.PROTECTION.find(id).orElse(null);
        if (region == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge protection region: " + id));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal(describeRegion(region)
                        + " | allowedPlayers=" + region.allowedPlayers().size()
                        + " | allowedPermissions=" + region.allowedPermissions()),
                false
        );
        return 1;
    }

    private static int protectionCheckAtPlayer(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        BlueprintRotation rotation = selection.hasSelection() ? selection.getRotation() : BlueprintRotation.NONE;
        return protectionCheck(context, registry, player, player.blockPosition(), rotation);
    }

    private static int protectionCheckAtCoordinates(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        PlayerBlueprintSelection selection = BlockForgeConnector.SELECTIONS.getOrCreate(player.getUUID());
        BlueprintRotation rotation = selection.hasSelection() ? selection.getRotation() : BlueprintRotation.NONE;
        BlockPos basePos = new BlockPos(
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "z")
        );
        return protectionCheck(context, registry, player, basePos, rotation);
    }

    private static int protectionCheck(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry,
            ServerPlayer player,
            BlockPos basePos,
            BlueprintRotation rotation
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }
        ProtectionPreflightReport report = BlockForgeConnector.PROTECTION.preflight(
                player,
                context.getSource().getLevel(),
                basePos,
                blueprint,
                rotation,
                BlockForgePermissionAction.BUILD_WAND
        );
        if (!report.allowed()) {
            sendSecurityDenied(context.getSource(), report);
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge protection check allowed for "
                        + blueprint.getId()
                        + " | checkedBlocks="
                        + report.checkedBlocks()),
                false
        );
        return 1;
    }

    private static int permissionCheck(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        String node = StringArgumentType.getString(context, "node");
        var result = BlockForgeConnector.PROTECTION.permissions().check(player, node, 2);
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge permission " + node + ": " + (result.allowed() ? "allowed" : "denied")),
                false
        );
        if (!result.allowed()) {
            context.getSource().sendFailure(Component.literal(result.reason()));
        }
        return result.allowed() ? 1 : 0;
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
        sendMaterialReport(context.getSource(), MATERIALS.report(blueprint, getPlayerOrNull(context)));
        return result.placedBlocks();
    }

    private static int materials(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, getPlayerOrNull(context));
        sendMaterialReport(context.getSource(), report);
        ServerPlayer player = getPlayerOrNull(context);
        if (player != null && BlockForgeConfig.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), materialSourceReport(blueprint, player, report));
        }
        return report.totalRequiredItems();
    }

    private static int materialsSelected(
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
        if (blueprint == null) {
            context.getSource().sendFailure(Component.literal("Selected BlockForge blueprint is not loaded: " + selection.getSelectedBlueprintId()));
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        sendMaterialReport(context.getSource(), report);
        if (BlockForgeConfig.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), materialSourceReport(blueprint, player, report));
        }
        return report.totalRequiredItems();
    }

    private static int sourcesScan(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        NeoForgeMaterialSourceScanner.Scan scan = SOURCE_SCANNER.scan(
                player,
                context.getSource().getLevel(),
                player.blockPosition(),
                BlockForgeConfig.materialSourceConfig()
        );
        MaterialSourceScanResult result = scan.result();
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge material sources scan: enabled="
                        + BlockForgeConfig.enableNearbyContainers()
                        + " | radius="
                        + BlockForgeConfig.nearbyContainerSearchRadius()
                        + " | foundContainers="
                        + result.foundContainers()
                        + " | scannedBlocks="
                        + result.scannedBlocks()),
                false
        );

        result.sources()
                .stream()
                .limit(12)
                .forEach(source -> context.getSource().sendSuccess(
                        () -> Component.literal("- "
                                + source.displayName()
                                + " @ "
                                + source.x()
                                + ","
                                + source.y()
                                + ","
                                + source.z()),
                        false
                ));
        for (String warning : result.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }
        return result.foundContainers();
    }

    private static int sourcesSelected(
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
        if (blueprint == null) {
            context.getSource().sendFailure(Component.literal("Selected BlockForge blueprint is not loaded: " + selection.getSelectedBlueprintId()));
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        MaterialSourceReport sourceReport = materialSourceReport(blueprint, player, report);
        sendMaterialSourceReport(context.getSource(), sourceReport);
        return sourceReport.totalRequiredItems();
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

        ServerPlayer player = getPlayerOrNull(context);
        BuildService.BuildResult buildResult = BUILDS.build(
                context.getSource().getLevel(),
                player,
                basePos,
                blueprint,
                rotation
        );

        if (!buildResult.allowed()) {
            if (buildResult.placementResult() != null) {
                sendPlacementResult(context.getSource(), "Build rejected", buildResult);
            } else {
                context.getSource().sendFailure(Component.literal("BlockForge build rejected: " + buildResult.message()));
            }

        if (buildResult.materialReport() != null) {
            sendMaterialReport(context.getSource(), buildResult.materialReport());
        }
        if (buildResult.materialSourceReport() != null) {
            sendMaterialSourceReport(context.getSource(), buildResult.materialSourceReport());
        }
        return 0;
    }

        sendPlacementResult(context.getSource(), "Build complete", buildResult);
        return buildResult.placementResult().placedBlocks();
    }

    private static MaterialSourceReport materialSourceReport(
            Blueprint blueprint,
            ServerPlayer player,
            MaterialReport report
    ) {
        NeoForgeMaterialSourceScanner.Scan scan = SOURCE_SCANNER.scan(
                player,
                player.serverLevel(),
                player.blockPosition(),
                BlockForgeConfig.materialSourceConfig()
        );
        return SOURCE_ADAPTER.report(
                report,
                player,
                scan.containers(),
                BlockForgeConfig.materialSourceConfig()
        );
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

    private static LoadedBlueprintPack findPack(
            CommandContext<CommandSourceStack> context,
            BlueprintRegistry registry
    ) {
        String packId = StringArgumentType.getString(context, "packId");
        return registry.getPacks()
                .stream()
                .filter(pack -> pack.manifest().packId().equals(packId))
                .findFirst()
                .orElseGet(() -> {
                    context.getSource().sendFailure(Component.literal("Unknown BlockForge pack id: " + packId));
                    return null;
                });
    }

    private static void sendPlacementResult(
            CommandSourceStack source,
            String label,
            BuildService.BuildResult buildResult
    ) {
        BlueprintPlacer.PlacementResult result = buildResult.placementResult();
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
                        + materialSummary(buildResult)
                        + undoHint(buildResult)),
                true
        );
    }

    private static void sendSecurityDenied(CommandSourceStack source, ProtectionPreflightReport report) {
        source.sendFailure(Component.literal(report.reason().isBlank()
                ? "BlockForge build denied by security preflight."
                : report.reason()));
        for (String warning : report.warnings()) {
            source.sendFailure(Component.literal("Warning: " + warning));
        }
    }

    private static String describeRegion(BlockForgeProtectionRegion region) {
        return region.id()
                + " | "
                + region.mode()
                + " | "
                + region.dimensionId()
                + " | ["
                + region.minX()
                + ","
                + region.minY()
                + ","
                + region.minZ()
                + "] -> ["
                + region.maxX()
                + ","
                + region.maxY()
                + ","
                + region.maxZ()
                + "]";
    }

    private static String materialSummary(BuildService.BuildResult buildResult) {
        if (buildResult == null || buildResult.materialReport() == null) {
            return "";
        }

        if (buildResult.creativeBypass()) {
            return ". Creative mode: no materials consumed";
        }

        if (buildResult.consumedFromNearbyContainers() > 0) {
            return ". consumedItems="
                    + buildResult.consumedItems()
                    + " (player="
                    + buildResult.consumedFromPlayerInventory()
                    + ", nearbyContainers="
                    + buildResult.consumedFromNearbyContainers()
                    + ")";
        }

        return ". consumedItems=" + buildResult.consumedItems();
    }

    private static String undoHint(BuildService.BuildResult buildResult) {
        if (buildResult != null && buildResult.consumedItems() > 0) {
            return ". Use /blockforge undo to restore blocks and refund materials.";
        }

        return ". Use /blockforge undo to revert blocks.";
    }

    private static String formatUndoResult(UndoManager.UndoResult result) {
        MaterialRefundResult refund = result.refundResult();
        if (result.consumedItems() <= 0) {
            return "BlockForge undo restored "
                    + result.restoredBlocks()
                    + " blocks from blueprint "
                    + result.blueprintId()
                    + ". No materials were consumed.";
        }

        if (refund.droppedItems() > 0) {
            return "BlockForge undo restored "
                    + result.restoredBlocks()
                    + " blocks and refunded "
                    + refund.refundedItems()
                    + " items (containers="
                    + refund.refundedToContainers()
                    + ", player="
                    + refund.refundedToPlayer()
                    + "), dropped "
                    + refund.droppedItems()
                    + " items near player.";
        }

        return "BlockForge undo restored "
                + result.restoredBlocks()
                + " blocks and refunded "
                + refund.refundedItems()
                + " items (containers="
                + refund.refundedToContainers()
                + ", player="
                + refund.refundedToPlayer()
                + ") from blueprint "
                + result.blueprintId()
                + ".";
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

    private static void sendMaterialReport(CommandSourceStack source, MaterialReport report) {
        source.sendSuccess(
                () -> Component.literal("BlockForge materials: "
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

        int shown = 0;
        for (MaterialRequirement requirement : report.requirements()) {
            if (shown >= 8) {
                source.sendSuccess(() -> Component.literal("... more material entries omitted."), false);
                break;
            }

            source.sendSuccess(
                    () -> Component.literal("- "
                            + requirement.itemId()
                            + " required="
                            + requirement.required()
                            + ", available="
                            + requirement.available()
                            + ", missing="
                            + requirement.missing()),
                    false
            );
            shown++;
        }
    }

    private static void sendMaterialSourceReport(CommandSourceStack source, MaterialSourceReport report) {
        source.sendSuccess(
                () -> Component.literal("BlockForge material sources: "
                        + report.blueprintId()
                        + " | enabled="
                        + BlockForgeConfig.enableNearbyContainers()
                        + " | priority="
                        + BlockForgeConfig.materialSourcePriority()
                        + " | radius="
                        + BlockForgeConfig.nearbyContainerSearchRadius()
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
                source.sendSuccess(() -> Component.literal("... more material source entries omitted."), false);
                break;
            }

            if (entry.reserved() <= 0 && entry.available() <= 0) {
                continue;
            }

            source.sendSuccess(
                    () -> Component.literal("- "
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

        for (String warning : report.warnings()) {
            source.sendFailure(Component.literal("Warning: " + warning));
        }
    }

    private static int reservedFrom(MaterialSourceReport report, MaterialSourceType sourceType) {
        return report.entries()
                .stream()
                .filter(entry -> entry.source() != null && entry.source().type() == sourceType)
                .mapToInt(MaterialSourceItemEntry::reserved)
                .sum();
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

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
import com.blockforge.common.house.HouseBlueprintCompiler;
import com.blockforge.common.house.HouseGenerationRequest;
import com.blockforge.common.house.HouseMaterialEstimator;
import com.blockforge.common.house.HousePlan;
import com.blockforge.common.house.HousePlanGenerator;
import com.blockforge.common.house.HouseQualityAnalyzer;
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
    private static final HousePlanGenerator HOUSES = new HousePlanGenerator();

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
                .then(Commands.literal("house")
                        .then(Commands.literal("presets")
                                .executes(BlockForgeCommands::housePresets))
                        .then(Commands.literal("create")
                                .then(Commands.argument("preset", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                                HOUSES.supportedPresets().stream().map(style -> style.name().toLowerCase(java.util.Locale.ROOT)),
                                                builder
                                        ))
                                        .executes(BlockForgeCommands::houseCreate)
                                        .then(Commands.literal("size")
                                                .then(Commands.argument("width", IntegerArgumentType.integer(5, 32))
                                                        .then(Commands.argument("depth", IntegerArgumentType.integer(5, 32))
                                                                .then(Commands.literal("floors")
                                                                        .then(Commands.argument("floors", IntegerArgumentType.integer(1, 4))
                                                                                .executes(BlockForgeCommands::houseCreateSized))))))))
                        .then(Commands.literal("roof")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("flat", "gable", "hip", "pyramid", "tower", "shed", "none"), builder))
                                        .executes(BlockForgeCommands::houseRoof)))
                        .then(Commands.literal("materials")
                                .then(Commands.argument("wall", StringArgumentType.word())
                                        .then(Commands.argument("roof", StringArgumentType.word())
                                                .then(Commands.argument("floor", StringArgumentType.word())
                                                        .executes(BlockForgeCommands::houseMaterials)))))
                        .then(Commands.literal("preview")
                                .executes(BlockForgeCommands::housePreview))
                        .then(Commands.literal("buildplan")
                                .executes(BlockForgeCommands::houseBuildPlan))
                        .then(Commands.literal("build")
                                .executes(BlockForgeCommands::houseBuild))
                        .then(Commands.literal("quality")
                                .executes(BlockForgeCommands::houseQuality))
                        .then(Commands.literal("export")
                                .then(Commands.literal("blueprint")
                                        .executes(BlockForgeCommands::houseExportBlueprint)))
                        .then(Commands.literal("save")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(BlockForgeCommands::houseSave))))
                .then(Commands.literal("station")
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::stationList))
                        .then(Commands.literal("info")
                                .executes(BlockForgeCommands::stationInfo)
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(BlockForgeCommands::stationInfo)))
                        .then(Commands.literal("bind")
                                .then(Commands.literal("blueprint")
                                        .then(blueprintIdArgument(registry)
                                                .executes(context -> stationBindBlueprint(context, registry))))
                                .then(Commands.literal("anchor")
                                        .then(Commands.literal("nearest")
                                                .executes(BlockForgeCommands::stationBindAnchorNearest)))
                                .then(Commands.literal("cache")
                                        .then(Commands.literal("nearest")
                                                .executes(BlockForgeCommands::stationBindCacheNearest))))
                        .then(Commands.literal("createplan")
                                .executes(BlockForgeCommands::stationCreatePlan))
                        .then(Commands.literal("start")
                                .executes(BlockForgeCommands::stationStart))
                        .then(Commands.literal("pause")
                                .executes(BlockForgeCommands::stationPause))
                        .then(Commands.literal("resume")
                                .executes(BlockForgeCommands::stationResume))
                        .then(Commands.literal("cancel")
                                .executes(BlockForgeCommands::stationCancel))
                        .then(Commands.literal("step")
                                .executes(BlockForgeCommands::stationStep))
                        .then(Commands.literal("status")
                                .executes(BlockForgeCommands::stationStatus))
                        .then(Commands.literal("clear")
                                .executes(BlockForgeCommands::stationClear)))
                .then(Commands.literal("settlement")
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(BlockForgeCommands::settlementCreate)))
                        .then(Commands.literal("info")
                                .executes(BlockForgeCommands::settlementInfo))
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::settlementList))
                        .then(Commands.literal("members")
                                .executes(BlockForgeCommands::settlementMembers))
                        .then(Commands.literal("invite")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(BlockForgeCommands::settlementInvite)))
                        .then(Commands.literal("leave")
                                .executes(BlockForgeCommands::settlementLeave))
                        .then(Commands.literal("level")
                                .executes(BlockForgeCommands::settlementLevel))
                        .then(Commands.literal("contracts")
                                .executes(BlockForgeCommands::settlementContracts))
                        .then(Commands.literal("abandon")
                                .executes(BlockForgeCommands::settlementAbandon)))
                .then(Commands.literal("contracts")
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::contractsList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::contractsInfo)))
                        .then(Commands.literal("accept")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::contractsAccept)))
                        .then(Commands.literal("active")
                                .executes(BlockForgeCommands::contractsActive))
                        .then(Commands.literal("verify")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(context -> contractsVerify(context, registry))))
                        .then(Commands.literal("submit")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::contractsSubmit)))
                        .then(Commands.literal("abandon")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::contractsAbandon)))
                        .then(Commands.literal("refresh")
                                .executes(BlockForgeCommands::contractsRefresh)))
                .then(Commands.literal("rewards")
                        .then(Commands.literal("claim")
                                .executes(BlockForgeCommands::rewardsClaim))
                        .then(Commands.literal("preview")
                                .executes(BlockForgeCommands::rewardsPreview)))
                .then(Commands.literal("architect")
                        .then(Commands.literal("profile")
                                .executes(BlockForgeCommands::architectProfile))
                        .then(Commands.literal("contracts")
                                .executes(BlockForgeCommands::architectContracts))
                        .then(Commands.literal("reputation")
                                .executes(BlockForgeCommands::architectReputation)))
                .then(Commands.literal("events")
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::eventsList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::eventsInfo)))
                        .then(Commands.literal("refresh")
                                .executes(BlockForgeCommands::eventsRefresh))
                        .then(Commands.literal("resolve")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::eventsResolve)))
                        .then(Commands.literal("ignore")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::eventsIgnore))))
                .then(Commands.literal("projects")
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::projectsList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("projectId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::projectsInfo)))
                        .then(Commands.literal("activate")
                                .then(Commands.argument("projectId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::projectsActivate)))
                        .then(Commands.literal("status")
                                .executes(BlockForgeCommands::projectsStatus))
                        .then(Commands.literal("complete")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("projectId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::projectsComplete))))
                .then(Commands.literal("emergency")
                        .then(Commands.literal("list")
                                .executes(BlockForgeCommands::emergencyList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::emergencyInfo)))
                        .then(Commands.literal("repair")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::emergencyRepair)))
                        .then(Commands.literal("verify")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(BlockForgeCommands::emergencyVerify))))
                .then(Commands.literal("admin")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("audit")
                                .executes(BlockForgeCommands::adminAudit)
                                .then(Commands.literal("export")
                                        .executes(BlockForgeCommands::adminAuditExport)))
                        .then(Commands.literal("builds")
                                .executes(BlockForgeCommands::adminBuilds)))
                .then(Commands.literal("quota")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("get")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(BlockForgeCommands::quotaGet)))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(BlockForgeCommands::quotaReset))))
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

    private static int stationList(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Builder Station alpha: placed stations are discoverable in-world; persistent station registry is planned."),
                false
        );
        return 1;
    }

    private static int stationInfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station: command-driven alpha. Supports bind blueprint/anchor/cache, createplan, step, pause, resume, cancel. Tick automation remains partial."),
                false
        );
        return 1;
    }

    private static int stationBindBlueprint(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        String blueprintId = StringArgumentType.getString(context, "id");
        Blueprint blueprint = registry.get(blueprintId).orElse(null);
        if (blueprint == null) {
            context.getSource().sendFailure(Component.literal("Unknown blueprint: " + blueprintId));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station alpha binding accepted for blueprint " + describe(blueprint) + ". Persistent station storage remains planned."),
                false
        );
        return 1;
    }

    private static int stationBindAnchorNearest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station alpha anchor binding: nearest-anchor scan is planned; use Builder Wand anchor state for current command-driven plans."),
                false
        );
        return 1;
    }

    private static int stationBindCacheNearest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station alpha cache binding: Material Link and Material Cache scanning are scaffolded; inventory-backed cache sourcing remains partial."),
                false
        );
        return 1;
    }

    private static int stationCreatePlan(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station createplan alpha uses the existing /blockforge buildplan create flow. Station-owned persistent jobs are planned."),
                false
        );
        return 1;
    }

    private static int stationStart(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station status set to RUNNING for command-alpha feedback. Real station tick placement remains pending."),
                true
        );
        return 1;
    }

    private static int stationPause(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Builder Station paused."), true);
        return 1;
    }

    private static int stationResume(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Builder Station resumed."), true);
        return 1;
    }

    private static int stationCancel(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Builder Station job cancelled. Use /blockforge undo for rollback when snapshots exist."), true);
        return 1;
    }

    private static int stationStep(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station step executed as a safe command-alpha scaffold. It does not place blocks yet; BuildPlan direct placement remains the guarded path."),
                true
        );
        return 1;
    }

    private static int stationStatus(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Builder Station status: IDLE/READY/RUNNING state model is available in common. Loader-persistent station jobs are partial."),
                false
        );
        return 1;
    }

    private static int stationClear(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Builder Station alpha job cleared."), true);
        return 1;
    }

    private static int settlementCreate(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Settlement alpha created/scaffolded: " + name + ". NeoForge persistence reference is planned after v5.0 common DTO validation."),
                true
        );
        return 1;
    }

    private static int settlementInfo(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement alpha: level=1, reputation=0, activeContracts=0. Place a Settlement Core and use /blockforge contracts list."),
                false
        );
        return 1;
    }

    private static int settlementList(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement list alpha: loader-persistent settlement registry is partial in v5.0."),
                false
        );
        return 1;
    }

    private static int settlementMembers(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement members alpha: owner/member permission DTOs are available; invite persistence is partial."),
                false
        );
        return 1;
    }

    private static int settlementInvite(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement invite alpha recorded for " + playerName + ". Multiplayer invite acceptance remains planned."),
                true
        );
        return 1;
    }

    private static int settlementLeave(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.literal("Settlement leave alpha: membership store is partial."), true);
        return 1;
    }

    private static int settlementLevel(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement level alpha: reputation thresholds unlock contracts, station access, and larger build limits."),
                false
        );
        return 1;
    }

    private static int settlementContracts(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement contracts alpha: use /blockforge contracts list and /blockforge contracts accept <contractId>."),
                false
        );
        return 1;
    }

    private static int settlementAbandon(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement abandon alpha: command is registered, destructive deletion requires persistent settlement storage."),
                true
        );
        return 1;
    }

    private static int contractsList(CommandContext<CommandSourceStack> context) {
        var templates = com.blockforge.common.contracts.ContractTemplates.templates();
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge contracts alpha: " + templates.size() + " templates available. Examples: "
                        + templates.get(0).contractId() + ", "
                        + templates.get(1).contractId() + ", "
                        + templates.get(2).contractId()),
                false
        );
        return 1;
    }

    private static int contractsInfo(CommandContext<CommandSourceStack> context) {
        String contractId = StringArgumentType.getString(context, "contractId");
        var contract = com.blockforge.common.contracts.ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals(contractId))
                .findFirst()
                .orElse(null);
        if (contract == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge contract template: " + contractId));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal(contract.contractId()
                        + " | "
                        + contract.title()
                        + " | difficulty="
                        + contract.difficulty()
                        + " | blocks="
                        + contract.requirements().minBlocks()
                        + "-"
                        + contract.requirements().maxBlocks()
                        + " | reputation="
                        + contract.rewards().reputation()),
                false
        );
        return 1;
    }

    private static int contractsAccept(CommandContext<CommandSourceStack> context) {
        String contractId = StringArgumentType.getString(context, "contractId");
        context.getSource().sendSuccess(
                () -> Component.literal("Contract accepted alpha: " + contractId + ". Server-side accepted-contract persistence is partial in v5.0."),
                true
        );
        return 1;
    }

    private static int contractsActive(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Active contracts alpha: use Architect Ledger or /blockforge contracts accept after persistence lands."),
                false
        );
        return 1;
    }

    private static int contractsVerify(CommandContext<CommandSourceStack> context, BlueprintRegistry registry) {
        String contractId = StringArgumentType.getString(context, "contractId");
        context.getSource().sendSuccess(
                () -> Component.literal("Contract verification alpha for " + contractId
                        + ": common heuristic verifier is implemented for blueprint-level checks. World snapshot verification remains loader-dependent."),
                false
        );
        return 1;
    }

    private static int contractsSubmit(CommandContext<CommandSourceStack> context) {
        String contractId = StringArgumentType.getString(context, "contractId");
        context.getSource().sendSuccess(
                () -> Component.literal("Contract submit alpha: " + contractId + ". Passed verification will award reputation/experience when persistent profiles are enabled."),
                true
        );
        return 1;
    }

    private static int contractsAbandon(CommandContext<CommandSourceStack> context) {
        String contractId = StringArgumentType.getString(context, "contractId");
        context.getSource().sendSuccess(() -> Component.literal("Contract abandoned alpha: " + contractId), true);
        return 1;
    }

    private static int contractsRefresh(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Contract refresh alpha: templates rotate by settlement level/game time; daily persistence is partial."),
                true
        );
        return 1;
    }

    private static int rewardsClaim(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Rewards claim alpha: reputation/experience rewards are implemented in common; item payout persistence is partial."),
                true
        );
        return 1;
    }

    private static int rewardsPreview(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Rewards preview alpha: contracts award reputation, experience, unlock ids, simple item reward records, and optional blueprint pack ids."),
                false
        );
        return 1;
    }

    private static int architectProfile(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Architect profile alpha: level=1, reputation=0, experience=0 until persistent profile storage is enabled."),
                false
        );
        return 1;
    }

    private static int architectContracts(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Architect contracts alpha: active/completed history is represented in common and persistence is partial."),
                false
        );
        return 1;
    }

    private static int architectReputation(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Architect reputation alpha: 100/250/500/1000 reputation unlock higher levels and contract tiers."),
                false
        );
        return 1;
    }

    private static int eventsList(CommandContext<CommandSourceStack> context) {
        var events = com.blockforge.common.settlement.event.SettlementEventTemplates.templates("settlement-alpha", 0);
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement events alpha: " + events.size() + " templates available. Examples: "
                        + events.get(0).eventId() + ", "
                        + events.get(1).eventId() + ", "
                        + events.get(2).eventId()),
                false
        );
        return 1;
    }

    private static int eventsInfo(CommandContext<CommandSourceStack> context) {
        String eventId = StringArgumentType.getString(context, "eventId");
        var event = com.blockforge.common.settlement.event.SettlementEventTemplates.templates("settlement-alpha", 0).stream()
                .filter(candidate -> candidate.eventId().equals(eventId))
                .findFirst()
                .orElse(null);
        if (event == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge settlement event: " + eventId));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal(event.eventId()
                        + " | "
                        + event.title()
                        + " | severity="
                        + event.severity()
                        + " | contracts="
                        + event.relatedContractIds()),
                false
        );
        return 1;
    }

    private static int eventsRefresh(CommandContext<CommandSourceStack> context) {
        var generated = new com.blockforge.common.settlement.event.SettlementEventGenerator()
                .generate("settlement-alpha", 3, com.blockforge.common.settlement.event.SettlementStability.balanced("settlement-alpha"), java.util.List.of(), 3, 0);
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement event refresh alpha generated " + generated.size() + " event(s). Loader persistence is partial."),
                true
        );
        return 1;
    }

    private static int eventsResolve(CommandContext<CommandSourceStack> context) {
        String eventId = StringArgumentType.getString(context, "eventId");
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement event resolve alpha: " + eventId + ". Common outcome/reward rules are available; loader state persistence is partial."),
                true
        );
        return 1;
    }

    private static int eventsIgnore(CommandContext<CommandSourceStack> context) {
        String eventId = StringArgumentType.getString(context, "eventId");
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement event ignored alpha: " + eventId + ". Ignoring events increases maintenance debt in common rules."),
                true
        );
        return 1;
    }

    private static int projectsList(CommandContext<CommandSourceStack> context) {
        var projects = com.blockforge.common.settlement.project.ProjectChainTemplates.templates("settlement-alpha", 0);
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement projects alpha: " + projects.size() + " chains available. Examples: "
                        + projects.get(0).projectId() + ", "
                        + projects.get(1).projectId()),
                false
        );
        return 1;
    }

    private static int projectsInfo(CommandContext<CommandSourceStack> context) {
        String projectId = StringArgumentType.getString(context, "projectId");
        var project = com.blockforge.common.settlement.project.ProjectChainTemplates.templates("settlement-alpha", 0).stream()
                .filter(candidate -> candidate.projectId().equals(projectId))
                .findFirst()
                .orElse(null);
        if (project == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge settlement project: " + projectId));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal(project.projectId()
                        + " | "
                        + project.title()
                        + " | stages="
                        + project.stages().size()
                        + " | current="
                        + (project.currentStage() == null ? "none" : project.currentStage().stageId())),
                false
        );
        return 1;
    }

    private static int projectsActivate(CommandContext<CommandSourceStack> context) {
        String projectId = StringArgumentType.getString(context, "projectId");
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement project activate alpha: " + projectId + ". Project chain state is command-driven in this alpha."),
                true
        );
        return 1;
    }

    private static int projectsStatus(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement project status alpha: current project stage, required contract, and completion are represented in common DTOs."),
                false
        );
        return 1;
    }

    private static int projectsComplete(CommandContext<CommandSourceStack> context) {
        String projectId = StringArgumentType.getString(context, "projectId");
        context.getSource().sendSuccess(
                () -> Component.literal("Settlement project admin-complete alpha: " + projectId),
                true
        );
        return 1;
    }

    private static int emergencyList(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("Emergency repairs alpha: active repair requests are represented by common DTOs and are loader-persistent later."),
                false
        );
        return 1;
    }

    private static int emergencyInfo(CommandContext<CommandSourceStack> context) {
        String repairId = StringArgumentType.getString(context, "repairId");
        context.getSource().sendSuccess(
                () -> Component.literal("Emergency repair alpha info: " + repairId + " requires RepairPlan verification before resolving the event."),
                false
        );
        return 1;
    }

    private static int emergencyRepair(CommandContext<CommandSourceStack> context) {
        String repairId = StringArgumentType.getString(context, "repairId");
        context.getSource().sendSuccess(
                () -> Component.literal("Emergency repair alpha started: " + repairId + ". BuildPlan repair integration remains loader-dependent."),
                true
        );
        return 1;
    }

    private static int emergencyVerify(CommandContext<CommandSourceStack> context) {
        String repairId = StringArgumentType.getString(context, "repairId");
        context.getSource().sendSuccess(
                () -> Component.literal("Emergency repair verification alpha: " + repairId + ". Common verifier checks repaired blocks, remaining issues, and timeout."),
                false
        );
        return 1;
    }

    private static int adminAudit(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge audit alpha: common audit DTO and in-memory log are available. Persistent JSONL export is planned."),
                false
        );
        return 1;
    }

    private static int adminAuditExport(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge audit export alpha: diagnostics export hook is planned; no file was written by this scaffold command."),
                false
        );
        return 1;
    }

    private static int adminBuilds(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge admin builds alpha: active station/buildplan aggregation is planned after persistent job registry lands."),
                false
        );
        return 1;
    }

    private static int quotaGet(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge quota for " + playerName + ": alpha defaults are documented; enforcement is scaffolded in common rules."),
                false
        );
        return 1;
    }

    private static int quotaReset(CommandContext<CommandSourceStack> context) {
        String playerName = StringArgumentType.getString(context, "player");
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge quota reset requested for " + playerName + ". Persistent quota store is planned."),
                true
        );
        return 1;
    }

    private static int housePresets(CommandContext<CommandSourceStack> context) {
        String presets = String.join(", ", HOUSES.supportedPresets()
                .stream()
                .map(style -> style.name().toLowerCase(java.util.Locale.ROOT))
                .toList());
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge house presets: " + presets),
                false
        );
        return HOUSES.supportedPresets().size();
    }

    private static int houseCreate(CommandContext<CommandSourceStack> context) {
        HousePlan plan = HOUSES.generate(HouseGenerationRequest.preset(parseHouseStyle(StringArgumentType.getString(context, "preset"))));
        sendHouseSummary(context, plan, "Created HousePlan alpha");
        return 1;
    }

    private static int houseCreateSized(CommandContext<CommandSourceStack> context) {
        HousePlan plan = HOUSES.generate(new HouseGenerationRequest(
                parseHouseStyle(StringArgumentType.getString(context, "preset")),
                IntegerArgumentType.getInteger(context, "width"),
                IntegerArgumentType.getInteger(context, "depth"),
                IntegerArgumentType.getInteger(context, "floors"),
                null,
                null,
                null
        ));
        sendHouseSummary(context, plan, "Created sized HousePlan alpha");
        return 1;
    }

    private static int houseRoof(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("House roof alpha set request: " + StringArgumentType.getString(context, "type") + ". Persistent per-player HousePlan state is planned; use /blockforge house create for current summary."),
                false
        );
        return 1;
    }

    private static int houseMaterials(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("House materials alpha request: wall="
                        + StringArgumentType.getString(context, "wall")
                        + ", roof="
                        + StringArgumentType.getString(context, "roof")
                        + ", floor="
                        + StringArgumentType.getString(context, "floor")
                        + ". Blueprint registration is command-alpha."),
                false
        );
        return 1;
    }

    private static int housePreview(CommandContext<CommandSourceStack> context) {
        HousePlan plan = HOUSES.generatePreset(HousePlan.HouseStyle.STARTER_COTTAGE);
        var blueprint = new HouseBlueprintCompiler().compile(plan);
        context.getSource().sendSuccess(
                () -> Component.literal("House preview alpha compiled "
                        + blueprint.getId()
                        + " to Blueprint v"
                        + blueprint.getSchemaVersion()
                        + " | size="
                        + blueprint.getSize().format()
                        + " | blocks="
                        + blueprint.getBlockCount()
                        + ". Ghost preview registration is planned."),
                false
        );
        return blueprint.getBlockCount();
    }

    private static int houseBuildPlan(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception exception) {
            context.getSource().sendFailure(Component.literal("House BuildPlan requires a player."));
            return 0;
        }
        HousePlan house = HOUSES.generatePreset(HousePlan.HouseStyle.STARTER_COTTAGE);
        var blueprint = new HouseBlueprintCompiler().compile(house);
        BlockPos pos = player.blockPosition();
        BuildPlan plan = BuildPlanFactory.create(
                blueprint,
                player.getUUID(),
                player.level().dimension().location().toString(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                0,
                false,
                false,
                0,
                0,
                0,
                BuildPlanOptions.defaults(),
                player.level().getGameTime()
        );
        BlockForgeConnector.BUILD_PLANS.delegate().save(player.getUUID(), plan);
        context.getSource().sendSuccess(
                () -> Component.literal("Created House BuildPlan alpha: "
                        + plan.planId()
                        + " | stages are represented by modules; loader BuildPlan still executes by layer."),
                true
        );
        return plan.totalBlocks();
    }

    private static int houseBuild(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("House build alpha: compile with /blockforge house buildplan, then use existing BuildPlan or Builder Wand build flow. Direct house placement is intentionally not separate."),
                false
        );
        return 1;
    }

    private static int houseQuality(CommandContext<CommandSourceStack> context) {
        HousePlan plan = HOUSES.generatePreset(HousePlan.HouseStyle.STARTER_COTTAGE);
        var report = new HouseQualityAnalyzer().analyze(plan);
        context.getSource().sendSuccess(
                () -> Component.literal("House quality alpha: total="
                        + report.total()
                        + " roof="
                        + report.roof()
                        + " entrance="
                        + report.entrance()
                        + " windows="
                        + report.windows()
                        + " warnings="
                        + report.warnings().size()),
                false
        );
        return report.total();
    }

    private static int houseExportBlueprint(CommandContext<CommandSourceStack> context) {
        HousePlan plan = HOUSES.generatePreset(HousePlan.HouseStyle.STARTER_COTTAGE);
        var blueprint = new HouseBlueprintCompiler().compile(plan);
        context.getSource().sendSuccess(
                () -> Component.literal("House export alpha compiled Blueprint v2 id="
                        + blueprint.getId()
                        + " | blocks="
                        + blueprint.getBlockCount()
                        + ". File export is available in the Web House Designer; loader save is planned."),
                false
        );
        return blueprint.getBlockCount();
    }

    private static int houseSave(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        context.getSource().sendSuccess(
                () -> Component.literal("House save alpha requested for '" + name + "'. Persistent in-game house library is planned; Web export is available now."),
                true
        );
        return 1;
    }

    private static void sendHouseSummary(CommandContext<CommandSourceStack> context, HousePlan plan, String prefix) {
        var estimate = new HouseMaterialEstimator().estimate(plan);
        var quality = new HouseQualityAnalyzer().analyze(plan);
        context.getSource().sendSuccess(
                () -> Component.literal(prefix
                        + ": "
                        + plan.name()
                        + " | size="
                        + plan.footprint().width()
                        + "x"
                        + plan.footprint().depth()
                        + " floors="
                        + plan.dimensions().floors()
                        + " roof="
                        + plan.roof().type().name().toLowerCase(java.util.Locale.ROOT)
                        + " modules="
                        + plan.modules().size()
                        + " materials="
                        + estimate.size()
                        + " quality="
                        + quality.total()),
                false
        );
    }

    private static HousePlan.HouseStyle parseHouseStyle(String value) {
        if (value == null || value.isBlank()) {
            return HousePlan.HouseStyle.STARTER_COTTAGE;
        }
        return HousePlan.HouseStyle.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
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

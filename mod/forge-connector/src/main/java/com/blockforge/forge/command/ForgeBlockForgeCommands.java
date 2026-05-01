package com.blockforge.forge.command;

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
import com.blockforge.common.security.permission.BlockForgePermissionAction;
import com.blockforge.common.security.protection.BlockForgeProtectionRegion;
import com.blockforge.common.security.protection.ProtectionPreflightReport;
import com.blockforge.common.selection.PlayerSelection;
import com.blockforge.forge.BlockForgeForge;
import com.blockforge.forge.blueprint.ForgeBlueprintRegistry;
import com.blockforge.forge.blueprint.ForgeExampleBlueprintInstaller;
import com.blockforge.forge.builder.ForgeBlueprintPlacer;
import com.blockforge.forge.material.ForgeMaterialBuildGate;
import com.blockforge.forge.material.source.ForgeMaterialSourceScanner;
import com.blockforge.forge.material.source.ForgeMaterialSourceSettings;
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
    private static final ForgeMaterialBuildGate MATERIALS = new ForgeMaterialBuildGate();
    private static final ForgeMaterialSourceScanner SOURCE_SCANNER = new ForgeMaterialSourceScanner();

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
                .then(Commands.literal("materials")
                        .then(Commands.literal("selected")
                                .executes(context -> materialsSelected(context, registry, selectionManager)))
                        .then(blueprintIdArgument(registry)
                                .executes(context -> materials(context, registry))))
                .then(Commands.literal("sources")
                        .then(Commands.literal("status")
                                .executes(ForgeBlockForgeCommands::sourcesStatus))
                        .then(Commands.literal("enable")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> setSourcesEnabled(context, true)))
                        .then(Commands.literal("disable")
                                .requires(source -> source.hasPermission(2))
                                .executes(context -> setSourcesEnabled(context, false)))
                        .then(Commands.literal("priority")
                                .requires(source -> source.hasPermission(2))
                                .then(sourcePriorityArgument()
                                        .executes(ForgeBlockForgeCommands::setSourcePriority)))
                        .then(Commands.literal("radius")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("radius", IntegerArgumentType.integer(1, 32))
                                        .executes(ForgeBlockForgeCommands::setSourceRadius)))
                        .then(Commands.literal("scan")
                                .executes(ForgeBlockForgeCommands::sourcesScan))
                        .then(Commands.literal("selected")
                                .executes(context -> sourcesSelected(context, registry, selectionManager))))
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
                        .executes(context -> undo(context, undoManager)))
                .then(Commands.literal("settlement")
                        .then(Commands.literal("create")
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge settlement create alpha registered."))))
                        .then(Commands.literal("info")
                                .executes(context -> partial(context, "Forge settlement info alpha registered.")))
                        .then(Commands.literal("list")
                                .executes(context -> partial(context, "Forge settlement list alpha registered."))))
                .then(Commands.literal("contracts")
                        .then(Commands.literal("list")
                                .executes(context -> partial(context, "Forge contracts alpha: common templates are available; loader persistence is partial.")))
                        .then(Commands.literal("info")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge contract info alpha registered."))))
                        .then(Commands.literal("accept")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge contract accept alpha registered."))))
                        .then(Commands.literal("verify")
                                .then(Commands.argument("contractId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge contract verify alpha uses common heuristic rules; world snapshots are pending.")))))
                .then(Commands.literal("architect")
                        .then(Commands.literal("profile")
                                .executes(context -> partial(context, "Forge architect profile alpha registered.")))
                        .then(Commands.literal("contracts")
                                .executes(context -> partial(context, "Forge architect contracts alpha registered.")))
                        .then(Commands.literal("reputation")
                                .executes(context -> partial(context, "Forge architect reputation alpha registered."))))
                .then(Commands.literal("events")
                        .then(Commands.literal("list")
                                .executes(context -> partial(context, "Forge settlement events alpha registered.")))
                        .then(Commands.literal("info")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge event info alpha registered."))))
                        .then(Commands.literal("refresh")
                                .executes(context -> partial(context, "Forge event refresh alpha registered.")))
                        .then(Commands.literal("resolve")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge event resolve alpha registered."))))
                        .then(Commands.literal("ignore")
                                .then(Commands.argument("eventId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge event ignore alpha registered.")))))
                .then(Commands.literal("projects")
                        .then(Commands.literal("list")
                                .executes(context -> partial(context, "Forge project chains alpha registered.")))
                        .then(Commands.literal("info")
                                .then(Commands.argument("projectId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge project info alpha registered."))))
                        .then(Commands.literal("activate")
                                .then(Commands.argument("projectId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge project activate alpha registered."))))
                        .then(Commands.literal("status")
                                .executes(context -> partial(context, "Forge project status alpha registered."))))
                .then(Commands.literal("emergency")
                        .then(Commands.literal("list")
                                .executes(context -> partial(context, "Forge emergency repairs alpha registered.")))
                        .then(Commands.literal("info")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge emergency info alpha registered."))))
                        .then(Commands.literal("repair")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge emergency repair alpha registered."))))
                        .then(Commands.literal("verify")
                                .then(Commands.argument("repairId", StringArgumentType.word())
                                        .executes(context -> partial(context, "Forge emergency verify alpha registered.")))))
                .then(Commands.literal("protection")
                        .then(Commands.literal("folder")
                                .executes(ForgeBlockForgeCommands::protectionFolder))
                        .then(Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(ForgeBlockForgeCommands::protectionReload))
                        .then(Commands.literal("list")
                                .executes(ForgeBlockForgeCommands::protectionList))
                        .then(Commands.literal("info")
                                .then(Commands.argument("region", StringArgumentType.word())
                                        .executes(ForgeBlockForgeCommands::protectionInfo)))
                        .then(Commands.literal("check")
                                .then(blueprintIdArgument(registry)
                                        .executes(context -> protectionCheckAtPlayer(context, registry, selectionManager))
                                        .then(Commands.literal("at")
                                                .then(Commands.argument("x", IntegerArgumentType.integer())
                                                        .then(Commands.argument("y", IntegerArgumentType.integer())
                                                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                                                        .executes(context -> protectionCheckAtCoordinates(context, registry, selectionManager)))))))))
                .then(Commands.literal("permissions")
                        .then(Commands.literal("check")
                                .then(Commands.argument("node", StringArgumentType.word())
                                        .executes(ForgeBlockForgeCommands::permissionCheck)))));
    }

    private static int partial(CommandContext<CommandSourceStack> context, String message) {
        context.getSource().sendSuccess(() -> Component.literal(message), false);
        return 1;
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
        ForgeBlueprintGuiNetworking.syncPreviewSelection(player, blueprint, selection.rotationDegrees());
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
            ForgeBlueprintGuiNetworking.clearPreview(player, "Selected BlockForge Forge blueprint no longer exists.");
            context.getSource().sendFailure(Component.literal("Selected BlockForge Forge blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        ForgeBlueprintGuiNetworking.syncPreviewSelection(player);
        context.getSource().sendSuccess(
                () -> Component.literal("Selected BlockForge Forge blueprint: "
                        + selection.selectedBlueprintId()
                        + " | rotation="
                        + selection.rotationDegrees()
                        + ". Hold Builder Wand and look at a block to see Ghost Preview."),
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

        ForgeBlueprintGuiNetworking.syncPreviewSelection(player);
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
        return Commands.argument("id", StringArgumentType.string())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(registry.getIds(), builder));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> rotationArgument() {
        return Commands.argument("rotation", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of("0", "90", "180", "270"), builder));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> sourcePriorityArgument() {
        return Commands.argument("priority", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(List.of(
                        "PLAYER_FIRST",
                        "CONTAINER_FIRST",
                        "PLAYER_ONLY",
                        "CONTAINER_ONLY"
                ), builder));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> packIdArgument(
            ForgeBlueprintRegistry registry
    ) {
        return Commands.argument("packId", StringArgumentType.word())
                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                        registry.getPacks().stream().map(pack -> pack.manifest().packId()).toList(),
                        builder
                ));
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
        try {
            ForgeBlueprintGuiNetworking.syncPreviewSelection(context.getSource().getPlayerOrException());
        } catch (Exception ignored) {
            // Console reloads have no client preview to update.
        }
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded "
                        + summary.loadedCount()
                        + " BlockForge Forge blueprint(s) from loose files and "
                        + summary.packCount()
                        + " pack(s)."),
                true
        );

        for (String warning : summary.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }

        return summary.loadedCount();
    }

    private static int packsFolder(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge pack folder: " + registry.getPackDirectory()),
                false
        );
        return 1;
    }

    private static int packsList(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        if (registry.getPacks().isEmpty()) {
            context.getSource().sendFailure(Component.literal("No BlockForge Forge packs loaded from " + registry.getPackDirectory()));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("Loaded BlockForge Forge packs:"), false);
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
            ForgeBlueprintRegistry registry
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
            ForgeBlueprintRegistry registry
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
            ForgeBlueprintRegistry registry
    ) {
        var result = registry.validatePacks();
        context.getSource().sendSuccess(
                () -> Component.literal("Validated BlockForge Forge packs: packs="
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

    private static int schematicsFolder(CommandContext<CommandSourceStack> context, ForgeBlueprintRegistry registry) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge schematics folder: " + registry.getSchematicDirectory()),
                false
        );
        return 1;
    }

    private static int schematicsList(CommandContext<CommandSourceStack> context, ForgeBlueprintRegistry registry) {
        var schematics = registry.getSchematics();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded BlockForge Forge schematics: " + schematics.size()),
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

    private static int schematicsInfo(CommandContext<CommandSourceStack> context, ForgeBlueprintRegistry registry) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null || !blueprint.getId().startsWith("schem/")) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge Forge schematic id."));
            return 0;
        }
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge schematic: " + describe(blueprint)
                        + " | palette=" + blueprint.getPalette().size()),
                false
        );
        return 1;
    }

    private static int schematicsValidate(CommandContext<CommandSourceStack> context, ForgeBlueprintRegistry registry) {
        var result = registry.validateSchematics();
        context.getSource().sendSuccess(
                () -> Component.literal("Validated BlockForge Forge schematics: loaded="
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
        sendSourceSettings(context.getSource());
        ServerPlayer player = getPlayerOrNull(context);
        if (player != null) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getLevel(), getBasePosition(context)));
        }
        return result.placedBlocks();
    }

    private static int materials(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        sendMaterialReport(context.getSource(), report);
        if (ForgeMaterialSourceSettings.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getLevel(), player.blockPosition()));
        }
        return 1;
    }

    private static int materialsSelected(
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

        Blueprint blueprint = registry.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            selectionManager.clear(player.getUUID());
            ForgeBlueprintGuiNetworking.clearPreview(player, "Selected BlockForge Forge blueprint no longer exists.");
            context.getSource().sendFailure(Component.literal("Selected BlockForge Forge blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        MaterialReport report = MATERIALS.report(blueprint, player);
        sendMaterialReport(context.getSource(), report);
        if (ForgeMaterialSourceSettings.enableNearbyContainers()) {
            sendMaterialSourceReport(context.getSource(), MATERIALS.sourceReport(blueprint, player, context.getSource().getLevel(), player.blockPosition()));
        }
        return 1;
    }

    private static int sourcesStatus(CommandContext<CommandSourceStack> context) {
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int setSourcesEnabled(CommandContext<CommandSourceStack> context, boolean enabled) {
        ForgeMaterialSourceSettings.setEnableNearbyContainers(enabled);
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int setSourcePriority(CommandContext<CommandSourceStack> context) {
        try {
            MaterialSourcePriority priority = MaterialSourcePriority.valueOf(StringArgumentType.getString(context, "priority").toUpperCase());
            ForgeMaterialSourceSettings.setMaterialSourcePriority(priority);
            sendSourceSettings(context.getSource());
            return 1;
        } catch (IllegalArgumentException error) {
            context.getSource().sendFailure(Component.literal("Unsupported material source priority. Use PLAYER_FIRST, CONTAINER_FIRST, PLAYER_ONLY, or CONTAINER_ONLY."));
            return 0;
        }
    }

    private static int setSourceRadius(CommandContext<CommandSourceStack> context) {
        ForgeMaterialSourceSettings.setNearbyContainerSearchRadius(IntegerArgumentType.getInteger(context, "radius"));
        sendSourceSettings(context.getSource());
        return 1;
    }

    private static int sourcesScan(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }

        ForgeMaterialSourceScanner.Scan scan = SOURCE_SCANNER.scan(
                player,
                context.getSource().getLevel(),
                player.blockPosition(),
                ForgeMaterialSourceSettings.config()
        );
        MaterialSourceScanResult result = scan.result();
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge material sources scan: enabled="
                        + ForgeMaterialSourceSettings.enableNearbyContainers()
                        + " | radius="
                        + ForgeMaterialSourceSettings.nearbyContainerSearchRadius()
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
                        () -> Component.literal("- " + source.displayName() + " @ " + source.x() + "," + source.y() + "," + source.z()),
                        false
                ));
        result.warnings().forEach(warning -> context.getSource().sendFailure(Component.literal("Warning: " + warning)));
        return result.foundContainers();
    }

    private static int sourcesSelected(
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

        Blueprint blueprint = registry.get(selection.selectedBlueprintId()).orElse(null);
        if (blueprint == null) {
            context.getSource().sendFailure(Component.literal("Selected BlockForge Forge blueprint no longer exists. Use /blockforge select <id> again."));
            return 0;
        }

        MaterialSourceReport report = MATERIALS.sourceReport(blueprint, player, context.getSource().getLevel(), player.blockPosition());
        sendMaterialSourceReport(context.getSource(), report);
        return report.totalRequiredItems();
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

        ForgeBlueprintPlacer.PlacementResult dryRun = PLACER.dryRun(
                context.getSource().getLevel(),
                basePos,
                blueprint,
                rotation
        );
        if (dryRun.tooLarge() || dryRun.empty()) {
            sendPlacementResult(context.getSource(), dryRun);
            return 0;
        }
        if (dryRun.placedBlocks() == 0) {
            context.getSource().sendFailure(Component.literal("Blueprint has no valid placeable blocks and cannot be built."));
            return 0;
        }

        ProtectionPreflightReport security = BlockForgeForge.PROTECTION.preflight(
                player,
                context.getSource().getLevel(),
                basePos,
                blueprint,
                rotation,
                BlockForgePermissionAction.BUILD_COMMAND
        );
        if (!security.allowed()) {
            sendSecurityDenied(context.getSource(), security);
            return 0;
        }

        ForgeMaterialBuildGate.BuildMaterialResult materialResult = MATERIALS.prepare(player, context.getSource().getLevel(), basePos, blueprint);
        if (!materialResult.allowed()) {
            context.getSource().sendFailure(Component.literal(materialResult.message()));
            sendMissingMaterials(context.getSource(), materialResult.report());
            if (materialResult.sourceReport() != null) {
                sendMaterialSourceReport(context.getSource(), materialResult.sourceReport());
            }
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
            undoManager.record(result.snapshot().withMaterialTransaction(materialResult.transaction()));
        } else if (materialResult.transaction() != null) {
            MaterialRefundResult rollbackResult = MATERIALS.rollback(player, materialResult.transaction());
            sendPlacementResult(context.getSource(), result);
            if (materialResult.transaction().hasConsumedItems()) {
                context.getSource().sendFailure(Component.literal("Build placed no blocks; rolled back "
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

    private static int protectionFolder(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge protection file: " + BlockForgeForge.PROTECTION.file()),
                false
        );
        return 1;
    }

    private static int protectionReload(CommandContext<CommandSourceStack> context) {
        var config = BlockForgeForge.PROTECTION.reload();
        context.getSource().sendSuccess(
                () -> Component.literal("Loaded " + config.regions().size() + " BlockForge Forge protection region(s)."),
                true
        );
        for (String warning : config.warnings()) {
            context.getSource().sendFailure(Component.literal("Warning: " + warning));
        }
        return config.regions().size();
    }

    private static int protectionList(CommandContext<CommandSourceStack> context) {
        if (BlockForgeForge.PROTECTION.regions().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("No BlockForge Forge protection regions loaded."), false);
            return 0;
        }
        for (BlockForgeProtectionRegion region : BlockForgeForge.PROTECTION.regions()) {
            context.getSource().sendSuccess(() -> Component.literal("- " + describeRegion(region)), false);
        }
        return BlockForgeForge.PROTECTION.regions().size();
    }

    private static int protectionInfo(CommandContext<CommandSourceStack> context) {
        String id = StringArgumentType.getString(context, "region");
        BlockForgeProtectionRegion region = BlockForgeForge.PROTECTION.find(id).orElse(null);
        if (region == null) {
            context.getSource().sendFailure(Component.literal("Unknown BlockForge Forge protection region: " + id));
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

    private static int protectionCheckAtPlayer(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgePlayerSelectionManager selectionManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        PlayerSelection selection = selectionManager.get(player.getUUID()).orElse(null);
        BlueprintRotation rotation = selection == null ? BlueprintRotation.NONE : selection.rotation();
        return protectionCheck(context, registry, player, player.blockPosition(), rotation);
    }

    private static int protectionCheckAtCoordinates(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ForgePlayerSelectionManager selectionManager
    ) {
        ServerPlayer player = getPlayer(context);
        if (player == null) {
            return 0;
        }
        PlayerSelection selection = selectionManager.get(player.getUUID()).orElse(null);
        BlueprintRotation rotation = selection == null ? BlueprintRotation.NONE : selection.rotation();
        BlockPos basePos = new BlockPos(
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "z")
        );
        return protectionCheck(context, registry, player, basePos, rotation);
    }

    private static int protectionCheck(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry,
            ServerPlayer player,
            BlockPos basePos,
            BlueprintRotation rotation
    ) {
        Blueprint blueprint = findBlueprint(context, registry);
        if (blueprint == null) {
            return 0;
        }
        ProtectionPreflightReport report = BlockForgeForge.PROTECTION.preflight(
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
                () -> Component.literal("BlockForge Forge protection check allowed for "
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
        var result = BlockForgeForge.PROTECTION.permissions().check(player, node, 2);
        context.getSource().sendSuccess(
                () -> Component.literal("BlockForge Forge permission " + node + ": " + (result.allowed() ? "allowed" : "denied")),
                false
        );
        if (!result.allowed()) {
            context.getSource().sendFailure(Component.literal(result.reason()));
        }
        return result.allowed() ? 1 : 0;
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
        MaterialRefundResult refundResult = MATERIALS.refund(player, snapshot.materialTransaction());
        sendUndoResult(context.getSource(), result, refundResult, snapshot);
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
                        + ". Use /blockforge undo to restore blocks and refund materials."),
                true
        );
    }

    private static void sendSecurityDenied(CommandSourceStack source, ProtectionPreflightReport report) {
        source.sendFailure(Component.literal(report.reason().isBlank()
                ? "BlockForge Forge build denied by security preflight."
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

    private static void sendUndoResult(
            CommandSourceStack source,
            ForgeUndoManager.UndoResult result,
            MaterialRefundResult refundResult,
            ForgeUndoManager.PlacementSnapshot snapshot
    ) {
        if (snapshot.materialTransaction() == null || snapshot.materialTransaction().creativeBypass()) {
            source.sendSuccess(
                    () -> Component.literal("Undo complete. Restored "
                            + result.restoredBlocks()
                            + " blocks. No materials were consumed."),
                    true
            );
            return;
        }

        source.sendSuccess(
                () -> Component.literal("Undo complete. Restored "
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
            source.sendFailure(Component.literal("Warning: " + warning));
        }
    }

    private static void sendMaterialResult(
            CommandSourceStack source,
            ForgeMaterialBuildGate.BuildMaterialResult materialResult
    ) {
        if (materialResult.report() == null) {
            return;
        }

        if (materialResult.creativeBypass()) {
            source.sendSuccess(() -> Component.literal("Creative mode: no materials consumed."), false);
            return;
        }

        source.sendSuccess(
                () -> Component.literal("Consumed "
                        + materialResult.consumedItems()
                        + (materialResult.consumedFromNearbyContainers() > 0
                        ? " items (nearbyContainers=" + materialResult.consumedFromNearbyContainers() + ")"
                        : " items")
                        + ". Use /blockforge undo to restore blocks and refund materials."),
                true
        );
    }

    private static void sendMaterialReport(CommandSourceStack source, MaterialReport report) {
        source.sendSuccess(
                () -> Component.literal("BlockForge Forge materials: "
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

    private static void sendSourceSettings(CommandSourceStack source) {
        source.sendSuccess(
                () -> Component.literal("BlockForge Forge material sources settings: enabled="
                        + ForgeMaterialSourceSettings.enableNearbyContainers()
                        + " | priority="
                        + ForgeMaterialSourceSettings.materialSourcePriority()
                        + " | radius="
                        + ForgeMaterialSourceSettings.nearbyContainerSearchRadius()
                        + " | maxContainers="
                        + ForgeMaterialSourceSettings.nearbyContainerMaxScanned()
                        + " | returnRefundsToOriginalSource="
                        + ForgeMaterialSourceSettings.returnRefundsToOriginalSource()
                        + " | allowPartialFromContainers="
                        + ForgeMaterialSourceSettings.allowPartialFromContainers()),
                false
        );
    }

    private static void sendMaterialSourceReport(CommandSourceStack source, MaterialSourceReport report) {
        source.sendSuccess(
                () -> Component.literal("BlockForge Forge material sources: "
                        + report.blueprintId()
                        + " | enabled="
                        + ForgeMaterialSourceSettings.enableNearbyContainers()
                        + " | priority="
                        + ForgeMaterialSourceSettings.materialSourcePriority()
                        + " | radius="
                        + ForgeMaterialSourceSettings.nearbyContainerSearchRadius()
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
        report.warnings().forEach(warning -> source.sendFailure(Component.literal("Warning: " + warning)));
    }

    private static int reservedFrom(MaterialSourceReport report, MaterialSourceType type) {
        return report.entries()
                .stream()
                .filter(entry -> entry.source() != null && entry.source().type() == type)
                .mapToInt(MaterialSourceItemEntry::reserved)
                .sum();
    }

    private static void sendMissingMaterials(CommandSourceStack source, MaterialReport report) {
        if (report == null) {
            return;
        }

        report.requirements()
                .stream()
                .filter(requirement -> requirement.missing() > 0)
                .limit(10)
                .forEach(requirement -> source.sendFailure(Component.literal("Missing materials: " + describeRequirement(requirement))));
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

    private static LoadedBlueprintPack findPack(
            CommandContext<CommandSourceStack> context,
            ForgeBlueprintRegistry registry
    ) {
        String packId = StringArgumentType.getString(context, "packId");
        return registry.getPacks()
                .stream()
                .filter(pack -> pack.manifest().packId().equals(packId))
                .findFirst()
                .orElseGet(() -> {
                    context.getSource().sendFailure(Component.literal("Unknown BlockForge Forge pack id: " + packId));
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

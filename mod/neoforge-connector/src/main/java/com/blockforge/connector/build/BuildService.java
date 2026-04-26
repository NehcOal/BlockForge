package com.blockforge.connector.build;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.builder.BlueprintPlacer;
import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.config.BlockForgeConfig;
import com.blockforge.connector.material.MaterialConsumer;
import com.blockforge.connector.material.MaterialRefundResult;
import com.blockforge.connector.material.MaterialReport;
import com.blockforge.connector.material.MaterialTransaction;
import com.blockforge.connector.material.PlayerInventoryMaterialChecker;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceAdapter;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceConsumer;
import com.blockforge.connector.material.source.NeoForgeMaterialSourceScanner;
import com.blockforge.connector.undo.PlacementSnapshot;
import com.blockforge.connector.undo.UndoManager;
import com.blockforge.common.material.source.MaterialSourceReport;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BuildService {
    private final BlueprintPlacer placer = new BlueprintPlacer();
    private final PlayerInventoryMaterialChecker checker = new PlayerInventoryMaterialChecker();
    private final MaterialConsumer consumer = new MaterialConsumer();
    private final NeoForgeMaterialSourceScanner sourceScanner = new NeoForgeMaterialSourceScanner();
    private final NeoForgeMaterialSourceAdapter sourceAdapter = new NeoForgeMaterialSourceAdapter();
    private final NeoForgeMaterialSourceConsumer sourceConsumer = new NeoForgeMaterialSourceConsumer();
    private final UndoManager undoManager;

    public BuildService(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public BuildResult build(
            ServerLevel level,
            ServerPlayer player,
            BlockPos basePos,
            Blueprint blueprint,
            BlueprintRotation rotation
    ) {
        BlueprintPlacer.PlacementResult preflight = placer.dryRun(level, basePos, blueprint, rotation);
        if (preflight.tooLarge() || preflight.empty()) {
            return BuildResult.rejected(preflight, null, null, null, MaterialRefundResult.empty(), "");
        }

        MaterialReport report = null;
        MaterialSourceReport sourceReport = null;
        MaterialTransaction transaction = null;

        if (player != null && BlockForgeConfig.requireMaterialsInSurvival()) {
            PlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
            report = checker.report(blueprint, player, preflight.acceptedBlocks());

            if (!access.allowed()) {
                return BuildResult.rejected(null, report, null, null, MaterialRefundResult.empty(), access.message());
            }

            if (checker.isCreativeBypass(player)) {
                transaction = MaterialTransaction.creative(player.getUUID(), blueprint.getId(), level.getGameTime());
            } else if (!report.enoughMaterials()) {
                if (!BlockForgeConfig.enableNearbyContainers()) {
                    return BuildResult.rejected(null, report, null, null, MaterialRefundResult.empty(), "Not enough materials.");
                }

                NeoForgeMaterialSourceScanner.Scan scan = sourceScanner.scan(
                        player,
                        level,
                        basePos,
                        BlockForgeConfig.materialSourceConfig()
                );
                sourceReport = sourceAdapter.report(
                        report,
                        player,
                        scan.containers(),
                        BlockForgeConfig.materialSourceConfig()
                );
                if (!sourceReport.enoughMaterials()) {
                    return BuildResult.rejected(null, report, sourceReport, null, MaterialRefundResult.empty(), "Not enough materials.");
                }

                MaterialConsumer.ConsumeResult consumeResult = sourceConsumer.consume(
                        player,
                        blueprint.getId(),
                        level.getGameTime(),
                        sourceReport,
                        scan.containers(),
                        BlockForgeConfig.materialSourceConfig()
                );
                transaction = consumeResult.transaction();

                if (!consumeResult.success()) {
                    return BuildResult.rejected(
                            null,
                            report,
                            sourceReport,
                            transaction,
                            consumeResult.rollbackResult(),
                            consumeResult.message()
                    );
                }
            } else {
                MaterialConsumer.ConsumeResult consumeResult;
                if (BlockForgeConfig.enableNearbyContainers()) {
                    NeoForgeMaterialSourceScanner.Scan scan = sourceScanner.scan(
                            player,
                            level,
                            basePos,
                            BlockForgeConfig.materialSourceConfig()
                    );
                    sourceReport = sourceAdapter.report(
                            report,
                            player,
                            scan.containers(),
                            BlockForgeConfig.materialSourceConfig()
                    );
                    consumeResult = sourceConsumer.consume(
                            player,
                            blueprint.getId(),
                            level.getGameTime(),
                            sourceReport,
                            scan.containers(),
                            BlockForgeConfig.materialSourceConfig()
                    );
                } else {
                    consumeResult = consumer.consume(player, blueprint.getId(), level.getGameTime(), report);
                }
                transaction = consumeResult.transaction();

                if (!consumeResult.success()) {
                    return BuildResult.rejected(
                            null,
                            report,
                            sourceReport,
                            transaction,
                            consumeResult.rollbackResult(),
                            consumeResult.message()
                    );
                }
            }
        }

        BlueprintPlacer.PlacementResult result;
        try {
            result = placer.place(level, basePos, blueprint, rotation, player);
        } catch (RuntimeException error) {
            MaterialRefundResult rollback = rollback(player, transaction);
            return BuildResult.rejected(null, report, sourceReport, transaction, rollback, "Build failed: " + error.getMessage());
        }

        if (result.placedBlocks() <= 0 || result.snapshot() == null) {
            MaterialRefundResult rollback = rollback(player, transaction);
            return BuildResult.completed(result, report, sourceReport, transaction, rollback);
        }

        PlacementSnapshot snapshot = result.snapshot().withMaterialTransaction(transaction);
        BlueprintPlacer.PlacementResult resultWithSnapshot = result.withSnapshot(snapshot);
        undoManager.record(snapshot);

        return BuildResult.completed(resultWithSnapshot, report, sourceReport, transaction, MaterialRefundResult.empty());
    }

    private MaterialRefundResult rollback(ServerPlayer player, MaterialTransaction transaction) {
        if (transaction != null && transaction.includesNearbyContainers()) {
            return sourceConsumer.rollbackConsumedMaterials(player, transaction, BlockForgeConfig.materialSourceConfig());
        }
        return consumer.rollbackConsumedMaterials(player, transaction);
    }

    public record BuildResult(
            boolean allowed,
            String message,
            BlueprintPlacer.PlacementResult placementResult,
            MaterialReport materialReport,
            MaterialSourceReport materialSourceReport,
            MaterialTransaction materialTransaction,
            MaterialRefundResult rollbackResult
    ) {
        public static BuildResult rejected(
                BlueprintPlacer.PlacementResult placementResult,
                MaterialReport materialReport,
                MaterialSourceReport materialSourceReport,
                MaterialTransaction materialTransaction,
                MaterialRefundResult rollbackResult,
                String message
        ) {
            return new BuildResult(false, message, placementResult, materialReport, materialSourceReport, materialTransaction, rollbackResult);
        }

        public static BuildResult completed(
                BlueprintPlacer.PlacementResult placementResult,
                MaterialReport materialReport,
                MaterialSourceReport materialSourceReport,
                MaterialTransaction materialTransaction,
                MaterialRefundResult rollbackResult
        ) {
            return new BuildResult(true, "", placementResult, materialReport, materialSourceReport, materialTransaction, rollbackResult);
        }

        public int consumedItems() {
            return materialTransaction == null ? 0 : materialTransaction.totalConsumedItems();
        }

        public boolean creativeBypass() {
            return materialTransaction != null && materialTransaction.creativeBypass();
        }

        public int consumedFromNearbyContainers() {
            if (materialTransaction == null) {
                return 0;
            }

            return materialTransaction.consumedItems()
                    .stream()
                    .filter(entry -> entry.source() != null
                            && entry.source().type() == com.blockforge.common.material.source.MaterialSourceType.NEARBY_CONTAINER)
                    .mapToInt(com.blockforge.connector.material.ConsumedMaterialEntry::count)
                    .sum();
        }

        public int consumedFromPlayerInventory() {
            return Math.max(0, consumedItems() - consumedFromNearbyContainers());
        }
    }
}

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
import com.blockforge.connector.undo.PlacementSnapshot;
import com.blockforge.connector.undo.UndoManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class BuildService {
    private final BlueprintPlacer placer = new BlueprintPlacer();
    private final PlayerInventoryMaterialChecker checker = new PlayerInventoryMaterialChecker();
    private final MaterialConsumer consumer = new MaterialConsumer();
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
        BlueprintPlacer.PlacementResult dryRun = placer.dryRun(blueprint);
        if (dryRun.tooLarge() || dryRun.empty()) {
            return BuildResult.rejected(dryRun, null, null, MaterialRefundResult.empty(), "");
        }

        MaterialReport report = null;
        MaterialTransaction transaction = null;

        if (player != null && BlockForgeConfig.requireMaterialsInSurvival()) {
            PlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
            report = checker.report(blueprint, player);

            if (!access.allowed()) {
                return BuildResult.rejected(null, report, null, MaterialRefundResult.empty(), access.message());
            }

            if (checker.isCreativeBypass(player)) {
                transaction = MaterialTransaction.creative(player.getUUID(), blueprint.getId(), level.getGameTime());
            } else if (!report.enoughMaterials()) {
                return BuildResult.rejected(null, report, null, MaterialRefundResult.empty(), "Not enough materials.");
            } else {
                MaterialConsumer.ConsumeResult consumeResult = consumer.consume(player, blueprint.getId(), level.getGameTime(), report);
                transaction = consumeResult.transaction();

                if (!consumeResult.success()) {
                    return BuildResult.rejected(
                            null,
                            report,
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
            MaterialRefundResult rollback = consumer.rollbackConsumedMaterials(player, transaction);
            return BuildResult.rejected(null, report, transaction, rollback, "Build failed: " + error.getMessage());
        }

        if (result.placedBlocks() <= 0 || result.snapshot() == null) {
            MaterialRefundResult rollback = consumer.rollbackConsumedMaterials(player, transaction);
            return BuildResult.completed(result, report, transaction, rollback);
        }

        PlacementSnapshot snapshot = result.snapshot().withMaterialTransaction(transaction);
        BlueprintPlacer.PlacementResult resultWithSnapshot = result.withSnapshot(snapshot);
        undoManager.record(snapshot);

        return BuildResult.completed(resultWithSnapshot, report, transaction, MaterialRefundResult.empty());
    }

    public record BuildResult(
            boolean allowed,
            String message,
            BlueprintPlacer.PlacementResult placementResult,
            MaterialReport materialReport,
            MaterialTransaction materialTransaction,
            MaterialRefundResult rollbackResult
    ) {
        public static BuildResult rejected(
                BlueprintPlacer.PlacementResult placementResult,
                MaterialReport materialReport,
                MaterialTransaction materialTransaction,
                MaterialRefundResult rollbackResult,
                String message
        ) {
            return new BuildResult(false, message, placementResult, materialReport, materialTransaction, rollbackResult);
        }

        public static BuildResult completed(
                BlueprintPlacer.PlacementResult placementResult,
                MaterialReport materialReport,
                MaterialTransaction materialTransaction,
                MaterialRefundResult rollbackResult
        ) {
            return new BuildResult(true, "", placementResult, materialReport, materialTransaction, rollbackResult);
        }

        public int consumedItems() {
            return materialTransaction == null ? 0 : materialTransaction.totalConsumedItems();
        }

        public boolean creativeBypass() {
            return materialTransaction != null && materialTransaction.creativeBypass();
        }
    }
}

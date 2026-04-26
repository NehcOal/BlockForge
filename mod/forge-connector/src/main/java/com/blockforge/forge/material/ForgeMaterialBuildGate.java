package com.blockforge.forge.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.forge.material.source.ForgeMaterialSourceConsumer;
import com.blockforge.forge.material.source.ForgeMaterialSourceReportBuilder;
import com.blockforge.forge.material.source.ForgeMaterialSourceScanner;
import com.blockforge.forge.material.source.ForgeMaterialSourceSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

public class ForgeMaterialBuildGate {
    private final ForgePlayerInventoryMaterialChecker checker = new ForgePlayerInventoryMaterialChecker();
    private final ForgeMaterialConsumer consumer = new ForgeMaterialConsumer();
    private final ForgeMaterialSourceScanner sourceScanner = new ForgeMaterialSourceScanner();
    private final ForgeMaterialSourceReportBuilder sourceReportBuilder = new ForgeMaterialSourceReportBuilder();
    private final ForgeMaterialSourceConsumer sourceConsumer = new ForgeMaterialSourceConsumer();

    public BuildMaterialResult prepare(ServerPlayer player, Blueprint blueprint) {
        return prepare(player, player == null ? null : player.serverLevel(), player == null ? null : player.blockPosition(), blueprint);
    }

    public BuildMaterialResult prepare(ServerPlayer player, ServerLevel level, BlockPos basePos, Blueprint blueprint) {
        if (player == null || !ForgePlayerInventoryMaterialChecker.REQUIRE_MATERIALS_IN_SURVIVAL) {
            return BuildMaterialResult.allowed(null, null, null);
        }

        ForgePlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
        if (!access.allowed()) {
            return BuildMaterialResult.denied(access.message(), checker.report(blueprint, player));
        }

        MaterialReport report = checker.report(blueprint, player);
        if (checker.isCreativeBypass(player)) {
            return BuildMaterialResult.allowed(
                    report,
                    MaterialTransaction.creative(player.getUUID(), blueprint.getId(), player.level().getGameTime()),
                    null
            );
        }

        MaterialSourceReport sourceReport = null;
        if (ForgeMaterialSourceSettings.enableNearbyContainers()) {
            ForgeMaterialSourceScanner.Scan scan = sourceScanner.scan(player, level, basePos, ForgeMaterialSourceSettings.config());
            sourceReport = sourceReportBuilder.report(report, player, scan.containers(), ForgeMaterialSourceSettings.config());
            if (!sourceReport.enoughMaterials()) {
                return BuildMaterialResult.denied("Not enough materials.", report, sourceReport);
            }

            ForgeMaterialConsumer.ConsumeResult consumeResult = sourceConsumer.consume(
                    player,
                    blueprint.getId(),
                    sourceReport,
                    scan.containers(),
                    ForgeMaterialSourceSettings.config()
            );
            if (!consumeResult.success()) {
                return BuildMaterialResult.denied(consumeResult.message(), report, sourceReport);
            }

            return BuildMaterialResult.allowed(report, consumeResult.transaction(), sourceReport);
        }

        if (!report.enoughMaterials()) {
            return BuildMaterialResult.denied("Not enough materials.", report);
        }

        ForgeMaterialConsumer.ConsumeResult consumeResult = consumer.consumeMaterials(player, report);
        if (!consumeResult.success()) {
            return BuildMaterialResult.denied(consumeResult.message(), report);
        }

        return BuildMaterialResult.allowed(report, consumeResult.transaction(), null);
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
        return checker.report(blueprint, player);
    }

    public MaterialRefundResult rollback(ServerPlayer player, MaterialTransaction transaction) {
        return sourceConsumer.refund(player, transaction, ForgeMaterialSourceSettings.config());
    }

    public MaterialRefundResult refund(ServerPlayer player, MaterialTransaction transaction) {
        return sourceConsumer.refund(player, transaction, ForgeMaterialSourceSettings.config());
    }

    public MaterialSourceReport sourceReport(Blueprint blueprint, ServerPlayer player, ServerLevel level, BlockPos basePos) {
        MaterialReport report = checker.report(blueprint, player);
        ForgeMaterialSourceScanner.Scan scan = sourceScanner.scan(player, level, basePos, ForgeMaterialSourceSettings.config());
        return sourceReportBuilder.report(report, player, scan.containers(), ForgeMaterialSourceSettings.config());
    }

    public record BuildMaterialResult(
            boolean allowed,
            String message,
            MaterialReport report,
            MaterialTransaction transaction,
            MaterialSourceReport sourceReport
    ) {
        public static BuildMaterialResult allowed(MaterialReport report, MaterialTransaction transaction) {
            return allowed(report, transaction, null);
        }

        public static BuildMaterialResult allowed(MaterialReport report, MaterialTransaction transaction, MaterialSourceReport sourceReport) {
            return new BuildMaterialResult(true, "", report, transaction, sourceReport);
        }

        public static BuildMaterialResult denied(String message, MaterialReport report) {
            return denied(message, report, null);
        }

        public static BuildMaterialResult denied(String message, MaterialReport report, MaterialSourceReport sourceReport) {
            return new BuildMaterialResult(false, message, report, null, sourceReport);
        }

        public int consumedItems() {
            return transaction == null ? 0 : transaction.totalConsumedItems();
        }

        public boolean creativeBypass() {
            return transaction != null && transaction.creativeBypass();
        }

        public int consumedFromNearbyContainers() {
            if (transaction == null) {
                return 0;
            }
            return transaction.consumedItems()
                    .stream()
                    .filter(entry -> entry.sourceType() == com.blockforge.common.material.source.MaterialSourceType.NEARBY_CONTAINER)
                    .mapToInt(com.blockforge.common.material.ConsumedMaterialEntry::count)
                    .sum();
        }
    }
}

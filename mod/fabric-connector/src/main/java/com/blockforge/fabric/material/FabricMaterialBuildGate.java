package com.blockforge.fabric.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import com.blockforge.common.material.source.MaterialSourceReport;
import com.blockforge.common.security.protection.ProtectionAction;
import com.blockforge.fabric.BlockForgeFabric;
import com.blockforge.fabric.material.source.FabricContainerMaterialSource;
import com.blockforge.fabric.material.source.FabricMaterialSourceConsumer;
import com.blockforge.fabric.material.source.FabricMaterialSourceReportBuilder;
import com.blockforge.fabric.material.source.FabricMaterialSourceScanner;
import com.blockforge.fabric.material.source.FabricMaterialSourceSettings;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FabricMaterialBuildGate {
    private final FabricPlayerInventoryMaterialChecker checker = new FabricPlayerInventoryMaterialChecker();
    private final FabricMaterialConsumer consumer = new FabricMaterialConsumer();
    private final FabricMaterialSourceScanner sourceScanner = new FabricMaterialSourceScanner();
    private final FabricMaterialSourceReportBuilder sourceReportBuilder = new FabricMaterialSourceReportBuilder();
    private final FabricMaterialSourceConsumer sourceConsumer = new FabricMaterialSourceConsumer();

    public BuildMaterialResult prepare(ServerPlayerEntity player, Blueprint blueprint) {
        return prepare(player, player == null ? null : player.getServerWorld(), player == null ? null : player.getBlockPos(), blueprint);
    }

    public BuildMaterialResult prepare(ServerPlayerEntity player, ServerWorld world, BlockPos basePos, Blueprint blueprint) {
        if (player == null || !FabricPlayerInventoryMaterialChecker.REQUIRE_MATERIALS_IN_SURVIVAL) {
            return BuildMaterialResult.allowed(null, null, null);
        }

        FabricPlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
        if (!access.allowed()) {
            return BuildMaterialResult.denied(access.message(), checker.report(blueprint, player));
        }

        MaterialReport report = checker.report(blueprint, player);
        if (checker.isCreativeBypass(player)) {
            return BuildMaterialResult.allowed(
                    report,
                    MaterialTransaction.creative(player.getUuid(), blueprint.getId(), player.getServerWorld().getTime()),
                    null
            );
        }

        MaterialSourceReport sourceReport = null;
        if (FabricMaterialSourceSettings.enableNearbyContainers()) {
            FabricMaterialSourceScanner.Scan scan = sourceScanner.scan(player, world, basePos, FabricMaterialSourceSettings.config());
            var containers = scan.containers()
                    .stream()
                    .filter(container -> BlockForgeFabric.PROTECTION.canUseContainer(player, world, container.pos(), ProtectionAction.USE_CONTAINER_MATERIALS))
                    .toList();
            sourceReport = sourceReportBuilder.report(report, player, containers, FabricMaterialSourceSettings.config());
            if (!sourceReport.enoughMaterials()) {
                return BuildMaterialResult.denied("Not enough materials.", report, sourceReport);
            }

            FabricMaterialConsumer.ConsumeResult consumeResult = sourceConsumer.consume(
                    player,
                    blueprint.getId(),
                    sourceReport,
                    containers,
                    FabricMaterialSourceSettings.config()
            );
            if (!consumeResult.success()) {
                return BuildMaterialResult.denied(consumeResult.message(), report, sourceReport);
            }

            return BuildMaterialResult.allowed(report, consumeResult.transaction(), sourceReport);
        }

        if (!report.enoughMaterials()) {
            return BuildMaterialResult.denied("Not enough materials.", report);
        }

        FabricMaterialConsumer.ConsumeResult consumeResult = consumer.consumeMaterials(player, report);
        if (!consumeResult.success()) {
            return BuildMaterialResult.denied(consumeResult.message(), report);
        }

        return BuildMaterialResult.allowed(report, consumeResult.transaction(), null);
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayerEntity player) {
        return checker.report(blueprint, player);
    }

    public MaterialRefundResult rollback(ServerPlayerEntity player, MaterialTransaction transaction) {
        return sourceConsumer.refund(player, transaction, FabricMaterialSourceSettings.config());
    }

    public MaterialRefundResult refund(ServerPlayerEntity player, MaterialTransaction transaction) {
        return sourceConsumer.refund(player, transaction, FabricMaterialSourceSettings.config());
    }

    public MaterialSourceReport sourceReport(Blueprint blueprint, ServerPlayerEntity player, ServerWorld world, BlockPos basePos) {
        MaterialReport report = checker.report(blueprint, player);
        FabricMaterialSourceScanner.Scan scan = sourceScanner.scan(player, world, basePos, FabricMaterialSourceSettings.config());
        var containers = scan.containers()
                .stream()
                .filter(container -> BlockForgeFabric.PROTECTION.canUseContainer(player, world, container.pos(), ProtectionAction.SCAN_CONTAINER))
                .toList();
        return sourceReportBuilder.report(report, player, containers, FabricMaterialSourceSettings.config());
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

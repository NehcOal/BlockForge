package com.blockforge.forge.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import com.blockforge.common.material.MaterialRefundResult;
import com.blockforge.common.material.MaterialTransaction;
import net.minecraft.server.level.ServerPlayer;

public class ForgeMaterialBuildGate {
    private final ForgePlayerInventoryMaterialChecker checker = new ForgePlayerInventoryMaterialChecker();
    private final ForgeMaterialConsumer consumer = new ForgeMaterialConsumer();

    public BuildMaterialResult prepare(ServerPlayer player, Blueprint blueprint) {
        if (player == null || !ForgePlayerInventoryMaterialChecker.REQUIRE_MATERIALS_IN_SURVIVAL) {
            return BuildMaterialResult.allowed(null, null);
        }

        ForgePlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
        if (!access.allowed()) {
            return BuildMaterialResult.denied(access.message(), checker.report(blueprint, player));
        }

        MaterialReport report = checker.report(blueprint, player);
        if (checker.isCreativeBypass(player)) {
            return BuildMaterialResult.allowed(
                    report,
                    MaterialTransaction.creative(player.getUUID(), blueprint.getId(), player.level().getGameTime())
            );
        }

        if (!report.enoughMaterials()) {
            return BuildMaterialResult.denied("Not enough materials.", report);
        }

        ForgeMaterialConsumer.ConsumeResult consumeResult = consumer.consumeMaterials(player, report);
        if (!consumeResult.success()) {
            return BuildMaterialResult.denied(consumeResult.message(), report);
        }

        return BuildMaterialResult.allowed(report, consumeResult.transaction());
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
        return checker.report(blueprint, player);
    }

    public MaterialRefundResult rollback(ServerPlayer player, MaterialTransaction transaction) {
        return consumer.refundMaterials(player, transaction);
    }

    public MaterialRefundResult refund(ServerPlayer player, MaterialTransaction transaction) {
        return consumer.refundMaterials(player, transaction);
    }

    public record BuildMaterialResult(
            boolean allowed,
            String message,
            MaterialReport report,
            MaterialTransaction transaction
    ) {
        public static BuildMaterialResult allowed(MaterialReport report, MaterialTransaction transaction) {
            return new BuildMaterialResult(true, "", report, transaction);
        }

        public static BuildMaterialResult denied(String message, MaterialReport report) {
            return new BuildMaterialResult(false, message, report, null);
        }

        public int consumedItems() {
            return transaction == null ? 0 : transaction.totalConsumedItems();
        }

        public boolean creativeBypass() {
            return transaction != null && transaction.creativeBypass();
        }
    }
}

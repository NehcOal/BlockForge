package com.blockforge.connector.material;

import com.blockforge.connector.blueprint.Blueprint;
import com.blockforge.connector.config.BlockForgeConfig;
import net.minecraft.server.level.ServerPlayer;

public class MaterialBuildGate {
    private final PlayerInventoryMaterialChecker checker = new PlayerInventoryMaterialChecker();
    private final MaterialConsumer consumer = new MaterialConsumer();

    public BuildMaterialResult prepare(ServerPlayer player, Blueprint blueprint) {
        if (player == null || !BlockForgeConfig.requireMaterialsInSurvival()) {
            return BuildMaterialResult.allowed(null, 0, false);
        }

        PlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
        if (!access.allowed()) {
            return BuildMaterialResult.denied(access.message(), checker.report(blueprint, player));
        }

        MaterialReport report = checker.report(blueprint, player);
        if (checker.isCreativeBypass(player)) {
            return BuildMaterialResult.allowed(report, 0, true);
        }

        if (!report.enoughMaterials()) {
            return BuildMaterialResult.denied("Not enough materials.", report);
        }

        MaterialConsumer.ConsumeResult consumeResult = consumer.consume(player, blueprint.getId(), player.serverLevel().getGameTime(), report);
        if (!consumeResult.success()) {
            return BuildMaterialResult.denied(consumeResult.message(), report);
        }

        return BuildMaterialResult.allowed(report, consumeResult.transaction(), false);
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
        return checker.report(blueprint, player);
    }

    public record BuildMaterialResult(
            boolean allowed,
            String message,
            MaterialReport report,
            MaterialTransaction transaction,
            boolean creativeBypass
    ) {
        public static BuildMaterialResult allowed(MaterialReport report, int consumedItems, boolean creativeBypass) {
            return new BuildMaterialResult(true, "", report, null, creativeBypass);
        }

        public static BuildMaterialResult allowed(MaterialReport report, MaterialTransaction transaction, boolean creativeBypass) {
            return new BuildMaterialResult(true, "", report, transaction, creativeBypass);
        }

        public static BuildMaterialResult denied(String message, MaterialReport report) {
            return new BuildMaterialResult(false, message, report, null, false);
        }

        public int consumedItems() {
            return transaction == null ? 0 : transaction.totalConsumedItems();
        }
    }
}

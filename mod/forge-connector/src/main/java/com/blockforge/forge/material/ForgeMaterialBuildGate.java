package com.blockforge.forge.material;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.material.MaterialReport;
import net.minecraft.server.level.ServerPlayer;

public class ForgeMaterialBuildGate {
    private final ForgePlayerInventoryMaterialChecker checker = new ForgePlayerInventoryMaterialChecker();
    private final ForgeMaterialConsumer consumer = new ForgeMaterialConsumer();

    public BuildMaterialResult prepare(ServerPlayer player, Blueprint blueprint) {
        if (player == null || !ForgePlayerInventoryMaterialChecker.REQUIRE_MATERIALS_IN_SURVIVAL) {
            return BuildMaterialResult.allowed(null, 0, false);
        }

        ForgePlayerInventoryMaterialChecker.AccessResult access = checker.canBuild(player);
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

        ForgeMaterialConsumer.ConsumeResult consumeResult = consumer.consume(player, report);
        if (!consumeResult.success()) {
            return BuildMaterialResult.denied(consumeResult.message(), report);
        }

        return BuildMaterialResult.allowed(report, consumeResult.consumedItems(), false);
    }

    public MaterialReport report(Blueprint blueprint, ServerPlayer player) {
        return checker.report(blueprint, player);
    }

    public record BuildMaterialResult(
            boolean allowed,
            String message,
            MaterialReport report,
            int consumedItems,
            boolean creativeBypass
    ) {
        public static BuildMaterialResult allowed(MaterialReport report, int consumedItems, boolean creativeBypass) {
            return new BuildMaterialResult(true, "", report, consumedItems, creativeBypass);
        }

        public static BuildMaterialResult denied(String message, MaterialReport report) {
            return new BuildMaterialResult(false, message, report, 0, false);
        }
    }
}

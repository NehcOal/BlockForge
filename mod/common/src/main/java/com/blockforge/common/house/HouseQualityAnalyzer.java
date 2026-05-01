package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseModuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class HouseQualityAnalyzer {
    private static final Set<String> RARE_BLOCKS = Set.of(
            "minecraft:diamond_block",
            "minecraft:emerald_block",
            "minecraft:netherite_block",
            "minecraft:beacon"
    );

    public HouseQualityReport analyze(HousePlan plan) {
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        boolean hasFoundation = hasModule(plan, HouseModuleType.FOUNDATION) || hasModule(plan, HouseModuleType.FLOOR);
        boolean hasDoor = !plan.openings().doors().isEmpty() || hasModule(plan, HouseModuleType.DOOR);
        boolean hasWindow = !plan.openings().windows().isEmpty() || hasModule(plan, HouseModuleType.WINDOW);
        boolean hasRoof = hasModule(plan, HouseModuleType.ROOF);
        boolean hasStair = hasModule(plan, HouseModuleType.STAIR);

        int enclosure = hasFoundation ? 90 : 40;
        int roof = !plan.options().buildRoof() ? 80 : (hasRoof ? 90 : 20);
        int entrance = hasDoor ? 100 : 10;
        int windows = !plan.options().addWindows() ? 85 : (hasWindow ? 90 : 30);
        int interior = plan.options().hollowInterior() ? 90 : 45;
        int buildability = buildabilityScore(plan);
        int materials = materialScore(plan);

        if (!hasFoundation) {
            warnings.add("House has no foundation or floor module.");
            suggestions.add("Enable foundation or interior floor.");
        }
        if (!hasDoor) {
            warnings.add("House has no planned entrance.");
            suggestions.add("Add at least one door.");
        }
        if (plan.options().addWindows() && !hasWindow) {
            warnings.add("Windows are enabled but no window modules exist.");
        }
        if (plan.options().buildRoof() && !hasRoof) {
            warnings.add("Roof is enabled but no roof module exists.");
        }
        if (plan.dimensions().floors() > 1 && !hasStair) {
            warnings.add("Multi-floor house has no stair or ladder plan.");
            suggestions.add("Add stairs for upper floors.");
        }
        if (materials < 80) {
            warnings.add("Survival-friendly mode contains rare material choices.");
        }

        int total = clamp((enclosure + roof + entrance + windows + interior + buildability + materials) / 7);
        return new HouseQualityReport(total, enclosure, roof, entrance, windows, interior, buildability, materials, warnings, suggestions);
    }

    private static boolean hasModule(HousePlan plan, HouseModuleType type) {
        return plan.modules().stream().anyMatch(module -> module.type() == type);
    }

    private static int buildabilityScore(HousePlan plan) {
        int blocks = plan.modules().stream().mapToInt(module -> Math.max(0, module.volume())).sum();
        if (plan.footprint().width() <= 0 || plan.footprint().depth() <= 0 || plan.dimensions().floors() <= 0) {
            return 0;
        }
        if (blocks > 5000) {
            return 45;
        }
        if (plan.footprint().width() > 32 || plan.footprint().depth() > 32 || plan.dimensions().floors() > 4) {
            return 55;
        }
        return 90;
    }

    private static int materialScore(HousePlan plan) {
        if (!plan.options().survivalFriendly()) {
            return 85;
        }
        boolean hasRare = plan.modules().stream().map(HousePlan.HouseModule::blockKey).anyMatch(RARE_BLOCKS::contains);
        return hasRare ? 45 : 92;
    }

    private static int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }
}

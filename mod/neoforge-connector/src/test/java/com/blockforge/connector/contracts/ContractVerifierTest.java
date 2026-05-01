package com.blockforge.connector.contracts;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;
import com.blockforge.common.blueprint.BlueprintSize;
import com.blockforge.common.contracts.ContractTemplates;
import com.blockforge.common.contracts.ContractVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractVerifierTest {
    @Test
    void passesStarterCottageHeuristic() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("starter_cottage"))
                .findFirst()
                .orElseThrow();

        var result = new ContractVerifier().verify(contract, cottageBlueprint());

        assertTrue(result.passed(), String.join(", ", result.failedChecks()));
        assertTrue(result.awardedReputation() > 0);
    }

    @Test
    void failsBannedBlockRequirement() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("wall_segment"))
                .findFirst()
                .orElseThrow();

        var result = new ContractVerifier().verify(contract, tntBlueprint());

        assertFalse(result.passed());
        assertTrue(result.failedChecks().stream().anyMatch(check -> check.contains("banned block")));
    }

    @Test
    void acceptsColoredWoolForMarketStallContract() {
        var contract = ContractTemplates.templates().stream()
                .filter(template -> template.contractId().equals("market_stall"))
                .findFirst()
                .orElseThrow();

        var result = new ContractVerifier().verify(contract, marketStallBlueprint());

        assertTrue(result.passed(), String.join(", ", result.failedChecks()));
    }

    public static Blueprint cottageBlueprint() {
        return new Blueprint(
                2,
                "cottage",
                "Cottage",
                "",
                "1.21.1",
                "test",
                new BlueprintSize(5, 4, 5),
                Map.of(
                        "planks", new BlueprintPaletteEntry("minecraft:oak_planks", Map.of()),
                        "door", new BlueprintPaletteEntry("minecraft:oak_door", Map.of()),
                        "glass", new BlueprintPaletteEntry("minecraft:glass", Map.of())
                ),
                List.of(
                        new BlueprintBlock(0, 0, 0, "planks"), new BlueprintBlock(1, 0, 0, "planks"), new BlueprintBlock(2, 0, 0, "planks"), new BlueprintBlock(3, 0, 0, "planks"), new BlueprintBlock(4, 0, 0, "planks"),
                        new BlueprintBlock(0, 0, 1, "planks"), new BlueprintBlock(1, 0, 1, "planks"), new BlueprintBlock(2, 0, 1, "planks"), new BlueprintBlock(3, 0, 1, "planks"), new BlueprintBlock(4, 0, 1, "planks"),
                        new BlueprintBlock(0, 0, 2, "planks"), new BlueprintBlock(1, 0, 2, "planks"), new BlueprintBlock(2, 0, 2, "planks"), new BlueprintBlock(3, 0, 2, "planks"), new BlueprintBlock(4, 0, 2, "planks"),
                        new BlueprintBlock(0, 0, 3, "planks"), new BlueprintBlock(1, 0, 3, "planks"), new BlueprintBlock(2, 0, 3, "planks"), new BlueprintBlock(3, 0, 3, "planks"), new BlueprintBlock(4, 0, 3, "planks"),
                        new BlueprintBlock(0, 1, 0, "door"), new BlueprintBlock(4, 1, 4, "glass"),
                        new BlueprintBlock(0, 3, 0, "planks"), new BlueprintBlock(1, 3, 0, "planks"), new BlueprintBlock(2, 3, 0, "planks"), new BlueprintBlock(3, 3, 0, "planks"), new BlueprintBlock(4, 3, 0, "planks"),
                        new BlueprintBlock(0, 3, 1, "planks"), new BlueprintBlock(1, 3, 1, "planks"), new BlueprintBlock(2, 3, 1, "planks"), new BlueprintBlock(3, 3, 1, "planks"), new BlueprintBlock(4, 3, 1, "planks")
                )
        );
    }

    private static Blueprint tntBlueprint() {
        return new Blueprint(
                2,
                "unsafe_wall",
                "Unsafe Wall",
                "",
                "1.21.1",
                "test",
                new BlueprintSize(3, 3, 3),
                Map.of("tnt", new BlueprintPaletteEntry("minecraft:tnt", Map.of())),
                List.of(new BlueprintBlock(0, 0, 0, "tnt"), new BlueprintBlock(1, 0, 0, "tnt"), new BlueprintBlock(2, 0, 0, "tnt"))
        );
    }

    private static Blueprint marketStallBlueprint() {
        return new Blueprint(
                2,
                "market_stall",
                "Market Stall",
                "",
                "1.21.1",
                "test",
                new BlueprintSize(5, 5, 5),
                Map.of(
                        "wood", new BlueprintPaletteEntry("minecraft:oak_planks", Map.of()),
                        "wool", new BlueprintPaletteEntry("minecraft:red_wool", Map.of())
                ),
                List.of(
                        new BlueprintBlock(0, 0, 0, "wood"), new BlueprintBlock(1, 0, 0, "wood"), new BlueprintBlock(2, 0, 0, "wood"), new BlueprintBlock(3, 0, 0, "wood"), new BlueprintBlock(4, 0, 0, "wood"),
                        new BlueprintBlock(0, 0, 1, "wood"), new BlueprintBlock(1, 0, 1, "wood"), new BlueprintBlock(2, 0, 1, "wood"), new BlueprintBlock(3, 0, 1, "wood"), new BlueprintBlock(4, 0, 1, "wood"),
                        new BlueprintBlock(0, 0, 2, "wood"), new BlueprintBlock(1, 0, 2, "wood"), new BlueprintBlock(2, 0, 2, "wood"), new BlueprintBlock(3, 0, 2, "wood"), new BlueprintBlock(4, 0, 2, "wood"),
                        new BlueprintBlock(0, 0, 3, "wood"), new BlueprintBlock(1, 0, 3, "wood"), new BlueprintBlock(2, 0, 3, "wood"), new BlueprintBlock(3, 0, 3, "wood"), new BlueprintBlock(4, 0, 3, "wood"),
                        new BlueprintBlock(0, 0, 4, "wood"), new BlueprintBlock(1, 0, 4, "wood"), new BlueprintBlock(2, 0, 4, "wood"), new BlueprintBlock(3, 0, 4, "wood"), new BlueprintBlock(4, 0, 4, "wood"),
                        new BlueprintBlock(0, 4, 0, "wool"), new BlueprintBlock(1, 4, 0, "wool"), new BlueprintBlock(2, 4, 0, "wool"),
                        new BlueprintBlock(0, 4, 1, "wool"), new BlueprintBlock(1, 4, 1, "wool"), new BlueprintBlock(2, 4, 1, "wool"),
                        new BlueprintBlock(0, 4, 2, "wool"), new BlueprintBlock(1, 4, 2, "wool"), new BlueprintBlock(2, 4, 2, "wool")
                )
        );
    }
}

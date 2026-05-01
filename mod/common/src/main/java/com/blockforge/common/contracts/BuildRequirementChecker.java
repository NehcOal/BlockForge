package com.blockforge.common.contracts;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BuildRequirementChecker {
    private final StructureShapeAnalyzer shapeAnalyzer;

    public BuildRequirementChecker() {
        this(new StructureShapeAnalyzer());
    }

    public BuildRequirementChecker(StructureShapeAnalyzer shapeAnalyzer) {
        this.shapeAnalyzer = shapeAnalyzer;
    }

    public List<String> failedChecks(Blueprint blueprint, BuildContractRequirements requirements) {
        List<String> failed = new ArrayList<>();
        if (blueprint == null) {
            failed.add("blueprint is required");
            return failed;
        }
        int blockCount = blueprint.getBlockCount();
        if (blockCount < requirements.minBlocks() || blockCount > requirements.maxBlocks()) {
            failed.add("block count outside contract range");
        }
        if (blueprint.getSize() == null) {
            failed.add("blueprint size missing");
        } else {
            if (blueprint.getSize().width() < requirements.minWidth() || blueprint.getSize().width() > requirements.maxWidth()) {
                failed.add("width outside contract range");
            }
            if (blueprint.getSize().height() < requirements.minHeight() || blueprint.getSize().height() > requirements.maxHeight()) {
                failed.add("height outside contract range");
            }
            if (blueprint.getSize().depth() < requirements.minDepth() || blueprint.getSize().depth() > requirements.maxDepth()) {
                failed.add("depth outside contract range");
            }
        }

        Set<String> paletteNames = blueprint.getPalette().values().stream()
                .map(BlueprintPaletteEntry::name)
                .filter(Objects::nonNull)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        for (String required : requirements.requiredBlockIds()) {
            if (!paletteNames.contains(required.toLowerCase(Locale.ROOT))) {
                failed.add("missing required block " + required);
            }
        }
        for (String banned : requirements.bannedBlockIds()) {
            if (paletteNames.contains(banned.toLowerCase(Locale.ROOT))) {
                failed.add("contains banned block " + banned);
            }
        }
        if (requirements.requireDoor() && !shapeAnalyzer.hasDoor(blueprint)) {
            failed.add("door heuristic failed");
        }
        if (requirements.requireRoof() && !shapeAnalyzer.hasRoof(blueprint)) {
            failed.add("roof heuristic failed");
        }
        if (requirements.requireWindows() && !shapeAnalyzer.hasWindows(blueprint)) {
            failed.add("window heuristic failed");
        }
        if (requirements.requireFoundation() && !shapeAnalyzer.hasFoundation(blueprint)) {
            failed.add("foundation heuristic failed");
        }
        return failed;
    }
}

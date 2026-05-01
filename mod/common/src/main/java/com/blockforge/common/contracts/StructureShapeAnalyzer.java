package com.blockforge.common.contracts;

import com.blockforge.common.blueprint.Blueprint;
import com.blockforge.common.blueprint.BlueprintBlock;
import com.blockforge.common.blueprint.BlueprintPaletteEntry;

import java.util.Locale;
import java.util.Objects;

public class StructureShapeAnalyzer {
    public boolean hasDoor(Blueprint blueprint) {
        return hasPaletteNameContaining(blueprint, "door");
    }

    public boolean hasWindows(Blueprint blueprint) {
        return hasPaletteNameContaining(blueprint, "glass");
    }

    public boolean hasRoof(Blueprint blueprint) {
        if (blueprint == null || blueprint.getSize() == null) {
            return false;
        }
        int topY = Math.max(0, blueprint.getSize().height() - 1);
        long topBlocks = blueprint.getBlocks().stream()
                .filter(block -> block.getY() == topY)
                .count();
        return topBlocks >= Math.max(1, blueprint.getSize().width() * blueprint.getSize().depth() / 3);
    }

    public boolean hasFoundation(Blueprint blueprint) {
        if (blueprint == null || blueprint.getSize() == null) {
            return false;
        }
        long foundationBlocks = blueprint.getBlocks().stream()
                .filter(block -> block.getY() == 0)
                .count();
        return foundationBlocks >= Math.max(1, blueprint.getSize().width() * blueprint.getSize().depth() / 2);
    }

    private boolean hasPaletteNameContaining(Blueprint blueprint, String token) {
        if (blueprint == null) {
            return false;
        }
        return blueprint.getBlocks().stream()
                .map(BlueprintBlock::getState)
                .map(blueprint.getPalette()::get)
                .filter(Objects::nonNull)
                .map(BlueprintPaletteEntry::name)
                .filter(Objects::nonNull)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .anyMatch(name -> name.contains(token));
    }
}

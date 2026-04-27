package com.blockforge.common.schematic;

import java.util.List;
import java.util.Map;

public record SpongeSchematic(
        int version,
        int dataVersion,
        int width,
        int height,
        int length,
        int[] offset,
        Map<String, Integer> palette,
        List<Integer> data,
        List<String> warnings
) {
}

package com.blockforge.common.material;

import com.blockforge.common.blueprint.Blueprint;

import java.util.Map;
import java.util.function.Function;

public final class MaterialCounterCore {
    private MaterialCounterCore() {
    }

    public static MaterialReport withAvailability(
            Blueprint blueprint,
            Map<String, Integer> availableItems,
            Function<String, String> itemIdResolver
    ) {
        MaterialCounter counter = new MaterialCounter(itemIdResolver);
        return counter.withAvailability(
                blueprint.getId(),
                blueprint.getBlocks(),
                blueprint.getPalette(),
                availableItems
        );
    }

    public static MaterialReport count(Blueprint blueprint, Function<String, String> itemIdResolver) {
        return withAvailability(blueprint, Map.of(), itemIdResolver);
    }
}

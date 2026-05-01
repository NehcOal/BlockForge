package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseModuleType;

import java.util.List;

public record HousePartTemplate(
        String templateId,
        HouseModuleType type,
        int width,
        int height,
        int depth,
        List<RelativeBlock> blocks,
        List<HouseConnector> connectors,
        List<String> tags
) {
    public HousePartTemplate {
        blocks = blocks == null ? List.of() : List.copyOf(blocks);
        connectors = connectors == null ? List.of() : List.copyOf(connectors);
        tags = tags == null ? List.of() : List.copyOf(tags);
    }

    public record RelativeBlock(int x, int y, int z, String blockId) {
    }

    public record HouseConnector(String connectorId, String direction, int x, int y, int z, String tag) {
    }
}

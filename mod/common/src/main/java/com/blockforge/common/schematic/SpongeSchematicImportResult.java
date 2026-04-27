package com.blockforge.common.schematic;

import com.blockforge.common.blueprint.Blueprint;

import java.util.List;

public record SpongeSchematicImportResult(Blueprint blueprint, SpongeSchematic schematic, List<String> warnings) {
}

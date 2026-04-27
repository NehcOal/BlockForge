package com.blockforge.common.pack;

public record BlueprintPackRegistryEntry(
        String packId,
        String blueprintId,
        String registryId,
        String name,
        String path
) {
    public BlueprintPackRegistryEntry(BlueprintPackManifest manifest, BlueprintPackEntry entry) {
        this(
                manifest.packId(),
                entry.id(),
                BlueprintPackPaths.registryId(manifest.packId(), entry.id()),
                entry.name(),
                entry.path()
        );
    }
}

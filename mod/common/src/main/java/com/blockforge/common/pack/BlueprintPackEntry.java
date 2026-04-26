package com.blockforge.common.pack;

import java.util.List;

public record BlueprintPackEntry(
        String id,
        String name,
        String path,
        String description,
        List<String> tags,
        String previewImage
) {
    public BlueprintPackEntry {
        id = BlueprintPackPaths.requireSafeId(id, "blueprint id");
        name = name == null || name.isBlank() ? id : name;
        path = BlueprintPackPaths.validateBlueprintPath(path);
        description = description == null ? "" : description;
        tags = tags == null ? List.of() : List.copyOf(tags);
        previewImage = previewImage == null ? "" : previewImage;
    }
}

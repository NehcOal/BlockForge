package com.blockforge.common.station;

public record MaterialLinkRef(
        String linkId,
        String dimensionId,
        int x,
        int y,
        int z,
        String sourceId,
        MaterialLinkType type
) {
    public MaterialLinkRef {
        linkId = linkId == null ? "" : linkId;
        dimensionId = dimensionId == null ? "" : dimensionId;
        sourceId = sourceId == null ? "" : sourceId;
        type = type == null ? MaterialLinkType.MATERIAL_CACHE : type;
    }

    public boolean sameDimension(String dimension) {
        return dimensionId.equals(dimension == null ? "" : dimension);
    }
}

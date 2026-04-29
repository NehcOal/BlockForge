package com.blockforge.common.buildstation;

import com.blockforge.common.material.source.MaterialSourceReport;

public record StationMaterialResolution(
        boolean allowed,
        MaterialSourceReport report,
        String message
) {
    public StationMaterialResolution {
        message = message == null ? "" : message;
    }
}

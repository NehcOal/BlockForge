package com.blockforge.connector.player;

import com.blockforge.connector.builder.BlueprintRotation;
import com.blockforge.connector.config.BlockForgeConfig;

public class PlayerBlueprintSelection {
    private String selectedBlueprintId;
    private BlueprintRotation rotation = BlueprintRotation.NONE;
    private long lastBuildGameTime = -BlockForgeConfig.wandCooldownTicks();

    public String getSelectedBlueprintId() {
        return selectedBlueprintId;
    }

    public void setSelectedBlueprintId(String selectedBlueprintId) {
        this.selectedBlueprintId = selectedBlueprintId;
    }

    public BlueprintRotation getRotation() {
        return rotation;
    }

    public void setRotation(BlueprintRotation rotation) {
        this.rotation = rotation;
    }

    public boolean hasSelection() {
        return selectedBlueprintId != null && !selectedBlueprintId.isBlank();
    }

    public long getLastBuildGameTime() {
        return lastBuildGameTime;
    }

    public void setLastBuildGameTime(long lastBuildGameTime) {
        this.lastBuildGameTime = lastBuildGameTime;
    }
}

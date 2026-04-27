package com.blockforge.fabric.security;

public final class FabricSecuritySettings {
    public static final boolean ENABLE_PROTECTION_REGIONS = true;
    public static final boolean REQUIRE_PERMISSIONS = false;
    public static final int PERMISSION_FALLBACK_BUILD_LEVEL = 0;
    public static final int PERMISSION_FALLBACK_ADMIN_LEVEL = 2;
    public static final boolean ENFORCE_PROTECTION_ON_UNDO = false;
    public static final boolean HIDE_INACCESSIBLE_CONTAINERS_FROM_SOURCES_SCAN = true;

    private FabricSecuritySettings() {
    }
}

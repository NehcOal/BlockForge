package com.blockforge.fabric.security;

public final class FabricSecuritySettings {
    public static final boolean ENABLE_PROTECTION_REGIONS = true;
    public static final int PERMISSION_FALLBACK_BUILD_LEVEL = 0;
    public static final int PERMISSION_FALLBACK_ADMIN_LEVEL = 2;
    public static final boolean ENFORCE_PROTECTION_ON_UNDO = false;
    public static final boolean HIDE_INACCESSIBLE_CONTAINERS_FROM_SOURCES_SCAN = true;
    private static final String REQUIRE_PERMISSIONS_PROPERTY = "blockforge.requirePermissions";
    private static final String REQUIRE_PERMISSIONS_ENV = "BLOCKFORGE_REQUIRE_PERMISSIONS";

    private FabricSecuritySettings() {
    }

    public static boolean requirePermissions() {
        String property = System.getProperty(REQUIRE_PERMISSIONS_PROPERTY);
        if (property != null && !property.isBlank()) {
            return Boolean.parseBoolean(property);
        }
        String env = System.getenv(REQUIRE_PERMISSIONS_ENV);
        return env != null && Boolean.parseBoolean(env);
    }
}

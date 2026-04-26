package com.blockforge.common.platform;

import java.util.UUID;

public interface PlatformPlayerRef {
    UUID uuid();

    String name();

    boolean isCreative();

    boolean isSpectator();

    boolean hasPermission(int level);
}

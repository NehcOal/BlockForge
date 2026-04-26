package com.blockforge.common.platform;

import java.util.Map;

public interface PlatformBlockRef {
    String blockId();

    Map<String, String> properties();
}

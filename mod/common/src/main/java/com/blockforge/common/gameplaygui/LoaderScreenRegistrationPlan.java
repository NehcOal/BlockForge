package com.blockforge.common.gameplaygui;

import java.util.List;

public record LoaderScreenRegistrationPlan(
        String loader,
        boolean commonRegistrationTouchesClientScreens,
        boolean clientInitializerOnly,
        boolean serverMenusLoadWithoutScreens,
        List<String> screenClassNames
) {
    public LoaderScreenRegistrationPlan {
        loader = loader == null || loader.isBlank() ? "unknown" : loader;
        screenClassNames = screenClassNames == null ? List.of() : List.copyOf(screenClassNames);
    }

    public boolean dedicatedServerSafe() {
        return !commonRegistrationTouchesClientScreens && serverMenusLoadWithoutScreens;
    }

    public List<String> warnings() {
        java.util.ArrayList<String> warnings = new java.util.ArrayList<>();
        if (commonRegistrationTouchesClientScreens) {
            warnings.add("Common/server registration references client screen classes.");
        }
        if (!clientInitializerOnly) {
            warnings.add("Client screens are not isolated to the client initializer/setup path.");
        }
        if (!serverMenusLoadWithoutScreens) {
            warnings.add("Server menu/screen-handler path cannot load without client screens.");
        }
        return List.copyOf(warnings);
    }
}

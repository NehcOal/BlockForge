package com.blockforge.common.house;

import java.util.List;

public record HousePlan(
        String housePlanId,
        String name,
        HouseStyle style,
        HouseFootprint footprint,
        HouseDimensions dimensions,
        HouseLayout layout,
        HouseRoof roof,
        HouseOpenings openings,
        HouseMaterials materials,
        HouseConstructionOptions options,
        List<HouseModule> modules,
        List<HouseIssue> issues
) {
    public HousePlan {
        modules = modules == null ? List.of() : List.copyOf(modules);
        issues = issues == null ? List.of() : List.copyOf(issues);
    }

    public enum HouseStyle {
        STARTER_COTTAGE,
        MEDIEVAL_HOUSE,
        FARMHOUSE,
        WATCHTOWER_HOUSE,
        STORAGE_HOUSE,
        WORKSHOP,
        MARKET_HOUSE,
        LONGHOUSE,
        MODERN_BOX,
        CUSTOM
    }

    public enum HouseFootprintShape {
        RECTANGLE,
        L_SHAPE,
        T_SHAPE,
        COURTYARD,
        CUSTOM
    }

    public enum HouseRoofType {
        FLAT,
        GABLE,
        HIP,
        PYRAMID,
        TOWER,
        SHED,
        NONE
    }

    public enum HouseRoomType {
        ENTRY,
        MAIN_ROOM,
        BEDROOM,
        STORAGE,
        WORKSHOP,
        KITCHEN,
        STAIR,
        BALCONY,
        CUSTOM
    }

    public enum HouseModuleType {
        FOUNDATION,
        FLOOR,
        WALL,
        ROOF,
        DOOR,
        WINDOW,
        STAIR,
        CHIMNEY,
        PORCH,
        BALCONY,
        TRIM,
        INTERIOR,
        DECORATION
    }

    public record HouseFootprint(int width, int depth, HouseFootprintShape shape) {
    }

    public record HouseDimensions(int floors, int floorHeight, int totalHeight, int foundationHeight) {
    }

    public record HouseRoof(HouseRoofType type, int overhang, int pitch, String mainBlockId, String trimBlockId) {
    }

    public record HouseLayout(List<HouseRoom> rooms, List<HouseConnection> connections) {
        public HouseLayout {
            rooms = rooms == null ? List.of() : List.copyOf(rooms);
            connections = connections == null ? List.of() : List.copyOf(connections);
        }
    }

    public record HouseRoom(String roomId, String name, HouseRoomType type, int x, int z, int width, int depth, int floor) {
    }

    public record HouseConnection(String fromRoomId, String toRoomId, String type) {
    }

    public record HouseOpenings(List<HouseDoor> doors, List<HouseWindow> windows) {
        public HouseOpenings {
            doors = doors == null ? List.of() : List.copyOf(doors);
            windows = windows == null ? List.of() : List.copyOf(windows);
        }
    }

    public record HouseDoor(int x, int y, int z, String facing, String blockId) {
    }

    public record HouseWindow(int x, int y, int z, int width, int height, String facing, String blockId) {
    }

    public record HouseMaterials(
            String foundationBlock,
            String wallBlock,
            String floorBlock,
            String roofBlock,
            String trimBlock,
            String windowBlock,
            String doorBlock,
            String stairBlock,
            String accentBlock
    ) {
    }

    public record HouseConstructionOptions(
            boolean buildFoundation,
            boolean buildInteriorFloor,
            boolean buildRoof,
            boolean addWindows,
            boolean addDoor,
            boolean addChimney,
            boolean addPorch,
            boolean addStairs,
            boolean hollowInterior,
            boolean survivalFriendly,
            boolean useSymmetry
    ) {
        public static HouseConstructionOptions defaults() {
            return new HouseConstructionOptions(true, true, true, true, true, false, false, true, true, true, true);
        }
    }

    public record HouseModule(
            String moduleId,
            HouseModuleType type,
            int x,
            int y,
            int z,
            int width,
            int height,
            int depth,
            String blockKey
    ) {
        public int volume() {
            return Math.max(0, width) * Math.max(0, height) * Math.max(0, depth);
        }
    }

    public record HouseIssue(String severity, String path, String message, String suggestion) {
    }
}

package com.blockforge.common.house;

import com.blockforge.common.house.HousePlan.HouseConstructionOptions;
import com.blockforge.common.house.HousePlan.HouseDimensions;
import com.blockforge.common.house.HousePlan.HouseDoor;
import com.blockforge.common.house.HousePlan.HouseFootprint;
import com.blockforge.common.house.HousePlan.HouseFootprintShape;
import com.blockforge.common.house.HousePlan.HouseIssue;
import com.blockforge.common.house.HousePlan.HouseLayout;
import com.blockforge.common.house.HousePlan.HouseMaterials;
import com.blockforge.common.house.HousePlan.HouseOpenings;
import com.blockforge.common.house.HousePlan.HouseRoof;
import com.blockforge.common.house.HousePlan.HouseRoofType;
import com.blockforge.common.house.HousePlan.HouseRoom;
import com.blockforge.common.house.HousePlan.HouseRoomType;
import com.blockforge.common.house.HousePlan.HouseStyle;
import com.blockforge.common.house.HousePlan.HouseWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class HousePlanGenerator {
    private static final int MAX_WIDTH = 32;
    private static final int MAX_DEPTH = 32;
    private static final int MAX_FLOORS = 4;
    private final HouseModulePlanner modulePlanner = new HouseModulePlanner();

    public List<HouseStyle> supportedPresets() {
        return List.of(
                HouseStyle.STARTER_COTTAGE,
                HouseStyle.MEDIEVAL_HOUSE,
                HouseStyle.FARMHOUSE,
                HouseStyle.WORKSHOP,
                HouseStyle.STORAGE_HOUSE,
                HouseStyle.WATCHTOWER_HOUSE,
                HouseStyle.MARKET_HOUSE,
                HouseStyle.LONGHOUSE
        );
    }

    public HousePlan generate(HouseGenerationRequest request) {
        HouseStyle style = request.style() == null ? HouseStyle.STARTER_COTTAGE : request.style();
        PresetDefaults defaults = defaultsFor(style);
        int width = bounded(request.width() > 0 ? request.width() : defaults.width(), 5, MAX_WIDTH);
        int depth = bounded(request.depth() > 0 ? request.depth() : defaults.depth(), 5, MAX_DEPTH);
        int floors = bounded(request.floors() > 0 ? request.floors() : defaults.floors(), 1, MAX_FLOORS);
        HouseRoofType roofType = request.roofType() == null ? defaults.roofType() : request.roofType();
        HouseMaterials materials = request.materials() == null ? defaults.materials() : request.materials();
        HouseConstructionOptions options = request.options() == null ? defaults.options() : request.options();

        List<HouseIssue> issues = validateInput(width, depth, floors, style);
        int floorHeight = style == HouseStyle.WATCHTOWER_HOUSE ? 4 : 3;
        int roofHeight = roofType == HouseRoofType.NONE || !options.buildRoof() ? 0 : (roofType == HouseRoofType.FLAT ? 1 : Math.max(2, defaults.roofPitch()));
        HouseDimensions dimensions = new HouseDimensions(floors, floorHeight, 1 + floors * floorHeight + roofHeight, 1);
        HouseFootprint footprint = new HouseFootprint(width, depth, HouseFootprintShape.RECTANGLE);
        HouseRoof roof = new HouseRoof(roofType, defaults.roofOverhang(), defaults.roofPitch(), materials.roofBlock(), materials.trimBlock());
        HouseOpenings openings = createOpenings(width, depth, floors, materials, options, style);
        HouseLayout layout = createLayout(width, depth, floors, style);

        return new HousePlan(
                "house-" + style.name().toLowerCase(Locale.ROOT),
                displayName(style),
                style,
                footprint,
                dimensions,
                layout,
                roof,
                openings,
                materials,
                options,
                modulePlanner.planModules(footprint, dimensions, roof, openings, materials, options),
                issues
        );
    }

    public HousePlan generatePreset(HouseStyle style) {
        return generate(HouseGenerationRequest.preset(style));
    }

    private List<HouseIssue> validateInput(int width, int depth, int floors, HouseStyle style) {
        List<HouseIssue> issues = new ArrayList<>();
        if (width < 5 || depth < 5) {
            issues.add(new HouseIssue("error", "footprint", "House footprint is too small.", "Use at least 5 x 5."));
        }
        if (width > MAX_WIDTH || depth > MAX_DEPTH) {
            issues.add(new HouseIssue("error", "footprint", "House footprint exceeds alpha limits.", "Use at most 32 x 32."));
        }
        if (floors > MAX_FLOORS) {
            issues.add(new HouseIssue("error", "dimensions.floors", "Too many floors for alpha generation.", "Use 4 floors or fewer."));
        }
        if (!supportedPresets().contains(style) && style != HouseStyle.MODERN_BOX && style != HouseStyle.CUSTOM) {
            issues.add(new HouseIssue("warning", "style", "Style uses generic fallback rules.", "Use one of the alpha house presets for best results."));
        }
        return issues;
    }

    private HouseOpenings createOpenings(int width, int depth, int floors, HouseMaterials materials, HouseConstructionOptions options, HouseStyle style) {
        List<HouseDoor> doors = new ArrayList<>();
        List<HouseWindow> windows = new ArrayList<>();
        if (options.addDoor()) {
            doors.add(new HouseDoor(width / 2, 1, depth - 1, "south", materials.doorBlock()));
        }
        if (options.addWindows()) {
            windows.add(new HouseWindow(2, 2, 0, 1, 1, "north", materials.windowBlock()));
            windows.add(new HouseWindow(width - 3, 2, 0, 1, 1, "north", materials.windowBlock()));
            if (style != HouseStyle.STORAGE_HOUSE) {
                windows.add(new HouseWindow(0, 2, depth / 2, 1, 1, "west", materials.windowBlock()));
                windows.add(new HouseWindow(width - 1, 2, depth / 2, 1, 1, "east", materials.windowBlock()));
            }
            if (floors > 1) {
                windows.add(new HouseWindow(width / 2, 5, 0, 1, 1, "north", materials.windowBlock()));
            }
        }
        return new HouseOpenings(doors, windows);
    }

    private HouseLayout createLayout(int width, int depth, int floors, HouseStyle style) {
        List<HouseRoom> rooms = new ArrayList<>();
        rooms.add(new HouseRoom("main", "Main Room", HouseRoomType.MAIN_ROOM, 1, 1, Math.max(3, width - 2), Math.max(3, depth - 2), 0));
        if (style == HouseStyle.FARMHOUSE || style == HouseStyle.MEDIEVAL_HOUSE) {
            rooms.add(new HouseRoom("bedroom", "Bedroom", HouseRoomType.BEDROOM, width / 2, 1, Math.max(3, width / 2 - 2), Math.max(3, depth / 2), 0));
        }
        if (style == HouseStyle.WORKSHOP) {
            rooms.add(new HouseRoom("workshop", "Workshop", HouseRoomType.WORKSHOP, 1, 1, width - 2, depth - 2, 0));
        }
        if (style == HouseStyle.STORAGE_HOUSE) {
            rooms.add(new HouseRoom("storage", "Storage", HouseRoomType.STORAGE, 1, 1, width - 2, depth - 2, 0));
        }
        if (floors > 1) {
            rooms.add(new HouseRoom("stair", "Stair", HouseRoomType.STAIR, 2, 2, 2, 2, 0));
        }
        return new HouseLayout(rooms, List.of());
    }

    private PresetDefaults defaultsFor(HouseStyle style) {
        return switch (style) {
            case MEDIEVAL_HOUSE -> new PresetDefaults(11, 9, 2, HouseRoofType.GABLE, 2, 1, medievalMaterials(), new HouseConstructionOptions(true, true, true, true, true, true, false, true, true, true, true));
            case FARMHOUSE -> new PresetDefaults(13, 11, 1, HouseRoofType.GABLE, 2, 1, farmhouseMaterials(), new HouseConstructionOptions(true, true, true, true, true, true, true, false, true, true, true));
            case WORKSHOP -> new PresetDefaults(13, 9, 1, HouseRoofType.GABLE, 2, 1, workshopMaterials(), new HouseConstructionOptions(true, true, true, true, true, false, false, false, true, true, false));
            case STORAGE_HOUSE -> new PresetDefaults(11, 9, 1, HouseRoofType.FLAT, 1, 0, storageMaterials(), new HouseConstructionOptions(true, true, true, false, true, false, false, false, true, true, true));
            case WATCHTOWER_HOUSE -> new PresetDefaults(7, 7, 4, HouseRoofType.TOWER, 3, 0, stoneMaterials(), new HouseConstructionOptions(true, true, true, true, true, false, false, true, true, true, true));
            case MARKET_HOUSE -> new PresetDefaults(11, 9, 1, HouseRoofType.SHED, 1, 1, marketMaterials(), new HouseConstructionOptions(true, true, true, true, true, false, true, false, true, true, false));
            case LONGHOUSE -> new PresetDefaults(17, 9, 1, HouseRoofType.GABLE, 2, 1, longhouseMaterials(), new HouseConstructionOptions(true, true, true, true, true, true, false, false, true, true, true));
            case MODERN_BOX -> new PresetDefaults(11, 11, 2, HouseRoofType.FLAT, 1, 0, modernMaterials(), HouseConstructionOptions.defaults());
            default -> new PresetDefaults(9, 7, 1, HouseRoofType.GABLE, 2, 1, cottageMaterials(), HouseConstructionOptions.defaults());
        };
    }

    private static HouseMaterials cottageMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:spruce_stairs", "minecraft:spruce_planks", "minecraft:glass_pane", "minecraft:oak_door", "minecraft:oak_stairs", "minecraft:lantern");
    }

    private static HouseMaterials medievalMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:oak_planks", "minecraft:spruce_planks", "minecraft:dark_oak_stairs", "minecraft:stripped_oak_log", "minecraft:glass_pane", "minecraft:spruce_door", "minecraft:spruce_stairs", "minecraft:lantern");
    }

    private static HouseMaterials farmhouseMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:stripped_oak_log", "minecraft:oak_planks", "minecraft:oak_stairs", "minecraft:fence", "minecraft:glass_pane", "minecraft:oak_door", "minecraft:oak_stairs", "minecraft:barrel");
    }

    private static HouseMaterials workshopMaterials() {
        return new HouseMaterials("minecraft:stone", "minecraft:cobblestone", "minecraft:stone", "minecraft:dark_oak_stairs", "minecraft:iron_bars", "minecraft:glass_pane", "minecraft:dark_oak_door", "minecraft:stone_stairs", "minecraft:anvil");
    }

    private static HouseMaterials storageMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:stone_bricks", "minecraft:oak_planks", "minecraft:dark_oak_planks", "minecraft:barrel", "minecraft:glass_pane", "minecraft:spruce_door", "minecraft:ladder", "minecraft:chest");
    }

    private static HouseMaterials stoneMaterials() {
        return new HouseMaterials("minecraft:stone_bricks", "minecraft:stone_bricks", "minecraft:stone", "minecraft:dark_oak_planks", "minecraft:iron_bars", "minecraft:glass_pane", "minecraft:iron_door", "minecraft:ladder", "minecraft:lantern");
    }

    private static HouseMaterials marketMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:oak_planks", "minecraft:oak_planks", "minecraft:red_wool", "minecraft:fence", "minecraft:glass_pane", "minecraft:oak_door", "minecraft:oak_stairs", "minecraft:barrel");
    }

    private static HouseMaterials longhouseMaterials() {
        return new HouseMaterials("minecraft:cobblestone", "minecraft:spruce_planks", "minecraft:spruce_planks", "minecraft:spruce_stairs", "minecraft:spruce_log", "minecraft:glass_pane", "minecraft:spruce_door", "minecraft:spruce_stairs", "minecraft:campfire");
    }

    private static HouseMaterials modernMaterials() {
        return new HouseMaterials("minecraft:smooth_stone", "minecraft:white_concrete", "minecraft:smooth_stone", "minecraft:smooth_stone", "minecraft:gray_concrete", "minecraft:glass", "minecraft:oak_door", "minecraft:stone_stairs", "minecraft:lantern");
    }

    private static String displayName(HouseStyle style) {
        return Arrays.stream(style.name().toLowerCase(Locale.ROOT).split("_"))
                .map(part -> part.substring(0, 1).toUpperCase(Locale.ROOT) + part.substring(1))
                .reduce((a, b) -> a + " " + b)
                .orElse(style.name());
    }

    private static int bounded(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private record PresetDefaults(
            int width,
            int depth,
            int floors,
            HouseRoofType roofType,
            int roofPitch,
            int roofOverhang,
            HouseMaterials materials,
            HouseConstructionOptions options
    ) {
    }
}

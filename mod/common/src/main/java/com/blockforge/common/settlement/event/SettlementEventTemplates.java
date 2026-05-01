package com.blockforge.common.settlement.event;

import java.util.List;

public final class SettlementEventTemplates {
    private SettlementEventTemplates() {
    }

    public static List<SettlementEvent> templates(String settlementId, long gameTime) {
        return List.of(
                event("repair_damaged_bridge", settlementId, SettlementEventType.BRIDGE_NEEDED, SettlementEventSeverity.HIGH, "Repair the Damaged Bridge", "A damaged crossing needs a repair contract.", List.of("stone_bridge"), -8, 12, gameTime),
                event("expand_storage", settlementId, SettlementEventType.STORAGE_OVERFLOW, SettlementEventSeverity.NORMAL, "Expand the Storage District", "Storage is strained and needs a shed or warehouse.", List.of("storage_shed"), -5, 8, gameTime),
                event("watchtower_before_nightfall", settlementId, SettlementEventType.DEFENSE_REQUEST, SettlementEventSeverity.HIGH, "Build a Watchtower Before Nightfall", "The settlement wants a defensive lookout.", List.of("watchtower"), -7, 14, gameTime),
                event("rebuild_mine_entrance", settlementId, SettlementEventType.MINE_COLLAPSE, SettlementEventSeverity.CRITICAL, "Rebuild the Mine Entrance", "A mine access project needs emergency attention.", List.of("mine_entrance"), -12, 18, gameTime),
                event("connect_road", settlementId, SettlementEventType.ROAD_CONNECTION, SettlementEventSeverity.NORMAL, "Connect the Settlement Road", "A road connection would improve logistics.", List.of("road_connection"), 4, 10, gameTime),
                event("market_stall_demand", settlementId, SettlementEventType.MARKET_DEMAND, SettlementEventSeverity.LOW, "Construct a Market Stall", "Traders are asking for a small stall.", List.of("market_stall"), 5, 8, gameTime),
                event("restore_farm_hut", settlementId, SettlementEventType.FARM_EXPANSION, SettlementEventSeverity.NORMAL, "Restore the Farm Hut", "Food production needs a farm structure.", List.of("farm_hut"), 3, 9, gameTime),
                event("reinforce_wall", settlementId, SettlementEventType.DEFENSE_REQUEST, SettlementEventSeverity.HIGH, "Reinforce the Wall Segment", "A defensive wall stage is ready.", List.of("wall_segment"), -6, 14, gameTime),
                event("festival_decoration", settlementId, SettlementEventType.CELEBRATION_BUILD, SettlementEventSeverity.LOW, "Prepare a Festival Decoration", "Morale would improve with a decoration.", List.of("small_shrine"), 8, 7, gameTime),
                event("dock_for_traders", settlementId, SettlementEventType.MARKET_DEMAND, SettlementEventSeverity.NORMAL, "Build a Dock for Traders", "Water traders need a dock.", List.of("dock"), 5, 11, gameTime),
                event("morale_fountain", settlementId, SettlementEventType.CELEBRATION_BUILD, SettlementEventSeverity.LOW, "Add a Fountain to Raise Morale", "A fountain would raise culture.", List.of("garden_fountain"), 9, 8, gameTime),
                event("emergency_shelter", settlementId, SettlementEventType.RESOURCE_SHORTAGE, SettlementEventSeverity.CRITICAL, "Build Emergency Shelter", "A critical shortage requires a quick shelter.", List.of("starter_cottage"), -10, 16, gameTime)
        );
    }

    private static SettlementEvent event(String id, String settlementId, SettlementEventType type, SettlementEventSeverity severity, String title, String description, List<String> contracts, int stabilityImpact, int reputationImpact, long gameTime) {
        return new SettlementEvent(id, settlementId, type, severity, SettlementEventStatus.ACTIVE, title, description, contracts, List.of(), stabilityImpact, reputationImpact, gameTime, gameTime + 24000L, 0, List.of());
    }
}

package com.blockforge.common.settlement.project;

import java.util.List;

public final class ProjectChainTemplates {
    private ProjectChainTemplates() {
    }

    public static List<SettlementProject> templates(String settlementId, long gameTime) {
        return List.of(
                project("starter_settlement", settlementId, "Starter Settlement", ProjectType.STARTER_DISTRICT, gameTime, List.of(
                        stage("starter_cottage", "Build Starter Cottage", "starter_cottage", 0),
                        stage("storage_shed", "Build Storage Shed", "storage_shed", 20),
                        stage("farm_hut", "Build Farm Hut", "farm_hut", 40)
                ), List.of("builder_station_access")),
                project("defensive_outpost", settlementId, "Defensive Outpost", ProjectType.DEFENSE_LINE, gameTime, List.of(
                        stage("watchtower", "Build Watchtower", "watchtower", 50),
                        stage("wall_segment", "Build Wall Segment", "wall_segment", 100),
                        stage("gatehouse", "Build Gatehouse", "gatehouse", 160)
                ), List.of("hard_contracts")),
                project("market_district", settlementId, "Market District", ProjectType.MARKET_DISTRICT, gameTime, List.of(
                        stage("market_stall", "Build Market Stall", "market_stall", 40),
                        stage("road_connection", "Build Road Connection", "dock", 80),
                        stage("fountain", "Build Fountain", "garden_fountain", 120)
                ), List.of("larger_build_limit")),
                project("mining_camp", settlementId, "Mining Camp", ProjectType.MINING_DISTRICT, gameTime, List.of(
                        stage("mine_entrance", "Build Mine Entrance", "mine_entrance", 40),
                        stage("storage_depot", "Build Storage Depot", "storage_shed", 90),
                        stage("watchtower", "Build Watchtower", "watchtower", 140)
                ), List.of("material_network_access"))
        );
    }

    private static SettlementProject project(String id, String settlementId, String title, ProjectType type, long gameTime, List<ProjectStage> stages, List<String> unlocks) {
        return new SettlementProject(id, settlementId, title, "Alpha settlement project chain.", type, ProjectStatus.AVAILABLE, stages, 0, stages.stream().map(ProjectStage::requiredContractTemplateId).toList(), unlocks, gameTime, 0);
    }

    private static ProjectStage stage(String id, String title, String contractId, int reputation) {
        return new ProjectStage(id, title, "Complete contract " + contractId + " to advance this project.", contractId, reputation, false, List.of("project_progress"));
    }
}

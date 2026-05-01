package com.blockforge.common.settlement.project;

public class ProjectChainProgressor {
    public SettlementProject activate(SettlementProject project, int settlementReputation) {
        ProjectStage stage = project.currentStage();
        if (stage != null && settlementReputation < stage.requiredReputation()) {
            return project;
        }
        return project.activate();
    }

    public SettlementProject completeContractStage(SettlementProject project, String completedContractTemplateId, long gameTime) {
        return project.completeStage(completedContractTemplateId, gameTime);
    }
}

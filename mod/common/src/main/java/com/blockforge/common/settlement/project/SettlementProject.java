package com.blockforge.common.settlement.project;

import java.util.ArrayList;
import java.util.List;

public record SettlementProject(
        String projectId,
        String settlementId,
        String title,
        String description,
        ProjectType type,
        ProjectStatus status,
        List<ProjectStage> stages,
        int currentStageIndex,
        List<String> requiredContractIds,
        List<String> unlockedFeatureIds,
        long createdAtGameTime,
        long completedAtGameTime
) {
    public SettlementProject {
        if (projectId == null || projectId.isBlank()) {
            throw new IllegalArgumentException("projectId is required");
        }
        if (settlementId == null || settlementId.isBlank()) {
            throw new IllegalArgumentException("settlementId is required");
        }
        title = title == null ? projectId : title;
        description = description == null ? "" : description;
        type = type == null ? ProjectType.CUSTOM : type;
        status = status == null ? ProjectStatus.AVAILABLE : status;
        stages = stages == null ? List.of() : List.copyOf(stages);
        currentStageIndex = Math.max(0, Math.min(currentStageIndex, Math.max(0, stages.size() - 1)));
        requiredContractIds = requiredContractIds == null ? List.of() : List.copyOf(requiredContractIds);
        unlockedFeatureIds = unlockedFeatureIds == null ? List.of() : List.copyOf(unlockedFeatureIds);
    }

    public ProjectStage currentStage() {
        if (stages.isEmpty()) {
            return null;
        }
        return stages.get(currentStageIndex);
    }

    public SettlementProject activate() {
        return new SettlementProject(projectId, settlementId, title, description, type, ProjectStatus.ACTIVE, stages, currentStageIndex, requiredContractIds, unlockedFeatureIds, createdAtGameTime, completedAtGameTime);
    }

    public SettlementProject completeStage(String contractTemplateId, long gameTime) {
        if (status != ProjectStatus.ACTIVE || stages.isEmpty()) {
            return this;
        }
        ProjectStage stage = currentStage();
        if (stage == null || stage.completed() || !stage.requiredContractTemplateId().equals(contractTemplateId)) {
            return this;
        }
        List<ProjectStage> nextStages = new ArrayList<>(stages);
        nextStages.set(currentStageIndex, stage.complete());
        int nextIndex = Math.min(currentStageIndex + 1, nextStages.size() - 1);
        boolean complete = nextStages.stream().allMatch(ProjectStage::completed);
        return new SettlementProject(projectId, settlementId, title, description, type, complete ? ProjectStatus.COMPLETED : ProjectStatus.ACTIVE, nextStages, nextIndex, requiredContractIds, unlockedFeatureIds, createdAtGameTime, complete ? gameTime : completedAtGameTime);
    }
}

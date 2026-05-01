package com.blockforge.common.settlement.project;

import java.util.List;

public record ProjectStage(
        String stageId,
        String title,
        String description,
        String requiredContractTemplateId,
        int requiredReputation,
        boolean completed,
        List<String> rewards
) {
    public ProjectStage {
        if (stageId == null || stageId.isBlank()) {
            throw new IllegalArgumentException("stageId is required");
        }
        title = title == null ? stageId : title;
        description = description == null ? "" : description;
        requiredContractTemplateId = requiredContractTemplateId == null ? "" : requiredContractTemplateId;
        requiredReputation = Math.max(0, requiredReputation);
        rewards = rewards == null ? List.of() : List.copyOf(rewards);
    }

    public ProjectStage complete() {
        return new ProjectStage(stageId, title, description, requiredContractTemplateId, requiredReputation, true, rewards);
    }
}

package com.blockforge.connector.settlement;

import com.blockforge.common.contracts.ContractTemplates;
import com.blockforge.common.settlement.project.ProjectChainProgressor;
import com.blockforge.common.settlement.project.ProjectChainTemplates;
import com.blockforge.common.settlement.project.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectChainTest {
    @Test
    void projectStageAdvancesAfterContractCompletion() {
        var project = ProjectChainTemplates.templates("settlement-1", 100).stream()
                .filter(candidate -> candidate.projectId().equals("starter_settlement"))
                .findFirst()
                .orElseThrow()
                .activate();

        var next = new ProjectChainProgressor().completeContractStage(project, "starter_cottage", 200);

        assertTrue(next.stages().get(0).completed());
        assertEquals(1, next.currentStageIndex());
        assertEquals(ProjectStatus.ACTIVE, next.status());
    }

    @Test
    void projectCompletionGivesCompletedStatus() {
        var progressor = new ProjectChainProgressor();
        var project = ProjectChainTemplates.templates("settlement-1", 100).stream()
                .filter(candidate -> candidate.projectId().equals("starter_settlement"))
                .findFirst()
                .orElseThrow()
                .activate();

        project = progressor.completeContractStage(project, "starter_cottage", 200);
        project = progressor.completeContractStage(project, "storage_shed", 300);
        project = progressor.completeContractStage(project, "farm_hut", 400);

        assertEquals(ProjectStatus.COMPLETED, project.status());
        assertEquals(400, project.completedAtGameTime());
    }

    @Test
    void projectActivationRequiresCurrentStageReputation() {
        var project = ProjectChainTemplates.templates("settlement-1", 100).stream()
                .filter(candidate -> candidate.projectId().equals("defensive_outpost"))
                .findFirst()
                .orElseThrow();

        var blocked = new ProjectChainProgressor().activate(project, 0);

        assertEquals(ProjectStatus.AVAILABLE, blocked.status());
    }

    @Test
    void inactiveProjectsDoNotAdvanceFromContractCompletion() {
        var project = ProjectChainTemplates.templates("settlement-1", 100).stream()
                .filter(candidate -> candidate.projectId().equals("starter_settlement"))
                .findFirst()
                .orElseThrow();

        var next = new ProjectChainProgressor().completeContractStage(project, "starter_cottage", 200);

        assertEquals(ProjectStatus.AVAILABLE, next.status());
        assertFalse(next.stages().get(0).completed());
        assertEquals(0, next.currentStageIndex());
    }

    @Test
    void defensiveOutpostGatehouseUsesGatehouseContract() {
        var defensive = ProjectChainTemplates.templates("settlement-1", 100).stream()
                .filter(candidate -> candidate.projectId().equals("defensive_outpost"))
                .findFirst()
                .orElseThrow();

        assertEquals("gatehouse", defensive.stages().get(2).requiredContractTemplateId());
    }

    @Test
    void projectStageContractsExistInContractTemplates() {
        Set<String> templateIds = ContractTemplates.templates().stream()
                .map(template -> template.contractId())
                .collect(Collectors.toSet());

        for (var project : ProjectChainTemplates.templates("settlement-1", 100)) {
            for (var stage : project.stages()) {
                assertTrue(templateIds.contains(stage.requiredContractTemplateId()), stage.requiredContractTemplateId());
            }
        }
    }
}

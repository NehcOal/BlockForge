package com.blockforge.connector.settlement;

import com.blockforge.common.settlement.Settlement;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettlementTest {
    @Test
    void createsSettlementAndManagesMembers() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();
        Settlement settlement = Settlement.create("settlement-1", "Harbor", owner, "minecraft:overworld", 1, 64, 2, 10);

        Settlement withMember = settlement.addMember(member, 20);

        assertTrue(withMember.hasMember(owner));
        assertTrue(withMember.hasMember(member));
        assertEquals(20, withMember.updatedAtGameTime());
        assertFalse(withMember.removeMember(member, 30).hasMember(member));
        assertThrows(IllegalArgumentException.class, () -> withMember.removeMember(owner, 40));
    }

    @Test
    void movesContractsFromActiveToCompleted() {
        Settlement settlement = Settlement.create("settlement-1", "Harbor", UUID.randomUUID(), "minecraft:overworld", 0, 64, 0, 0)
                .acceptContract("starter_cottage", 10)
                .completeContract("starter_cottage", 12, 20);

        assertEquals(12, settlement.reputation());
        assertEquals(1, settlement.completedContracts());
        assertTrue(settlement.completedContractIds().contains("starter_cottage"));
        assertFalse(settlement.activeContractIds().contains("starter_cottage"));
    }

    @Test
    void completingSameContractAgainDoesNotIncreaseProgress() {
        Settlement settlement = Settlement.create("settlement-1", "Harbor", UUID.randomUUID(), "minecraft:overworld", 0, 64, 0, 0)
                .acceptContract("starter_cottage", 10)
                .completeContract("starter_cottage", 12, 20);

        Settlement completedAgain = settlement.completeContract("starter_cottage", 12, 30);

        assertEquals(settlement.reputation(), completedAgain.reputation());
        assertEquals(settlement.completedContracts(), completedAgain.completedContracts());
        assertEquals(settlement.completedContractIds(), completedAgain.completedContractIds());
    }
}

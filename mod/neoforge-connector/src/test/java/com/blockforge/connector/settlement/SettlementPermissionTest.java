package com.blockforge.connector.settlement;

import com.blockforge.common.settlement.SettlementPermission;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SettlementPermissionTest {
    @Test
    void ownerCanManageMembersAndMemberCannot() {
        UUID owner = UUID.randomUUID();
        UUID member = UUID.randomUUID();

        assertTrue(SettlementPermission.owner(owner, "s1").canManageMembers());
        assertFalse(SettlementPermission.member(member, "s1").canManageMembers());
        assertTrue(SettlementPermission.member(member, "s1").canAcceptContracts());
    }
}

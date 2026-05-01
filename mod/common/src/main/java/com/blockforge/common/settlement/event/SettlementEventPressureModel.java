package com.blockforge.common.settlement.event;

public class SettlementEventPressureModel {
    public int negativePressure(SettlementStability stability) {
        int pressure = 0;
        if (stability.stability() < 30) pressure += 30 - stability.stability();
        if (stability.safety() < 30) pressure += 30 - stability.safety();
        if (stability.logistics() < 30) pressure += 30 - stability.logistics();
        pressure += stability.maintenanceDebt() / 2;
        return pressure;
    }

    public boolean prefersPositiveEvents(SettlementStability stability) {
        return stability.stability() >= 70 && stability.prosperity() >= 60;
    }
}

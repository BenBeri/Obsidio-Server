package com.benberi.cadesim.server.model.vessel.impl;


import com.benberi.cadesim.server.model.vessel.CannonType;
import com.benberi.cadesim.server.model.vessel.Vessel;

public class WarFrigate extends Vessel {

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public int getMaxCannons() {
        return 24;
    }

    @Override
    public boolean isDualCannon() {
        return true;
    }

    @Override
    public boolean isManuaver() {
        return true;
    }

    @Override
    public double getMaxDamage() {
        return 33.32;
    }

    @Override
    public CannonType getCannonType() {
        return CannonType.LARGE;
    }
}

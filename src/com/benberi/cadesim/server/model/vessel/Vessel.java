package com.benberi.cadesim.server.model.vessel;

import com.benberi.cadesim.server.model.vessel.impl.WarFrigate;

/**
 * Abstraction of vessel
 */
public abstract class Vessel {

    /**
     * The damage of the vessel
     */
    private int damage;

    /**
     * The bilge of the vessel
     */
    private int bilge;

    /**
     * The level of the jobbers
     */
    private int jobbersQuality;

    /**
     * The ID of the vessel type
     */
    public abstract int getID();

    public static final Vessel createVesselByType(int type) {
        switch (type) {
            default:
            case 0:
                return new WarFrigate();
        }
    }

    public void setJobbersQuality(int jobbersQuality) {
        this.jobbersQuality = jobbersQuality;
    }
}

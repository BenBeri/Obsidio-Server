package com.benberi.cadesim.server.model.vessel;

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
     * The ID of the vessel type
     */
    public abstract int getID();
}

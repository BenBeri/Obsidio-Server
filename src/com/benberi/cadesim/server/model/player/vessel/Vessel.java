package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.player.vessel.impl.WarFrigate;

/**
 * Abstraction of vessel
 */
public abstract class Vessel {

    /**
     * The damage of the vessel
     */
    private double damage;

    /**
     * The bilge of the vessel
     */
    private double bilge;

    /**
     * The level of the jobbers
     */
    private int jobbersQuality;

    /**
     * The select moves handler
     */
    private TurnMoveHandler moves;

    /**
     * Appends damage
     *
     * @param damage    The damage amount to append
     */
    public void appendDamage(double damage) {
        this.damage += damage;
        if (damage > getMaxDamage()) {
            this.damage = getMaxDamage();
        }
    }

    public void appendBilge(double bilge) {
        this.bilge += bilge;
        if (bilge > 100) {
            this.bilge = 100;
        }
    }
    /**
     * Gets the bilge
     *
     * @return  The bilge
     */
    public double getBilge() {
        return this.bilge;
    }

    /**
     * Gets the damage
     *
     * @return  The damage
     */
    public double getDamage() {
        return this.damage;
    }

    public int getDamagePercentage() {
        double percentage = damage / getMaxDamage() * 100;
        return (int) percentage;
    }

    public void setJobbersQuality(int jobbersQuality) {
        this.jobbersQuality = jobbersQuality;
    }

    /**
     * @return The maximum amount of filled cannons allowed
     */
    public abstract int getMaxCannons();

    /**
     * @return If the vessel is dual-cannon shoot per turn
     */
    public abstract boolean isDualCannon();

    /**
     * @return If the vessel is 3-move only
     */
    public abstract boolean isManuaver();

    /**
     * @return  The maximum damage
     */
    public abstract double getMaxDamage();

    public abstract double getRamDamage();

    /**
     * @return The cannon type
     */
    public abstract CannonType getCannonType();

    /**
     * The ID of the vessel type
     */
    public abstract int getID();

    /**
     * Gets the size of the ship
     * @return  The size
     */
    public abstract int getSize();

    /**
     * Creates a vessel by given vessel type
     * @param type  The vessel type
     * @return The created vessel
     */
    public static Vessel createVesselByType(int type) {
        switch (type) {
            default:
            case 0:
                return new WarFrigate();
        }
    }

    public int getBilgePercentage() {
        return (int) (bilge / 100 * 100);
    }

    public void decreaseDamage(double rate) {
        damage -= rate;
        if (damage < 0) {
            damage = 0;
        }
    }

    public void decreaseBilge(double rate) {
        bilge -= rate;
        if (bilge < 0) {
            bilge = 0;
        }
    }

    public boolean isDamageMaxed() {
        return damage >= getMaxDamage();
    }

    public void resetDamageAndBilge() {
        damage = 0;
        bilge = 0;
    }
}

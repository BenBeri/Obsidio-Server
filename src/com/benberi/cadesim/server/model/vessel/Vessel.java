package com.benberi.cadesim.server.model.vessel;

import com.benberi.cadesim.server.model.move.MoveType;
import com.benberi.cadesim.server.model.move.TurnMoveHandler;
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
     * The select moves handler
     */
    private TurnMoveHandler moves = new TurnMoveHandler();

    /**
     * The ID of the vessel type
     */
    public abstract int getID();

    public TurnMoveHandler getMoves() {
        return moves;
    }

    public static Vessel createVesselByType(int type) {
        switch (type) {
            default:
            case 0:
                return new WarFrigate();
        }
    }

    public int getAnimationTime() {
        int time = 0;
        for(MoveType type : moves.getMoves()) {
            if (type != MoveType.NONE) {
                time += 600;
            }
        }

        return time;
    }

    public int getBilge() {
        return this.bilge;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setJobbersQuality(int jobbersQuality) {
        this.jobbersQuality = jobbersQuality;
    }

    public abstract int getMaxCannons();
    public abstract boolean isDualCannon();
}

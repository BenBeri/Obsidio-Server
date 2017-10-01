package com.benberi.cadesim.server.model.player.vessel;

public enum CannonType {
    SMALL(0.5),
    MEDIUM(1),
   LARGE(1.3328);

    /**
     * The damage the cannon deals
     */
    private double damage;

    CannonType(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }
}

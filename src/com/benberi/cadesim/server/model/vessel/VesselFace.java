package com.benberi.cadesim.server.model.vessel;

public enum VesselFace {

    NORTH(14),
    SOUTH(6),
    WEST(10),
    EAST(2);

    private int directionId;

    VesselFace(int dir) {
        this.directionId = dir;
    }

    public int getDirectionId() {
        return this.directionId;
    }
}

package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.move.MoveType;

public enum VesselMovementAnimation {
    NO_ANIMATION(-1),
    TURN_LEFT(0),
    TURN_RIGHT(1),
    MOVE_FORWARD(2),
    MOVE_BACKWARD(3),
    MOVE_LEFT(4),
    MOVE_RIGHT(5),
    TURN_LEFT_BUMP(6),
    TURN_RIGHT_BUMP(7),
    MOVE_FORWARD_BUMP(8);

    private int id;

    VesselMovementAnimation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        switch (this) {
            case NO_ANIMATION:
                return 0;
            case TURN_LEFT:
            case TURN_RIGHT:
            case MOVE_FORWARD:
            case MOVE_LEFT:
            case MOVE_RIGHT:
                return 500;
            case MOVE_FORWARD_BUMP:
            case TURN_LEFT_BUMP:
            case TURN_RIGHT_BUMP:
                return 250;
        }

        return 0;
    }

    public static VesselMovementAnimation getIdForMoveType(MoveType type) {
        switch (type) {
            case LEFT:
                return TURN_LEFT;
            case RIGHT:
                return TURN_RIGHT;
            case NONE:
                return NO_ANIMATION;
            default:
            case FORWARD:
                return MOVE_FORWARD;
        }
    }
}

package com.benberi.cadesim.server.model.player.vessel;

import com.benberi.cadesim.server.model.player.move.MoveType;

public enum VesselMovementAnimation {
    NO_ANIMATION(-1),
    TURN_LEFT(0),
    TURN_RIGHT(1),
    MOVE_FORWARD(2),
    MOVE_BACKWARD(3),
    MOVE_LEFT(4),
    MOVE_RIGHT(5),
    BUMP_PHASE_1(6),
    BUMP_PHASE_2(7);

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
        }

        return 0;
    }

    public static VesselMovementAnimation getBumpForPhase(int phase) {
        switch (phase) {
            case 0:
                return BUMP_PHASE_1;
            case 1:
                return BUMP_PHASE_2;
        }

        return null;
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

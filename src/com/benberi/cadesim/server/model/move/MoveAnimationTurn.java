package com.benberi.cadesim.server.model.move;

import com.benberi.cadesim.server.model.vessel.VesselMovementAnimation;

public class MoveAnimationTurn {

    private VesselMovementAnimation animation = VesselMovementAnimation.NO_ANIMATION; // phase 1
    private VesselMovementAnimation subAnimation = VesselMovementAnimation.NO_ANIMATION; // phase 2
    private int leftShoots;
    private int rightShoots;

    public VesselMovementAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(VesselMovementAnimation animation) {
        this.animation = animation;
    }

    public VesselMovementAnimation getSubAnimation() {
        return subAnimation;
    }

    public void setSubAnimation(VesselMovementAnimation subAnimation) {
        this.subAnimation = subAnimation;
    }

    public int getLeftShoots() {
        return leftShoots;
    }

    public void setLeftShoots(int leftShoots) {
        this.leftShoots = leftShoots;
    }

    public int getRightShoots() {
        return rightShoots;
    }

    public void setRightShoots(int rightShoots) {
        this.rightShoots = rightShoots;
    }

}

package com.benberi.cadesim.server.model.move;

import com.benberi.cadesim.server.model.vessel.Vessel;

public class MoveTokensHandler {

    /**
     * Left move tokens
     */
    private int left = 100;

    /**
     * Forward move tokens
     */
    private int forward = 100;

    /**
     * Right move tokens
     */
    private int right = 100;

    /**
     * Cannons
     */
    private int cannons;

    /**
     * The owner vessel
     */
    private Vessel vessel;

    public MoveTokensHandler(Vessel vessel) {
        this.vessel = vessel;
    }


    /**
     * Adds a left move token
     * @param toAdd The amount of tokens to add
     */
    public void addLeft(int toAdd) {
        left += toAdd;
    }

    /**
     * Adds a forward move token
     * @param toAdd The amount of tokens to add
     */
    public void addForward(int toAdd) {
        forward += toAdd;
    }

    /**
     * Adds a right move token
     * @param toAdd The amount of tokens to add
     */
    public void addRight(int toAdd) {
        right += toAdd;
    }

    /**
     * Removes a right move token
     * @param toRemove  The amount to remove
     */
    public void removeRight(int toRemove) {
        right -= toRemove;
        if (right < 0) {
            right = 0;
        }
    }

    /**
     * Removes a left move token
     * @param toRemove  The amount to remove
     */
    public void removeLeft(int toRemove) {
        left -= toRemove;
        if (left < 0) {
            left = 0;
        }
    }

    /**
     * Removes a forward move token
     * @param toRemove  The amount to remove
     */
    public void removeForward(int toRemove) {
       forward -= toRemove;
        if (forward < 0) {
            forward = 0;
        }
    }

    /**
     * Adds a cannon token
     * @param toAdd The amount to add
     */
    public void addCannons(int toAdd) {
        cannons += toAdd;
        if (cannons > vessel.getMaxCannons()) {
            cannons = vessel.getMaxCannons();
        }
    }

    public void removeCannons(int toRemove) {
        cannons -= toRemove;
        if (cannons < 0) {
            cannons = 0;
        }
    }

    public int getLeft() {
        return left;
    }

    public int getForward() {
        return forward;
    }

    public int getRight() {
        return right;
    }

    public int getCannons() {
        return cannons;
    }

    public boolean useTokenForMove(MoveType moveType) {
        if (moveType == MoveType.NONE) {
            return true;
        }
        switch (moveType) {
            case LEFT:
                if (left > 0) {
                    left--;
                    return  true;
                }
                break;
            case RIGHT:
                if (right > 0) {
                    right--;
                    return  true;
                }
                break;
            case FORWARD:
                if (forward > 0) {
                    forward--;
                    return  true;
                }
                break;
        }
        return  false;
    }

    public void addToken(MoveType move, int amount) {
        switch (move) {
            case LEFT:
                left += amount;
                break;
            case RIGHT:
                right += amount;
                break;
            case FORWARD:
                forward += amount;
                break;
        }
    }
}

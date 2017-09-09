package com.benberi.cadesim.server.model.move;

public class TurnMoveHandler {

    /**
     * The moves
     */
    private MoveType[] moves = new MoveType[4];

    /**
     * Left cannons
     */
    private int[] leftCannons = new int[4];

    /**
     * Right cannons
     */
    private int[] rightCannons = new int[4];

    public TurnMoveHandler() {
        resetTurn();
    }

    public MoveType getMove(int slot) {
        return moves[slot];
    }

    public int getLeftCannons(int slot) {
        return leftCannons[slot];
    }

    public int getRightCannons(int slot) {
        return rightCannons[slot];
    }

    public void setMove(int slot, MoveType move) {
        moves[slot] = move;
    }

    public void setLeftCannons(int slot, int amount) {
        this.leftCannons[slot] = amount;
    }

    public void setRightCannons(int slot, int amount) {
        this.rightCannons[slot] = amount;
    }

    public MoveType[] getMoves() {
        return moves;
    }

    public int[] getLeftCannons() {
        return leftCannons;
    }

    public int[] getRightCannons() {
        return rightCannons;
    }

    public void resetTurn() {
        for(int i = 0; i < moves.length; i++) {
            moves[i] = MoveType.NONE;
            leftCannons[i] = 0;
            rightCannons[i] = 0;
        }
    }
}

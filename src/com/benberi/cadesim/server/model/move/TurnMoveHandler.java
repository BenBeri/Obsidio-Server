package com.benberi.cadesim.server.model.move;

import com.benberi.cadesim.server.model.Player;

public class TurnMoveHandler {

    private Player player;

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

    private int manuaverSlot = 3;

    public TurnMoveHandler(Player p)
    {
        this.player = p;
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

    public void setManuaverSlot(int manuaverSlot) {
        int prevSlot = this.manuaverSlot;
        System.out.println("prev: " + prevSlot);
        this.manuaverSlot = manuaverSlot;
        System.out.println("new: " + manuaverSlot);
        MoveType move = getMove(manuaverSlot);
        if (move != MoveType.NONE) {
            setMove(prevSlot, move);
            setMove(manuaverSlot, MoveType.NONE);
            player.sendMovePlaceVerification(manuaverSlot, MoveType.NONE.getId());
            player.sendMovePlaceVerification(prevSlot, move.getId());
        }
    }

    public int getManuaverSlot() {
        return manuaverSlot;
    }
}

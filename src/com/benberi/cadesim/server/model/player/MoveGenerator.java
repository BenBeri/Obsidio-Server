package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.Constants;

public class MoveGenerator {

    /**
     * The player
     */
    private Player player;

    private double cannonGenerationPercentage;

    private double moveGenerationPercentage;

    public MoveGenerator(Player p) {
        this.player = p;
    }

    public void update() {
        if (player.getMoveTokens().getCannons() + player.getMoves().countAllShoots() < 24) {
            updateCannonGeneration();
        }
        updateMoveGeneration();
    }

    private void updateMoveGeneration() {
        double movesPerTurn = player.getJobbersQuality().getMovesPerTurn();
        double movesPerTurnAffectBilge = movesPerTurn * 1 - (((player.getVessel().getBilgePercentage() * 1.45) / 100) * movesPerTurn);
        if (movesPerTurnAffectBilge == 0) {
            movesPerTurnAffectBilge = player.getJobbersQuality().getFullBilgeMoveRate();
        }

        double rate = movesPerTurnAffectBilge / Constants.TURN_TIME;

        moveGenerationPercentage += rate;
        if (moveGenerationPercentage >= 1) {
            moveGenerationPercentage -= Math.floor(moveGenerationPercentage);
            player.getMoveTokens().moveGenerated();
            player.getPackets().sendTokens();
        }
    }

    private void updateCannonGeneration() {
        double rate = player.getJobbersQuality().getCannonsPerTurn() / Constants.TURN_TIME;
        cannonGenerationPercentage += rate;
        if (cannonGenerationPercentage >= 1) {
            cannonGenerationPercentage -= Math.floor(cannonGenerationPercentage);
            player.getMoveTokens().addCannons(1);
            player.getPackets().sendTokens();
        }
    }
}
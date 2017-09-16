package com.benberi.cadesim.server.model.player;

public enum JobbersQuality {

    BASIC(0.5, 4, 40, 1.7, 11.5, 0.15),
    ELITE(1, 7, 50, 2.4, 17, 0.3);

    /**
     *  The fix amount per turn
     */
    private double fixRate;

    /**
     * The fix bilge amount per turn
     */
    private double bilgeFixRate;

    /**
     * Minimum damage for bilge to increase
     */
    private double minBilgeForDamamge;

    /**
     * Move generation per turn
     */
    private double movesPerTurn;

    /**
     * Cannon generation per turn
     */
    private double cannonsPerTurn;

    private double fullBilgeMoveRate;

    JobbersQuality(double fixRate, double bilgeFixRate, double minBilgeForDamage,  double movesPerTurn, double cannonsPerTurn, double fullBilgeMoveRate) {
        this.fixRate = fixRate;
        this.bilgeFixRate = bilgeFixRate;
        this.minBilgeForDamamge = minBilgeForDamage;
        this.movesPerTurn = movesPerTurn;
        this.cannonsPerTurn = cannonsPerTurn;
        this.fullBilgeMoveRate = fullBilgeMoveRate;
    }

    public double getFixRate() {
        return fixRate;
    }

    public double getCannonsPerTurn() {
        return this.cannonsPerTurn;
    }

    public double getBilgeFixRate() {
        return bilgeFixRate;
    }

    public double getMinDamageForBilge() {
        return minBilgeForDamamge;
    }

    public double getFullBilgeMoveRate() {
        return fullBilgeMoveRate;
    }

    public double getMovesPerTurn() {
        return movesPerTurn;
    }
}

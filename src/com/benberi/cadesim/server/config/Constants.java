package com.benberi.cadesim.server.config;

public class Constants {

    /**
     * The port the cade sim will use
     */
    public static final int PROTOCOL_DEFAULT_PORT = 80;

    /**
     * The delay of the main game service loop in milliseconds
     */
    public static final int SERVICE_LOOP_DELAY = 100; // ms

    /**
     * The main blockade time
     */
    public static final int BLOCKADE_TIME = 9000; // sec*10

    /**
     * The time it takes for a turn to select moves
     */
    public static final int TURN_TIME = 180; // sec*10

    /**
     * If to debug packets or not
     */
    public static final boolean DEBUG_PACKETS = false;

    public static final int DEFAULT_VESSEL_TYPE = 0;

    public static final double BILGE_INCREASE_RATE_PER_TICK = 0.1;
    public static final int TOKEN_LIFE = 4; // turns
}

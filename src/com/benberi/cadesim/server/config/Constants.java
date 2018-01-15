package com.benberi.cadesim.server.config;

public class Constants {

    /**
     * The protocol verison to allow connections from (client-version)
     */
    public static final int PROTOCOL_VERSION = 7;

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
    public static final int TURN_TIME = 150; // sec*10

    /**
     * If to debug packets or not
     */
    public static final boolean DEBUG_PACKETS = false;

    public static final int DEFAULT_VESSEL_TYPE = 0;

    /**
     * Bilge increasing rate after X damage
     */
    public static final double BILGE_INCREASE_RATE_PER_TICK = 0.1;

    /**
     * The tokens life after generating
     */
    public static final int TOKEN_LIFE = 4; // turns

    /**
     * This is used to timeout players that did not notify the server about their animation finish for the given
     * timeout value
     */
    public static final int TURN_FINISH_TIMEOUT = 30;

    public static final int TURN_EXTRA_TIME = 13;
    public static final int OUTGOING_PACKETS_PLAYER_PER_TICK = 100;
    public static final int INCOMING_PACKETS_PLAYER_PER_TICK = 100;
}

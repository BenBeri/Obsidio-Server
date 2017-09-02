package com.benberi.cadesim.server;

public class Constants {

    /**
     * The port the cade sim will use
     */
    public static final int PROTOCOL_DEFAULT_PORT = 4666;

    /**
     * The delay of the main game service loop in milliseconds
     */
    public static final int SERVICE_LOOP_DELAY = 1000; // ms

    /**
     * The main blockade time
     */
    public static final int BLOCKADE_TIME = 2700; // sec

    /**
     * The time it takes for a turn to select moves
     */
    public static final int TURN_TIME = 35; // sec

    /**
     * If to debug packets or not
     */
    public static final boolean DEBUG_PACKETS = true;
}

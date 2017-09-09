package com.benberi.cadesim.server;

public class Constants {

    /**
     * The port the cade sim will use
     */
    public static final int PROTOCOL_DEFAULT_PORT = 4666;

    /**
     * The delay of the main game service loop in milliseconds
     */
    public static final int SERVICE_LOOP_DELAY = 100; // ms

    /**
     * The main blockade time
     */
    public static final int BLOCKADE_TIME = 27000; // sec*10

    /**
     * The time it takes for a turn to select moves
     */
    public static final int TURN_TIME = 350; // sec*10

    /**
     * If to debug packets or not
     */
    public static final boolean DEBUG_PACKETS = true;

    public static final int DEFAULT_VESSEL_TYPE = 0;
}

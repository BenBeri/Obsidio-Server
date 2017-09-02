package com.benberi.cadesim.server.service;

import com.benberi.cadesim.server.ServerContext;

/**
 * This is the "heartbeat" main loop of the game server
 */
public class GameService implements Runnable {

    /**
     * The server context
     */
    private ServerContext context;

    public GameService(ServerContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        context.getTimeMachine().tick();
        context.getPlayerManager().tick();
    }
}

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
        try {
            long start = System.currentTimeMillis();
            context.getPackets().queuePackets();
            context.getTimeMachine().tick();
            context.getPlayerManager().tick();
            context.getPlayerManager().queueOutgoing();
            long end = System.currentTimeMillis() - start;
        } catch (Exception e) {
            e.printStackTrace();
            ServerContext.log(e.getMessage());
        }
    }
}

package com.benberi.cadesim.server;

import com.benberi.cadesim.server.model.PlayerManager;

public class ServerContext {

    /**
     * The server instance
     */
    private CadeServer server;

    /**
     * The player manager
     */
    private PlayerManager players;

    public ServerContext(CadeServer server) {
        this.server = server;
        this.players = new PlayerManager(this);
    }

    /**
     * Gets the player manager
     * @return {@link #players}
     */
    public PlayerManager getPlayerManager() {
        return players;
    }
}

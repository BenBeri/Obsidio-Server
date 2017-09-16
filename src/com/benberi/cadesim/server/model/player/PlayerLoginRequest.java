package com.benberi.cadesim.server.model.player;

public class PlayerLoginRequest {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The player name requested
     */
    private String name;


    public PlayerLoginRequest(Player player, String name) {
        this.player = player;
        this.name = name;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }
}

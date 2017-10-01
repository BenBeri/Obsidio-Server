package com.benberi.cadesim.server.model.player.domain;

import com.benberi.cadesim.server.model.player.Player;

public class PlayerLoginRequest {

    /**
     * The player instance
     */
    private Player player;

    /**
     * The player name requested
     */
    private String name;

    /**
     * The ship ID selected
     */
    private int ship;

    /**
     * The version
     */
    private int version;

    public PlayerLoginRequest(Player player, String name, int ship, int version) {
        this.player = player;
        this.name = name;
        this.ship = ship;
        this.version = version;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    public int getShip() {
        return ship;
    }

    public int getVersion() {
        return version;
    }
}

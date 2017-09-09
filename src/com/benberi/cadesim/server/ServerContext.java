package com.benberi.cadesim.server;

import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.PlayerManager;
import com.benberi.cadesim.server.model.cade.BlockadeMap;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.packet.ServerPacketManager;

public class ServerContext {

    /**
     * The player manager
     */
    private PlayerManager players;

    /**
     * The time machine of blockade
     */
    private BlockadeTimeMachine timeMachine;

    /**
     * The blockade map
     */
    private BlockadeMap map;

    /**
     * The configuration of the server
     */
    private ServerConfiguration configuration;

    private ServerPacketManager packets;

    public ServerContext(ServerConfiguration config) {
        this.configuration = config;
        this.players = new PlayerManager(this);
        this.timeMachine = new BlockadeTimeMachine(this);
        this.map = new BlockadeMap(this);
        this.packets = new ServerPacketManager(this);
    }

    /**
     * Gets the player manager
     * @return {@link #players}
     */
    public PlayerManager getPlayerManager() {
        return players;
    }

    /**
     * Gets the server configuration
     * @return {@link #configuration}
     */
    public ServerConfiguration getConfiguration() {
         return configuration;
    }

    /**
     * Gets the blokade time machine
     * @return {@link #timeMachine}
     */
    public BlockadeTimeMachine getTimeMachine() {
        return timeMachine;
    }

    /**
     * Gets the map handler
     * @return  {@link #map}
     */
    public BlockadeMap getMap() {
        return map;
    }

    /**
     * Gets the packet manager
     * @return {@link #packets}
     */
    public ServerPacketManager getPackets() {
        return packets;
    }
}

package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.packet.out.AddPlayerShipPacket;
import com.benberi.cadesim.server.packet.out.SendMapPacket;
import com.benberi.cadesim.server.packet.out.SendTimePacket;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerManager {

    private Logger logger = Logger.getLogger("PlayerManager");

    /**
     * List of players in the game
     */
    private List<Player> players = new ArrayList<>();

    /**
     * The server context
     */
    private ServerContext context;

    public PlayerManager(ServerContext context) {
        this.context = context;
    }

    /**
     * Ticks all players
     */
    public void tick() {
        sendTime();
    }

    public void registerPlayer(Channel c) {
        Player player = new Player(context, c);
        this.players.add(player);
        logger.info("A new player attempts to join the game: " + c.remoteAddress());
    }

    /**
     * Sends a packet to all players
     * @param p The packet to send
     */
    public void sendAllPacket(Packet p, boolean registeredOnly) {
        if (registeredOnly) {
            players.stream().filter(Player::isRegistered).forEach(pl -> pl.sendPacket(p));
        }
        else {
            players.forEach(pl -> pl.sendPacket(p));
        }
    }

    /**
     * De-registers a player from the server
     * @param channel   The channel that got de-registered
     */
    public void deRegisterPlayer(Channel channel) {
        boolean removed = players.removeIf(player -> player.equals(channel));

        if (Constants.DEBUG_PACKETS) {
            if (removed) {
                logger.info("Successfully deregistered channel " + channel.remoteAddress());
            }
            else {
                logger.log(Level.WARNING, "A channel de-registered but could not find the player instance: " + channel.remoteAddress());
            }
        }

       // todo send pakcets to all about this removal

    }

    /**
     * Sends and updates the time of the game, turn for all players
     */
    private void sendTime() {
        SendTimePacket packet = new SendTimePacket(context.getTimeMachine());
        sendAllPacket(packet, true);
    }

    /**
     * Gets a player instance by its channel
     * @param c The channel
     * @return  The player instance if found, null if not found
     */
    public Player getPlayerByChannel(Channel c) {
        for(Player p : players) {
            if (p.equals(c)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Instances a player, makes it official to all, and initializes it
     * @param player    The player to instance
     */
    public void instancePlayer(Player player) {
        player.sendBoard(); // Send the game board map

    }
}

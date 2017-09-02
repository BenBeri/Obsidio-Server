package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.packet.out.AddPlayerShipPacket;
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

        logger.info("A new player joined the game: " + c.remoteAddress());
        //sendAllPacket(new AddPlayerShipPacket(player));
    }

    /**
     * Sends a packet to all players
     * @param p The packet to send
     */
    public void sendAllPacket(Packet p) {
        players.forEach(pl -> pl.sendPacket(p));
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
        sendAllPacket(packet);
    }
}

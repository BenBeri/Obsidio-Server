package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.vessel.Vessel;
import com.benberi.cadesim.server.packet.out.SendMapPacket;
import com.sun.security.ntlm.Server;
import io.netty.channel.Channel;

import java.util.logging.Logger;


public class Player {

    private Logger logger = Logger.getLogger("Player-Logger");

    /**
     * The channel of the player
     */
    private Channel channel;

    /**
     * The name of the player
     */
    private String name = "A Vessel";

    /**
     * Player's vessel
     */
    private Vessel vessel = Vessel.createVesselByType(Constants.DEFAULT_VESSEL_TYPE);

    /**
     * X-axis position of the player
     */
    private int x;

    /**
     * Y-axis position of the player
     */
    private int y;

    /**
     * The face of the player (rotation id)
     */
    private int face;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * If the player is registered
     */
    private boolean isRegistered;

    public Player(ServerContext ctx, Channel c) {
        this.channel = c;
        this.context = ctx;
    }

    /**
     * Sends a packet
     * @param p The packet to send
     */
    public void sendPacket(Packet p) {
        if (Constants.DEBUG_PACKETS) {
            logger.info("Writing packet in channel " + channel.remoteAddress() + " packet: " + p.toString());
        }
        channel.write(p);
    }

    /**
     * Gets the channel
     * @return {@link #channel}
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets the server context
     * @return {@link #context}
     */
    public ServerContext getContext() {
        return context;
    }

    public String getName() {
        return name;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }


    public void register(String name) {
        this.name = name;
        context.getPlayerManager().instancePlayer(this);
        this.isRegistered = true;
    }

    /**
     * If the player is registered
     * @return  {@link #isRegistered}
     */
    public boolean isRegistered() {
        return this.isRegistered;
    }

    /**
     * Sends the game board to the client
     */
    public void sendBoard() {
        Packet packet = new SendMapPacket(context.getMap());
        sendPacket(packet);
    }

    /**
     * Proper equality check
     * @param o The object to check - supports for Player, Channel
     * @return <code>TRUE</code> If the given object equals to either channel, player or other.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            return this == o;
        }
        else if (o instanceof Channel) {
            return this.channel == o;
        }
        return super.equals(o);
    }
}

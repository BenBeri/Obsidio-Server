package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.vessel.Vessel;
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
    private String name;

    /**
     * Player's vessel
     */
    private Vessel vessel;

    /**
     * The server context
     */
    private ServerContext context;

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

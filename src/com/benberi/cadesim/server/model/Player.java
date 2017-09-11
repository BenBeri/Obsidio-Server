package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.move.MoveTokensHandler;
import com.benberi.cadesim.server.model.move.MoveType;
import com.benberi.cadesim.server.model.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.vessel.Vessel;
import com.benberi.cadesim.server.model.vessel.VesselFace;
import com.benberi.cadesim.server.model.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.packet.out.impl.*;
import io.netty.channel.Channel;
import sun.security.krb5.internal.PAData;

import java.util.List;
import java.util.Random;
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
    private String name = "";

    /**
     * Player's vessel
     */
    private Vessel vessel = Vessel.createVesselByType(Constants.DEFAULT_VESSEL_TYPE);

    private final TurnMoveHandler moves;

    /**
     * Move tokens handler
     */
    private MoveTokensHandler tokens;

    /**
     * The animation structure
     */
    private MoveAnimationStructure animation = new MoveAnimationStructure();

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
    private VesselFace face = VesselFace.NORTH;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * If the player is registered
     */
    private boolean isRegistered;

    private List<VesselMovementAnimation> animations;

    public Player(ServerContext ctx, Channel c) {
        this.channel = c;
        this.context = ctx;
        this.tokens = new MoveTokensHandler(vessel);
        this.moves = new TurnMoveHandler(this);
    }

    /**
     * Sends a packet
     * @param packet The packet to send
     */
    public void sendPacket(OutgoingPacket packet) {
        packet.encode();
        channel.write(packet);
        channel.flush();
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

    public VesselFace getFace() {
        return face;
    }

    public void setFace(VesselFace face) {
        this.face = face;
    }


    public void register(String name) {
        this.name = name;
        this.isRegistered = true;

        Random r = new Random();
        this.x = r.nextInt(10);
    }

    public MoveAnimationStructure getAnimationStructure() {
        return animation;
    }

    public TurnMoveHandler getMoves() {
        return moves;
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
        SendMapPacket packet = new SendMapPacket();
        packet.setMap(context.getMap());
        sendPacket(packet);
    }

    /**
     * Sends ship damage packet
     */
    public void sendDamage() {
        SendDamagePacket packet = new SendDamagePacket();
        packet.setDamage(vessel.getDamage());
        packet.setBilge(vessel.getBilge());
        sendPacket(packet);
    }

    /**
     * Sends move tokens to the client from the token handler
     * {@link #tokens}
     */
    public void sendTokens() {
        SendMoveTokensPacket packet = new SendMoveTokensPacket();
        packet.setLeft(tokens.getLeft());
        packet.setRight(tokens.getRight());
        packet.setForward(tokens.getForward());
        packet.setCannons(tokens.getCannons());

        sendPacket(packet);
    }

    /**
     * Proper equality check
     *
     * @param o The object to check - supports for Player, Channel
     * @return <code>TRUE</code> If the given object equals to either channel, player or other.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            return this == o;
        } else if (o instanceof Channel) {
            return this.channel == o;
        }
        return super.equals(o);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sendMovePlaceVerification(int slot, int move) {
        System.out.println("move place send");
        MovePlaceVerificationPacket packet = new MovePlaceVerificationPacket();
        packet.setSlot(slot);
        packet.setMove(move);
        sendPacket(packet);
    }

    /**
     * Places a move
     * @param slot  The slot to place at
     * @param move  The move to place
     */
    public void placeMove(int slot, int move) {
        if (moves.getManuaverSlot() == slot) {
            return;
        }
        MoveType moveType = MoveType.forId(move);
        if (moveType != null) {
            MoveType currentMove = moves.getMove(slot);

            if (tokens.useTokenForMove(moveType)) {
                if (currentMove != MoveType.NONE) {
                    tokens.addToken(currentMove, 1);
                }

                moves.setMove(slot, moveType);
                sendMovePlaceVerification(slot, move);
                sendTokens();
            }
            else {
                if (currentMove != MoveType.NONE) {
                    tokens.addToken(currentMove, 1);
                    sendMovePlaceVerification(slot, MoveType.NONE.getId());
                    sendTokens();
                }
            }

            context.getPlayerManager().sendMoveBar(this);
        }
    }


    public List<VesselMovementAnimation> getAnimations() {
        return animations;
    }

    public void setAnimations(List<VesselMovementAnimation> animations) {
        this.animations = animations;
    }

    public int getAnimationTime() {
        int time = 0;
        for (VesselMovementAnimation anim : animations) {
            time += anim.getTime();
        }

        return time;
    }
}

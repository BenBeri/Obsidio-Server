package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.collision.PlayerCollisionStorage;
import com.benberi.cadesim.server.model.player.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.player.move.MoveTokensHandler;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.player.domain.JobbersQuality;
import com.benberi.cadesim.server.model.player.domain.MoveGenerator;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.util.Position;
import com.benberi.cadesim.server.util.RandomUtils;
import io.netty.channel.Channel;

import java.util.logging.Logger;


public class Player extends Position {


    private Logger logger = Logger.getLogger("Player-Logger");

    /**
     * The channel of the player
     */
    private Channel channel;

    /**
     * The packet manager
     */
    private PlayerPacketManager packets;

    /**
     * The name of the player
     */
    private String name = "";

    /**
     * Player's vessel
     */
    private Vessel vessel = Vessel.createVesselByType(Constants.DEFAULT_VESSEL_TYPE);

    /**
     * The turn move handler
     */
    private TurnMoveHandler moves;

    /**
     * Move tokens handler
     */
    private MoveTokensHandler tokens;

    /**
     * The animation structure
     */
    private MoveAnimationStructure animation = new MoveAnimationStructure();

    private JobbersQuality jobbersQuality = JobbersQuality.ELITE;

    /**
     * The face of the player (rotation id)
     */
    private VesselFace face = VesselFace.EAST;

    private MoveGenerator moveGenerator;

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * If the player is registered
     */
    private boolean isRegistered;

    /**
     * The last damage update
     */
    private long lastDamageUpdate;

    /**
     * The turn the ship sunk at
     */
    private int sunkTurn = -1;

    /**
     * The collision storage
     */
    private PlayerCollisionStorage collisionStorage;

    public Player(ServerContext ctx, Channel c) {
        this.channel = c;
        this.context = ctx;
        this.packets = new PlayerPacketManager(this);
        this.moveGenerator = new MoveGenerator(this);
        this.tokens = new MoveTokensHandler(this);
        this.moves = new TurnMoveHandler(this);
        this.collisionStorage = new PlayerCollisionStorage(this);
    }

    /**
     * Logical updates during the game
     */
    public void update() {
        if (vessel.getDamagePercentage() >= jobbersQuality.getMinDamageForBilge()) {
            double bilge = Constants.BILGE_INCREASE_RATE_PER_TICK + (vessel.getDamagePercentage() / 100) / 10; // Rate increases as damage is higher
            vessel.appendBilge(bilge);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        if (vessel.getDamagePercentage() > 0) {
            double rate = jobbersQuality.getFixRate() / Constants.TURN_TIME;
            vessel.decreaseDamage(rate);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        if (vessel.getBilgePercentage() > 0) {
            double rate = (jobbersQuality.getBilgeFixRate() / Constants.TURN_TIME) - (vessel.getDamage() / 10000);
            vessel.decreaseBilge(rate);
            if (System.currentTimeMillis() - lastDamageUpdate >= 2000) {
                packets.sendDamage();
                lastDamageUpdate = System.currentTimeMillis();
            }
        }

        moveGenerator.update();
    }

    /**
     * Sends a packet
     *
     * @param packet The packet to send
     */
    public void sendPacket(OutgoingPacket packet) {
        packet.encode();
        channel.write(packet);
        channel.flush();
    }

    /**
     * Gets the channel
     *
     * @return {@link #channel}
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Gets the jobbers quality set
     *
     * @return {@link JobbersQuality}
     */
    public JobbersQuality getJobbersQuality() {
        return jobbersQuality;
    }

    /**
     * Gets the collision storage
     *
     * @return {@link #collisionStorage}
     */
    public PlayerCollisionStorage getCollisionStorage() {
        return collisionStorage;
    }

    /**
     * Gets the server context
     *
     * @return {@link #context}
     */
    public ServerContext getContext() {
        return context;
    }

    /**
     * Gets the player name
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the vessel instance
     *
     * @return {@link #vessel}
     */
    public Vessel getVessel() {
        return vessel;
    }


    /**
     * Gets the player face
     *
     * @return  {@link VesselFace}
     */
    public VesselFace getFace() {
        return face;
    }

    /**
     * Sets the vessel face
     *
     * @param face  The new vessel face to set
     */
    public void setFace(VesselFace face) {
        this.face = face;
    }

    /**
     * Sets the player registered
     *
     * @param name  The name to register
     */
    public void register(String name) {
        this.name = name;
        this.isRegistered = true;
        this.setX(RandomUtils.randInt(0, 10));
        this.setY(RandomUtils.randInt(0, 3));
    }

    /**
     * Gets the animation structure of the player
     *
     * @return {@link MoveAnimationStructure}
     */
    public MoveAnimationStructure getAnimationStructure() {
        return animation;
    }

    /**
     * Gets the player's selected turn moves
     *
     * @return {@link TurnMoveHandler}
     */
    public TurnMoveHandler getMoves() {
        return moves;
    }

    /**
     * If the player is registered
     *
     * @return  {@link #isRegistered}
     */
    public boolean isRegistered() {
        return this.isRegistered;
    }

    /**
     * Gets the move tokens
     *
     * @return {@link MoveTokensHandler}
     */
    public MoveTokensHandler getMoveTokens() {
        return tokens;
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
    /**
     * Places a move
     * @param slot  The slot to place at
     * @param move  The move to place
     */
    public void placeMove(int slot, int move) {
        if (vessel.isManuaver() && moves.getManuaverSlot() == slot) {
            return;
        }
        MoveType moveType = MoveType.forId(move);
        if (moveType != null) {
            MoveType currentMove = moves.getMove(slot);

            if (tokens.useTokenForMove(moveType)) {
                if (currentMove != MoveType.NONE) {
                    tokens.returnMove(currentMove);
                }

                moves.setMove(slot, moveType);
                packets.sendMovePlaceVerification(slot, move);
                packets.sendTokens();
            }
            else {
                if (currentMove != MoveType.NONE) {
                    tokens.returnMove(currentMove);
                    packets.sendMovePlaceVerification(slot, MoveType.NONE.getId());
                    packets.sendTokens();
                }
            }

            context.getPlayerManager().sendMoveBar(this);
        }
    }

    /**
     * Attempts to place a cannon
     *
     * @param slot  The slot to place
     * @param side  The side to place
     */
    public void placeCannon(int slot, int side) {
        if (tokens.getCannons() <= 0) {
            return;
        }
        if (side == 0) {
            int shoots = moves.getLeftCannons(slot);
            if (shoots == 0) {
                moves.setLeftCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else {
                if (vessel.isDualCannon()) {
                    if (shoots < 2) {
                        moves.setLeftCannons(slot, 2);
                        tokens.removeCannons(1);
                    }
                    else {
                        moves.setLeftCannons(slot, 0);
                        tokens.addCannons(2);
                    }
                }
                else {
                    moves.setLeftCannons(slot, 0);
                    tokens.addCannons(1);
                }
            }

            packets.sendPlaceCannonVerification(slot, 0);
        }
        else {
            int shoots = moves.getRightCannons(slot);
            if (shoots == 0) {
                moves.setRightCannons(slot, 1);
                tokens.removeCannons(1);
            }
            else {
                if (vessel.isDualCannon()) {
                    if (shoots < 2) {
                        moves.setRightCannons(slot, 2);
                        tokens.removeCannons(1);
                    }
                    else {
                        moves.setRightCannons(slot, 0);
                        tokens.addCannons(2);
                    }
                }
                else {
                    moves.setRightCannons(slot, 0);
                    tokens.addCannons(1);
                }
            }

            packets.sendPlaceCannonVerification(slot, 1);
        }

        packets.sendTokens();
        context.getPlayerManager().sendMoveBar(this);
    }

    /**
     * Gets the packet handler
     *
     * @return {@link PlayerPacketManager}
     */
    public PlayerPacketManager getPackets() {
        return packets;
    }

    public void processAfterTurnUpdate() {
        tokens.clearTemp();
        tokens.tickExpiration();

        // Send moves
        packets.sendAnimationStructure();

        packets.sendDamage();
        moves.resetTurn();
        packets.sendTokens();

    }

    public void setSunk(int sunk) {
        this.sunkTurn = sunk;
    }

    public boolean isSunk() {
        return sunkTurn > -1;
    }

    public int getSunkTurn() {
        return sunkTurn;
    }

    public void giveLife() {
        sunkTurn = -1;
        vessel.resetDamageAndBilge();
        tokens = new MoveTokensHandler(this);
        moves = new TurnMoveHandler(this);
        animation = new MoveAnimationStructure();

        resetPosition();

        face = VesselFace.NORTH;

        for (Player p : context.getPlayerManager().listRegisteredPlayers()) {
            p.packets.sendRespawn(this);
        }

        packets.sendDamage();
        packets.sendTokens();
    }
}

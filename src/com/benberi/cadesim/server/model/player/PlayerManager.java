package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.collision.CollisionCalculator;
import com.benberi.cadesim.server.model.move.MoveAnimationTurn;
import com.benberi.cadesim.server.model.move.MoveType;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.packet.out.impl.LoginResponsePacket;
import com.benberi.cadesim.server.packet.out.impl.SendPlayersAnimationStructurePacket;
import com.benberi.cadesim.server.util.Direction;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerManager {

    private Logger logger = Logger.getLogger("PlayerManager");

    /**
     * List of players in the game
     */
    private List<Player> players = new ArrayList<>();

    /**
     * Queued players login
     */
    private Queue<PlayerLoginRequest> queuedLoginRequests = new LinkedList<>();

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The collision calculator
     */
    private CollisionCalculator collision;

    private long lastTimeSend;

    public PlayerManager(ServerContext context) {
        this.context = context;
        this.collision = new CollisionCalculator(context, this);
    }

    /**
     * Ticks all players
     */
    public void tick() {

        // Send time ~ every second
        if (System.currentTimeMillis() - lastTimeSend >= 1000) {
            sendTime();
            lastTimeSend = System.currentTimeMillis();
        }

        // Update players (for stuff like damage fixing, bilge fixing and move token generation)
        for (Player p : listRegisteredPlayers()) {
            p.update();
        }

        // Handle login requests
        if (!context.getTimeMachine().hasTurnDelay()) {
            handlePlayerLoginRequests();
        }
    }

    /**
     * Handles the turn
     */
    public void handleTurn() {
        // Send tokens

        int maxSlotsFilled = 0;
        int maxShotsFilled = 0;
        List<Player> registered = listRegisteredPlayers();

        // Loop through 4 turns we want to execute
        for (int turn = 0; turn < 4; turn++) {
            for (Player p : registered) {
                // If player is sunk, don't move anywhere even if moves placed
                if (p.isSunk()) {
                    continue;
                }

                // Process move, and set new position
                MoveType move = p.getMoves().getMove(turn);
                move.setNextPosition(p);
                p.setFace(move.getNextFace(p.getFace()));
            }

            // Handle shoots, and register animations per player in this turn
            for (Player p : registered) {
                // If this ship sunk, don't place any animations or shoots.
                if (p.isSunk() && p.getSunkTurn() != turn) {
                    continue;
                }

                // The move the player wants to perform
                MoveType move = p.getMoves().getMove(turn);
                // left shoots
                int leftShoots = p.getMoves().getLeftCannons(turn);
                // right shoots
                int rightShoots = p.getMoves().getRightCannons(turn);

                /*
                 * Apply damage to players on the left/right side of this player's shoots
                 */
                damagePlayersAtDirection(leftShoots, p, Direction.LEFT, turn);
                damagePlayersAtDirection(rightShoots, p, Direction.RIGHT, turn);


                /*
                 * Register animations for this move in this turn, for the client
                 */
                MoveAnimationTurn turnAnimation = p.getAnimationStructure().getTurn(turn);
                // Sets the animation for this turn
                turnAnimation.setAnimation(VesselMovementAnimation.getIdForMoveType(move));
                // sets shoots
                turnAnimation.setLeftShoots(leftShoots);
                turnAnimation.setRightShoots(rightShoots);

                int count = p.getAnimationStructure().countFilledTurnSlots();
                int countShoots = p.getAnimationStructure().countFilledShootSlots();

                if (count > maxSlotsFilled) {
                    maxSlotsFilled = count;
                }

                if (countShoots > maxShotsFilled) {
                    maxShotsFilled = countShoots;
                }
            }
        }


        int sunkShips = 0;

        for (int i = 0; i < 4; i++) {
            for (Player p : registered) {
                MoveAnimationTurn turn = p.getAnimationStructure().getTurn(i);
                if (turn.isSunk()) {
                    sunkShips++;
                    break;
                }
            }
        }

        // Send packets to the players
        for (Player p : registered) {
            p.processAfterTurnUpdate();
        }

        context.getTimeMachine().setTurnResetDelay(System.currentTimeMillis() + (maxSlotsFilled * 1130) + (maxShotsFilled * 1800) + sunkShips * 3500);
    }


    /**
     * Damages entities for player's shoot
     * @param shoots        How many shoots to calculate
     * @param source        The shooting vessel instance
     * @param direction     The shoot direction
     */
    private void damagePlayersAtDirection(int shoots, Player source, Direction direction, int turnId) {
        if (shoots <= 0) {
            return;
        }
        Player player = collision.getVesselForCannonCollide(source, direction);
        if (player != null) {
            player.getVessel().appendDamage(((double) shoots * source.getVessel().getCannonType().getDamage()));
            if (player.getVessel().isDamageMaxed()) {
                player.setSunk(turnId);
                MoveAnimationTurn turnAnimation = player.getAnimationStructure().getTurn(turnId);
                turnAnimation.setSunk(true);
            }
        }
    }

    /**
     * Gets a player for given position
     * @param x The X-axis position
     * @param y The Y-xis position
     * @return The player instance if found, null if not
     */
    public Player getPlayerByPosition(int x, int y) {
        for (Player p : listRegisteredPlayers()) {
            if (p.getX() == x && p.getY() == y) {
                return p;
            }
        }
        return  null;
    }

    /**
     * Sends a player's move bar to everyone
     *
     * @param pl    The player's move to send
     */
    public void sendMoveBar(Player pl) {
        for (Player p : listRegisteredPlayers()) {
            p.getPackets().sendMoveBar(pl);
        }
    }

    /**
     * Lists all registered players
     *
     * @return Sorted list of {@link #players} with only registered players
     */
    public List<Player> listRegisteredPlayers() {
        List<Player> registered = new ArrayList<>();
        for (Player p : players) {
            if (p.isRegistered()) {
                registered.add(p);
            }
        }

        return registered;
    }

    /**
     * Registers a new player to the server, puts him in a hold until he sends the protocol handshake packet
     *
     * @param c The channel to register
     */
    public void registerPlayer(Channel c) {
        Player player = new Player(context, c);
        players.add(player);
        logger.info("A new player attempts to join the game: " + c.remoteAddress());
    }


    /**
     * De-registers a player from the server
     *
     * @param channel   The channel that got de-registered
     */
    public void deRegisterPlayer(Channel channel) {
        boolean removed = players.removeIf(player -> player.equals(channel));
        if (removed) {
            logger.info("Channel deregistered: " + channel.remoteAddress());
        }
        else {
            logger.log(Level.WARNING, "A channel de-registered but could not find the player instance: " + channel.remoteAddress());
        }
    }

    /**
     * Reset all move bars
     */
    public void resetMoveBars() {
        for (Player p : listRegisteredPlayers()) {
            sendMoveBar(p);
            p.getAnimationStructure().reset();
        }
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
     * Gets a player by its name
     * @param name  The player name
     * @return The player
     */
    public Player getPlayerByName(String name) {
        for (Player p : listRegisteredPlayers()) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Sends a player for all players
     *
     * @param player    The player to send
     */
    public void sendPlayerForAll(Player player) {
        for (Player p : players) {
            if (p == player) {
                continue;
            }
            p.getPackets().sendPlayer(player);
        }
    }

    /**
     * Queues a player login request
     *
     * @param request   The login request
     */
    public void queuePlayerLogin(PlayerLoginRequest request) {
        queuedLoginRequests.add(request);
    }

    /**
     * Handles all player login requests
     */
    public void handlePlayerLoginRequests() {
        while(!queuedLoginRequests.isEmpty()) {
            PlayerLoginRequest request = queuedLoginRequests.poll();

            Player pl = request.getPlayer();
            String name = request.getName();

            int response = LoginResponsePacket.SUCCESS;

            if (getPlayerByName(name) != null) {
                response = LoginResponsePacket.NAME_IN_USE;
            }

            pl.getPackets().sendLoginResponse(response);

            if (response == LoginResponsePacket.SUCCESS) {
                pl.register(name);
                pl.getPackets().sendBoard();
                pl.getPackets().sendPlayers();
                pl.getPackets().sendDamage();
                pl.getPackets().sendTokens();
                sendPlayerForAll(pl);
            }
        }
    }

    /**
     * Sends and updates the time of the game, turn for all players
     */
    private void sendTime() {

        for (Player player : players) {
            if (!player.isRegistered()) {
                continue;
            }
            player.getPackets().sendTime();
        }
    }

    public void resetSunkShips() {
        for (Player p : listRegisteredPlayers()) {
            if (p.isSunk()) {
                p.giveLife();
            }
        }
    }
}

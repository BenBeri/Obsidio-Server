package com.benberi.cadesim.server.model.player;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.collision.CollisionCalculator;
import com.benberi.cadesim.server.model.player.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.player.move.MoveAnimationTurn;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.domain.PlayerLoginRequest;
import com.benberi.cadesim.server.codec.packet.out.impl.LoginResponsePacket;
import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;
import io.netty.channel.Channel;
import javafx.scene.shape.MoveTo;

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
     * Handles and executes all turns
     */
    public void handleTurns() {

        // Loop through all turns
        for (int turn = 0; turn < 4; turn++) {

            /*
             * Phase 1 handling
             */

            // Loop through phases in the turn (split turn into phases, e.g turn left is 2 phases, turn forward is one phase).
            // So if stopped in phase 1, and its a turn left, it will basically stay in same position, if in phase 2, it will only
            // move one step instead of 2 full steps.
            for (int phase = 0; phase < 2; phase++) {

                 // Go through all players and check if their move causes a collision
                 for (Player p : listRegisteredPlayers()) {

                     // If a player is already collided in this step or is sunk, we don't want him to move
                     // anywhere, so we skip this iteration
                     if (p.getCollisionStorage().isCollided(turn) || p.isSunk()) {
                         continue;
                     }

                     // Checks collision for the player, according to his current step #phase index and turn
                     collision.checkCollision(p, turn, phase, true);
                 }

                 // There we update the bumps toggles, if a bump was toggled, we need to save it to the animation storage for this
                 // current turn, and un-toggle it from the collision storage. We separate bump toggles from the main player loop above
                 // Because we have to make sure that bumps been calculated for every one before moving to the next phase
                 for (Player p : listRegisteredPlayers()) {
                     if (p.getCollisionStorage().isBumped()) {
                         p.set(p.getCollisionStorage().getBumpAnimation().getPositionForAnimation(p));
                         p.getCollisionStorage().setBumped(false);
                     }

                     // Toggle last position change save off because we made sure that it was set in the main loop of players
                     p.getCollisionStorage().setPositionChanged(false);
                 }
             }


            // There we save the animations of all bumps, collision and moves of this turn, and clearing the collision storage
            // And what-ever. We also handle shoots there and calculate the turn time
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn t = p.getAnimationStructure().getTurn(turn);
                MoveType move = p.getMoves().getMove(turn);
                p.setFace(move.getNextFace(p.getFace()));
                t.setMoveToken(move);
                if (p.getCollisionStorage().isCollided(turn)) {
                    t.setAnimation(VesselMovementAnimation.getBumpForPhase(p.getCollisionStorage().getCollisionRerefence(turn).getPhase()));
                }
                else {
                    if (p.getCollisionStorage().getBumpAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                        t.setAnimation(p.getCollisionStorage().getBumpAnimation());
                    }
                    else {
                        t.setAnimation(VesselMovementAnimation.getIdForMoveType(move));
                    }
                }

                // Clear the collision storage toggles and such
                p.getCollisionStorage().clear();
            }

            /*
            * Phase 2 handling
            */

            for(Player player : listRegisteredPlayers()) {
                if (player.getCollisionStorage().isBumped()) {
                    continue;
                }
                int tile = context.getMap().getTile(player.getX(), player.getY());
                if (context.getMap().isActionTile(player.getX(), player.getY())) {
                    Position next = context.getMap().getNextActionTilePosition(player);
                    if (context.getMap().isWhirlpool(tile)) {
                        player.setFace(context.getMap().getNextActionTileFace(player.getFace()));
                    }
                    if (!collision.checkActionCollision(player, next, turn)) {
                        player.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(tile));
                    }
                    else {
                        player.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getBumpAnimationForAction(tile));
                    }
                }
            }

            for (Player p : listRegisteredPlayers()) {
                p.getCollisionStorage().clear();
                p.getCollisionStorage().setBumped(false);

                // left shoots
                int leftShoots = p.getMoves().getLeftCannons(turn);
                // right shoots
                int rightShoots = p.getMoves().getRightCannons(turn);

                // Apply cannon damages if they collided with anyone
                damagePlayersAtDirection(leftShoots, p, Direction.LEFT, turn);
                damagePlayersAtDirection(rightShoots, p, Direction.RIGHT, turn);

                MoveAnimationTurn t = p.getAnimationStructure().getTurn(turn);

                // Set cannon animations
                t.setLeftShoots(leftShoots);
                t.setRightShoots(rightShoots);
            }
        }

        // Process some after-turns stuff like updating damage interfaces, and such
        for (Player p : listRegisteredPlayers()) {
            p.processAfterTurnUpdate();
        }

        // Set the turn delay, and calculate the approximate execution time of the turn
        context.getTimeMachine().setTurnResetDelay(System.currentTimeMillis() + countTurnExecutionTime());
    }

    private int countTurnExecutionTime() {
        int slotsFilled = 0;
        int slotsSubAnimations = 0;
        int shootsFilled = 0;
        int sunkShips = 0;

        for (int i = 0; i < 4; i++) {
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn turn = p.getAnimationStructure().getTurn(i);
                if (turn.getAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                    slotsFilled += 2000;
                    break;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn turn = p.getAnimationStructure().getTurn(i);
                if (turn.getLeftShoots() > 0 || turn.getRightShoots() > 0) {
                    shootsFilled += 1650;
                    break;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn turn = p.getAnimationStructure().getTurn(i);
                if (turn.getSubAnimation() != VesselMovementAnimation.NO_ANIMATION) {
                    if (turn.getSubAnimation().isWhirlpool()) {
                        slotsSubAnimations += 1700;
                    }
                    else {
                        slotsSubAnimations += 1100;
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            for (Player p : listRegisteredPlayers()) {
                MoveAnimationTurn turn = p.getAnimationStructure().getTurn(i);
                if (turn.isSunk()) {
                    sunkShips += 5000;
                    break;
                }
            }
        }

        sunkShips -= (slotsFilled + slotsSubAnimations + shootsFilled);
        if (sunkShips <= 0) {
            sunkShips = 0;
        }
        return sunkShips + slotsFilled + slotsSubAnimations + shootsFilled;
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
        Player player = getPlayerByChannel(channel);
        if (player != null) {
            for (Player p : listRegisteredPlayers()) {
                if (p != player) {
                    p.getPackets().sendRemovePlayer(player);
                }
            }
        }
        boolean removed = players.removeIf(pl -> pl.equals(channel));
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

    public void sendPositions() {

        for (Player p : listRegisteredPlayers()) {
            if (p.isNeedsRespawn()) {
                p.respawn();
            }
        }

        for (Player p : listRegisteredPlayers()) {
            p.getPackets().sendPositions();
        }
    }

}

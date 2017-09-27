package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Collision Mechanism
 * @author Ben | Jony benberi545@gmail.com
 *
 * https://github.com/BenBeri/Obsidio-Server/
 */
public class CollisionCalculator {

    /**
     * The server context
     */
    private ServerContext context;

    /**
     * The player manager
     */
    private PlayerManager players;

    
    public CollisionCalculator(ServerContext ctx, PlayerManager players) {
        this.context = ctx;
        this.players = players;
    }

    /**
     * Gets all players that are trying to claim the given position
     * @param pl        The player to check for
     * @param target    The target position to check
     * @param turn      The turn
     * @param phase     The move-phase step
     * @return A list of players that collided there
     */
    public List<Player> getPlayersTryingToClaim(Player pl, Position target, int turn, int phase) {
        List<Player> collided = new ArrayList<>();

        for (Player p : players.listRegisteredPlayers()) {
            if (p == pl || p.getCollisionStorage().isCollided(turn)) {
                continue;
            }
            MoveType move = p.getMoves().getMove(turn);
            Position next;
            if (p.getCollisionStorage().isPositionChanged()) {
                next = p;
            }
            else {
                next = move.getNextPositionWithPhase(p, p.getFace(), phase);
            }
            if (next.equals(target)) {
                collided.add(p);
            }
        }

        return collided;
    }

    public List<Player> getPlayersTryingToClaimByAction(Player pl, Position target, int turn) {
        List<Player> collided = new ArrayList<>();
        for (Player p : players.listRegisteredPlayers()) {
            if (p == pl || p.getCollisionStorage().isCollided(turn)) {
                continue;
            }

            Position next = p;
            if (!p.getCollisionStorage().isPositionChanged()) {
                next = context.getMap().getNextActionTilePosition(p);
            }
            if (next.equals(target)) {
                collided.add(p);
            }
        }

        return collided;
    }

    /**
     * Checks if a player has a collision according to his move, in the given turn and move-phase
     * @param p             The player to check
     * @param turn          The turn
     * @param phase         The move-phase step
     * @param setPosition   If to set the next position or not on non-collided result
     * @return  <code>TRUE</code> If the player was collided, <code>FALSE</code> if not.
     */
    public boolean checkCollision(Player p, int turn, int phase, boolean setPosition) {

        // The current selected move of the player
        MoveType move =  p.getMoves().getMove(turn);

        // If this player was bumped, and a move was not selected, we want to process the bump animation
        // But we have to check if the position to be bumped is available to be claimed
        if (move == MoveType.NONE && p.getCollisionStorage().isBumped()) {
            Position pos = p.getCollisionStorage().getBumpAnimation().getPositionForAnimation(p);
            Player claimed = players.getPlayerByPosition(pos.getX(), pos.getY());

            // Claiming checking for the new position for bump
            return claimed != null && (claimed.getMoves().getMove(turn) == MoveType.NONE || checkCollision(claimed, turn, phase, false));
        }

        // Use the current position as default.txt, imply we have already set it
        Position position = p;

        // If not set by default.txt on previous loops, gets the next position on the map for the given phase on the given move
        if (!p.getCollisionStorage().isPositionChanged()) {
            position = move.getNextPositionWithPhase(p, p.getFace(), phase);
        }

        // If the player has moved since his last position
        if (!position.equals(p)) {
            // Check for bounds collision with the border
            if (checkBoundCollision(p, turn, phase) || checkRockCollision(p, turn, phase)) {
                return true;
            }

            // Check if the next position is claimed by another player, null result if not
            Player claimed = players.getPlayerByPosition(position.getX(), position.getY());

            // If the result is not null, the position is claimed
            if (claimed != null) {
                Position claimedNextPos = claimed;
                if (!claimed.getCollisionStorage().isPositionChanged()) {
                    claimedNextPos = claimed.getMoves().getMove(turn).getNextPositionWithPhase(claimed, claimed.getFace(), phase);
                }

                // Check if the claimed position doesn't move away
                if (claimed.getMoves().getMove(turn) == MoveType.NONE || claimed.getMoves().getMove(turn).getNextPositionWithPhase(claimed, claimed.getFace(), phase).equals(claimed)) {
                    collide(p, claimed, turn, phase);

                    if (move == MoveType.FORWARD && canBumpPlayer(p, claimed, turn, phase)) {
                        bumpPlayer(claimed, p, turn, phase);
                    }

                    claimed.getVessel().appendDamage(p.getVessel().getRamDamage());

                    return true;
                }
                else if (claimedNextPos.equals(p)) { // If they switched positions (e.g nose to nose, F, F move)
                    collide(p, claimed, turn, phase);
                    collide(claimed, p, turn, phase);
                    return true;
                }
                else {
                    // Make sure that the claimed position moves away successfully
                    if (!checkCollision(claimed, turn, phase, false)) {
                        if (setPosition) {
                            // Moved successfully, claim position
                            p.set(position);
                            p.getCollisionStorage().setPositionChanged(true);
                        }
                    }
                    else {
                        // did not move successfully, collide
                        collide(p, claimed, turn, phase);
                        collide(claimed, p, turn, phase);
                        return true;
                    }
                }
            } else {

                // List of players that collided with this player, while performing this move, in this phase
                List<Player> collisions = getPlayersTryingToClaim(p, position, turn, phase);

                if (collisions.size() > 0) { // Collision has happened
                    collisions.add(p);
                    // Stop players from movement
                    for (Player pl : collisions) {
                        pl.getCollisionStorage().setCollided(turn, phase);
                        collide(pl, pl, turn, phase);
                    }
                    return true;
                } else {
                    if (setPosition) {
                        p.set(position);
                        p.getCollisionStorage().setPositionChanged(true);
                    }
                }
            }
        }

        return false;
    }

    public boolean checkActionCollision(Player player, Position target, int turn) {
        if (player.getCollisionStorage().isActionMoveCollided() || context.getMap().isRock(target.getX(), target.getY()) || isOutOfBounds(target)) {
            player.getVessel().appendDamage(player.getVessel().getRamDamage());
            return true;
        }
        Player claimed = players.getPlayerByPosition(target.getX(), target.getY());
        if (claimed != null) {
            Position next = claimed;
            if (!claimed.getCollisionStorage().isPositionChanged()) {
                next = context.getMap().getNextActionTilePosition(claimed);
            }
            if (next.equals(player)) {
                player.getVessel().appendDamage(claimed.getVessel().getRamDamage());
                claimed.getVessel().appendDamage(player.getVessel().getRamDamage());
                player.getCollisionStorage().setActionMoveCollided(true);
                claimed.getCollisionStorage().setActionMoveCollided(true);
                return true;
            }
            else if (next.equals(claimed)) {
                player.getCollisionStorage().setActionMoveCollided(true);
                player.getVessel().appendDamage(claimed.getVessel().getRamDamage());
                Position bumpPos = context.getMap().getNextActionTilePositionForTile(claimed, context.getMap().getTile(player.getX(), player.getY()));
                if (players.getPlayerByPosition(bumpPos.getX(), bumpPos.getY()) == null && !isOutOfBounds(bumpPos) && !context.getMap().isRock(bumpPos.getX(), bumpPos.getY())) {
                    claimed.set(bumpPos);
                    claimed.getVessel().appendDamage(player.getVessel().getRamDamage());
                    claimed.getCollisionStorage().setPositionChanged(true);
                    claimed.getCollisionStorage().setBumped(true);
                    claimed.getAnimationStructure().getTurn(turn).setSubAnimation(VesselMovementAnimation.getSubAnimation(context.getMap().getTile(player.getX(), player.getY())));
                }
                return true;
            }
            if (checkActionCollision(claimed, next, turn)) {
                return true;
            }
        }
        else {
            List<Player> collided = getPlayersTryingToClaimByAction(player, target, turn);
            if (collided.size() > 0) {
                player.getCollisionStorage().setActionMoveCollided(true);
                player.getVessel().appendDamage(player.getVessel().getRamDamage());
                for (Player p : collided) {
                    p.getVessel().appendDamage(p.getVessel().getRamDamage());
                    p.getCollisionStorage().setActionMoveCollided(true);
                }

                return true;
            }
        }
        player.set(target);
        player.getCollisionStorage().setPositionChanged(true);
        return false;
    }

    private boolean checkRockCollision(Player player, int turn, int phase) {
        MoveType move = player.getMoves().getMove(turn);
        Position pos = player;
        if (!player.getCollisionStorage().isPositionChanged()) {
            pos = move.getNextPositionWithPhase(player, player.getFace(), phase);
        }
        if (context.getMap().isRock(pos.getX(), pos.getY())) {
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        return false;
    }

    /**
     * Checks if player's shoot by direction and face hits another player
     * @param source        The shooting player
     * @param direction     The shooting direction
     * @return The player instance if it hits any player, null if no hit
     */
    public Player getVesselForCannonCollide(Player source, Direction direction) {
        switch (direction) {
            case LEFT:
                switch (source.getFace().getDirectionId()) {
                    case 2:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX();
                            int y = source.getY() + i;

                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }

                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 6:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX() + i;
                            int y = source.getY();
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 10:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX();
                            int y = source.getY() - i;
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 14:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX() - i;
                            int y = source.getY();
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                }
                break;
            case RIGHT:
                switch (source.getFace().getDirectionId()) {
                    case 2:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX();
                            int y = source.getY() - i;
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 6:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX() - i;
                            int y = source.getY();
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 10:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX();
                            int y = source.getY() + i;
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 14:
                        for (int i = 1; i < 4; i++) {
                            int x = source.getX() + i;
                            int y = source.getY();
                            if (context.getMap().isBigRock(x, y)) {
                                return null;
                            }
                            Player player = players.getPlayerByPosition(x, y);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                }
                break;

        }

        return null;
    }

    /**
     * Bump a player by player
     * @param bumped   The bumped player
     * @param bumper   The bumper
     */
    private void bumpPlayer(Player bumped, Player bumper, int turn, int phase) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        bumped.getCollisionStorage().setBumpAnimation(anim);
        if (checkCollision(bumped, turn, phase, false)) {
            bumped.getCollisionStorage().setBumpAnimation(VesselMovementAnimation.NO_ANIMATION);
        }
    }

    /**
     * Checks if a player can bump the other player
     * @param bumper    The bumping player
     * @param bumped    The bumped player
     * @return  If the bump can happen
     */
    private boolean canBumpPlayer(Player bumper, Player bumped, int turn, int phase) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        Position bumpPosition = anim.getPositionForAnimation(bumped);
        return bumper.getVessel().getSize() >= bumped.getVessel().getSize() && !bumped.isSunk() &&
                !isOutOfBounds(bumpPosition) && getPlayersTryingToClaim(bumped, bumpPosition, turn, phase).size() == 0;
    }

    /**
     * Collides a player, and damages him
     * @param player    The player that collided
     * @param other     The other player collided with, OR self
     * @param turn      The turn it happened at
     * @param phase     The phase-step it happened at
     */
    private void collide(Player player, Player other, int turn, int phase) {
        player.getCollisionStorage().setCollided(turn, phase);
        player.getVessel().appendDamage(other.getVessel().getRamDamage());
    }

    /**
     * Checks out of bounds collision
     * @param player    The player to check
     * @param turn      The turn
     * @param phase     The move-phase step
     * @return TRUE if boudns collided FALSE if not
     */
    private boolean checkBoundCollision(Player player, int turn, int phase) {
        MoveType move = player.getMoves().getMove(turn);
        Position pos = player;
        if (!player.getCollisionStorage().isPositionChanged()) {
            pos = move.getNextPositionWithPhase(player, player.getFace(), phase);
        }
        if (isOutOfBounds(pos)) {
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        return false;
    }

    /**
     * Checks if a position is out of bounds of the map
     * @param pos   The position to check
     * @return TRUE if out of bounds FALSE if not
     */
    private boolean isOutOfBounds(Position pos) {
        return pos.getX() < 0 || pos.getX() > 19 || pos.getY() < 0 || pos.getY() > 35;
    }
}

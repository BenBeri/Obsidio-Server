package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.move.MoveType;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.model.player.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.player.vessel.Vessel;
import com.benberi.cadesim.server.model.player.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.util.Direction;
import com.benberi.cadesim.server.util.Position;
import com.sun.org.apache.bcel.internal.generic.RET;

import java.util.ArrayList;
import java.util.List;

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


    public List<Player> getPlayersTryingToClaim(Player pl, Position target, int turn, int phase) {
        System.out.println("Trying to find claiming players on phase " + phase +" turn " + turn + " target pos: " + target.getX() + " " + target.getY());
        List<Player> collided = new ArrayList<>();

        for (Player p : players.listRegisteredPlayers()) {
            if (p == pl) {
                continue;
            }
            MoveType move = p.getMoves().getMove(turn);
            System.out.println("current pos: " + p.getX() + " " + p.getY());
            Position next;
            if (p.getCollisionStorage().isPositionChanged()) {
                next = p;
            }
            else {
                next = move.getNextPositionWithPhase(p, p.getFace(), phase);
            }
            System.out.println(p.getName() + "move: " + move.name() +" Next position: " + next.getX() + " " + next.getY());
            if (next.equals(target)) {
                collided.add(p);
            }
        }

        return collided;
    }

    public boolean checkCollision(Player p, int turn, int phase, boolean setPosition) {
        MoveType move =  p.getMoves().getMove(turn);

        if (move == MoveType.NONE && p.getCollisionStorage().isBumped()) {
            Position pos = p.getCollisionStorage().getBumpAnimation().getPositionForAnimation(p);
            Player claimed = players.getPlayerByPosition(pos.getX(), pos.getY());

            return claimed != null && (claimed.getMoves().getMove(turn) == MoveType.NONE || checkCollision(claimed, turn, phase, false));
        }

        // Gets the next position on the map for the given phase on the given move
        Position position = move.getNextPositionWithPhase(p, p.getFace(), phase);

        // If the player has moved since his last position
        if (!position.equals(p)) {

            // Check for bounds collision with the border
            if (checkBoundCollision(p, turn, phase)) {
                return true;
            }

            // Check if the next position is claimed by another player, null result if not
            Player claimed = players.getPlayerByPosition(position.getX(), position.getY());
            // If the result is not null, the position is claimed
            if (claimed != null) {
                // Check if the claimed position doesn't move away
                if (claimed.getMoves().getMove(turn) == MoveType.NONE || claimed.getMoves().getMove(turn).getNextPositionWithPhase(claimed, claimed.getFace(), phase).equals(claimed)) {
                    p.getCollisionStorage().setCollided(turn, phase);

                    if (move == MoveType.FORWARD && canBumpPlayer(p, claimed)) {
                        bumpPlayer(claimed, p, turn, phase);
                    }
                    return true;
                }
                else if (claimed.getMoves().getMove(turn).getNextPositionWithPhase(claimed, claimed.getFace(), phase).equals(p)) {
                    p.getCollisionStorage().setCollided(turn, phase);
                    claimed.getCollisionStorage().setCollided(turn, phase);
                    return true;
                }
                else {
                    if (!checkCollision(claimed, turn, phase, false)) {
                        if (setPosition) {
                            p.set(position);
                            p.getCollisionStorage().setPositionChanged(true);
                        }
                    }
                    else {
                        p.getCollisionStorage().setCollided(turn, phase);
                        claimed.getCollisionStorage().setCollided(turn, phase);
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
            return;
        }

        bumped.getVessel().appendDamage(bumper.getVessel().getRamDamage());
    }

    private boolean canBumpPlayer(Player bumper, Player bumped) {
        VesselMovementAnimation anim = VesselMovementAnimation.getBumpAnimation(bumper, bumped);
        return bumper.getVessel().getSize() >= bumped.getVessel().getSize() && !bumped.isSunk() && !isOutOfBounds(anim.getPositionForAnimation(bumped));
    }

    private boolean isOutOfBounds(Position pos) {
        return pos.getX() < 0 || pos.getX() > 19 || pos.getY() < 0 || pos.getY() > 35;
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
                            Player player = players.getPlayerByPosition(source.getX(), source.getY() + i);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 6:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX() + i, source.getY());
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 10:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX(), source.getY() - i);
                            if (player != null) {
                                System.out.println("found!");
                                return player;
                            }
                        }
                        break;
                    case 14:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX() - i, source.getY());
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
                            Player player = players.getPlayerByPosition(source.getX(), source.getY() - i);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 6:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX() - i, source.getY());
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 10:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX(), source.getY() + i);
                            if (player != null) {
                                return player;
                            }
                        }
                        break;
                    case 14:
                        for (int i = 1; i < 4; i++) {
                            Player player = players.getPlayerByPosition(source.getX() + i, source.getY());
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

    private boolean checkBoundCollision(Player player, int turn, int phase) {
        MoveType move = player.getMoves().getMove(turn);
        Position pos = move.getNextPositionWithPhase(player, player.getFace(), phase);
        if (isOutOfBounds(pos)) {
            player.getCollisionStorage().setCollided(turn, phase);
            return true;
        }
        return false;
    }
}

package com.benberi.cadesim.server.model.collision;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.model.player.PlayerManager;
import com.benberi.cadesim.server.util.Direction;

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
}

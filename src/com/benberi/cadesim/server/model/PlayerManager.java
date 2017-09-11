package com.benberi.cadesim.server.model;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.model.move.MoveAnimationStructure;
import com.benberi.cadesim.server.model.move.MoveAnimationTurn;
import com.benberi.cadesim.server.model.move.MoveType;
import com.benberi.cadesim.server.model.move.TurnMoveHandler;
import com.benberi.cadesim.server.model.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.packet.out.OutgoingPacket;
import com.benberi.cadesim.server.packet.out.impl.*;
import io.netty.channel.Channel;
import sun.security.krb5.internal.PAData;

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

    private long lastTimeSend;

    public PlayerManager(ServerContext context) {
        this.context = context;
    }

    /**
     * Ticks all players
     */
    public void tick() {
        if (System.currentTimeMillis() - lastTimeSend >= 1000) {
            sendTime();
            lastTimeSend = System.currentTimeMillis();
        }
    }

    public void handleTurn() {
        // Send tokens

        int maxSlotsFilled = 0;
        int maxShotsFilled = 0;

        List<Player> registered = listRegisteredPlayers();

        // Go through all players
        for (Player p : registered) {
            // His moves selected
            TurnMoveHandler turns = p.getMoves();

            // Go through all 4 moves
            for(int slot = 0; slot < 4; slot++) {
                MoveType move = turns.getMove(slot);
                int leftShoots = turns.getLeftCannons(slot);
                int rightShoots = turns.getRightCannons(slot);

                MoveAnimationTurn turnAnimation = p.getAnimationStructure().getTurn(slot);
                turnAnimation.setAnimation(VesselMovementAnimation.getIdForMoveType(move));
                if (slot == 0) {
                    turnAnimation.setLeftShoots(2);
                }
                else {
                    turnAnimation.setLeftShoots(leftShoots);
                }
                turnAnimation.setRightShoots(rightShoots);
            }

            int count = p.getAnimationStructure().countFilledTurnSlots();
            int countShoots = p.getAnimationStructure().countFilledShootSlots();

            if (count > maxSlotsFilled) {
                maxSlotsFilled = count;
            }

            if (countShoots > maxShotsFilled) {
                maxShotsFilled = countShoots;
            }
        }

        // Send packets to the players
        for (Player p : registered) {
            SendPlayersAnimationStructurePacket packet = new SendPlayersAnimationStructurePacket();
            packet.setPlayers(registered);
            p.sendPacket(packet);
            p.getMoves().resetTurn();
            p.sendTokens();
        }

        context.getTimeMachine().setTurnResetDelay(System.currentTimeMillis() + (maxSlotsFilled * 800) + (maxShotsFilled * 2000));
    }

    public void sendMoveBar(Player pl) {
        for (Player p : listRegisteredPlayers()) {
            SendPlayerMoveBar packet = new SendPlayerMoveBar();
            packet.setPlayerName(pl.getName());
            packet.setMoves(p.getMoves());

            p.sendPacket(packet);
        }
    }

    public List<Player> listRegisteredPlayers() {
        List<Player> registered = new ArrayList<>();
        for (Player p : players) {
            if (p.isRegistered()) {
                registered.add(p);
            }
        }

        return registered;
    }

    public void registerPlayer(Channel c) {
        Player player = new Player(context, c);
        this.players.add(player);
        logger.info("A new player attempts to join the game: " + c.remoteAddress());
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
    }

    /**
     * Sends and updates the time of the game, turn for all players
     */
    private void sendTime() {

        for (Player player : players) {
            if (!player.isRegistered()) {
                continue;
            }
            BlockadeTimeMachine machine = context.getTimeMachine();
            SendTimePacket packet = new SendTimePacket();
            packet.setGameTime(machine.getGameTime());
            packet.setTurnTime(machine.getTurnTime());

            player.sendPacket(packet);
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
        for (Player p : players) {
            if (p.isRegistered() && p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public void sendPlayerForAll(Player player) {
        for (Player p : players) {
            if (p == player) {
                continue;
            }

            AddPlayerShipPacket packet = new AddPlayerShipPacket();
            packet.setName(player.getName());
            packet.setX(player.getX());
            packet.setY(player.getY());
            packet.setFace(player.getFace());

            p.sendPacket(packet);
        }
    }

    /**
     * Handles a player login validation
     * @param pl        The player to check
     * @param name      The name to register
     */
    public void handlePlayerLogin(Player pl, String name) {
        int response = LoginResponsePacket.SUCCESS;

        if (getPlayerByName(name) != null) {
            response = LoginResponsePacket.NAME_IN_USE;
        }

        pl.register(name);


        // Send a response packet
        LoginResponsePacket login = new LoginResponsePacket();
        login.setResponse(response);
        pl.sendPacket(login);

        // Send map
        pl.sendBoard();

        // Send his ship
        SendPlayersPacket sendPlayersPacket = new SendPlayersPacket();
        sendPlayersPacket.setPlayers(players);
        pl.sendPacket(sendPlayersPacket);

        // send the damage of the vessel
        pl.sendDamage();

        // Send move tokens
        pl.sendTokens();

        sendPlayerForAll(pl);

    }

    public void resetMoveBars() {
        for (Player p : listRegisteredPlayers()) {
            sendMoveBar(p);
        }
    }
}

package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.config.Constants;
import com.benberi.cadesim.server.ServerContext;

public class BlockadeTimeMachine {

    /**
     * The timer of the blockade
     */
    private int gameTime = Constants.BLOCKADE_TIME;

    /**
     * The current turn time
     */
    private int turnTime = Constants.TURN_TIME;

    /**
     * The server context
     */
    private ServerContext context;

    public BlockadeTimeMachine(ServerContext context) {
        this.context = context;
    }

    private long turnResetDelay = -1;

    public boolean lock;

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        if (gameTime == 0) {
            // TODO end?
        }

        gameTime--; // Tick blockade time

        if (turnTime == 0) {
            if (!lock) {
                try {
                    context.getPlayerManager().handleTurns();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }

        turnTime--; // Tick turn time
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public void setTurnResetDelay(long delay) {
        this.turnResetDelay = delay;
    }

    /**
     * @return Checks if there is a turn delay
     */
    public boolean hasLock() {
        return lock;
    }

    /**
     * Gets the blockade time
     * @return {@link #gameTime}
     */
    public int getGameTime() {
        return gameTime / 10;
    }

    /**
     * Gets the current turn time
     * @return {@link #turnTime}
     */
    public int getTurnTime() {
        return turnTime / 10;
    }

    /**
     * Renewals the turn time
     */
    public void renewTurn() {
        turnTime = Constants.TURN_TIME;
        turnResetDelay = -1;
        setLock(false);
    }
}

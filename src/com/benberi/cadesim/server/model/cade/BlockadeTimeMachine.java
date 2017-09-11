package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.Constants;
import com.benberi.cadesim.server.ServerContext;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

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

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        if (gameTime == 0) {
            // TODO end?
        }

        gameTime--; // Tick blockade time

        if (turnTime == 0) {
            if (turnResetDelay == -1) {
                try {
                    context.getPlayerManager().handleTurn();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (System.currentTimeMillis() >= turnResetDelay) {
                renewTurn();
            }
            return;
        }

        turnTime--; // Tick turn time
    }


    public void setTurnResetDelay(long delay) {
        this.turnResetDelay = delay;
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
    private void renewTurn() {
        turnTime = Constants.TURN_TIME;
        turnResetDelay = -1;
        context.getPlayerManager().resetMoveBars();
    }
}

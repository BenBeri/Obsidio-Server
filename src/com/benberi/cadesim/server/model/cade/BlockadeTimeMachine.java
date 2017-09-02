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

    /**
     * The main tick of blockade time machine
     */
    public void tick() {
        if (gameTime == 0) {
            // TODO end?
        }

        gameTime--; // Tick blockade time

        if (turnTime == 0) {
            renewTurn();
            return;
        }

        turnTime--; // Tick turn time
    }


    /**
     * Gets the blockade time
     * @return {@link #gameTime}
     */
    public int getGameTime() {
        return gameTime;
    }

    /**
     * Gets the current turn time
     * @return {@link #turnTime}
     */
    public int getTurnTime() {
        return turnTime;
    }

    /**
     * Renewals the turn time
     */
    private void renewTurn() {
        turnTime = Constants.TURN_TIME;
    }
}

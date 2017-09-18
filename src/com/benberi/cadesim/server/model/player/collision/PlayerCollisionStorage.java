package com.benberi.cadesim.server.model.player.collision;

import com.benberi.cadesim.server.model.player.Player;

public class PlayerCollisionStorage {

    /**
     * The player instance
     */
    private Player player;

    private PlayerCollisionReference[] collisions = new PlayerCollisionReference[4];

    public PlayerCollisionStorage(Player player) {
        this.player = player;
    }

    /**
     * Stores a collision
     * @param turn  The turn to store at
     * @param phase The phase the collision happened at
     */
    public void setCollided(int turn, int phase) {
        this.collisions[turn] = new PlayerCollisionReference(player, phase);
    }

    /**
     * Check if the player collided at given turn
     * @param turn  The turn to check
     * @return <code>true</code> If collision happened, else false
     */
    public boolean isCollided(int turn) {
        return getCollisionRerefence(turn) != null;
    }

    /**
     * Gets the collision reference by turn
     * @param turn  The turn
     * @return The collision reference if found, or null if not
     */
    public PlayerCollisionReference getCollisionRerefence(int turn) {
        return this.collisions[turn];
    }

    /**
     * Clears the storage
     */
    public void clear() {
        this.collisions = new PlayerCollisionReference[4];
    }
}

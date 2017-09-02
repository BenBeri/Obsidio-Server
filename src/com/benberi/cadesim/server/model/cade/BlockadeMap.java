package com.benberi.cadesim.server.model.cade;

import com.benberi.cadesim.server.ServerContext;
import com.sun.security.ntlm.Server;

public class BlockadeMap {

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;

    public static final int TILE = 0;
    public static final int SAFE_TILE = 1;
    public static final int SMALL_ROCK = 2;
    public static final int BIG_ROCK = 3;

    public static final int WIND_NORTH = 4;
    public static final int WIND_SOUTH = 5;
    public static final int WIND_WEST = 6;
    public static final int WIND_EAST = 7;

    public static final int WP_NW = 8;
    public static final int WP_NE = 9;
    public static final int WP_SW = 10;
    public static final int WP_SE = 11;

    public static final int FLAG_1 = 12;
    public static final int FLAG_2 = 13;
    public static final int FLAG_3 = 14;


    /**
     * The map
     */
    private int[][] map = new int[MAP_WIDTH][MAP_HEIGHT];

    /**
     * The server context
     */
    private ServerContext context;

    public BlockadeMap(ServerContext ctx) {
        this.context = ctx;

        for (int i = 0; i < map.length; i++) {
            for(int j = 0; j < map[i].length; j++) {
                if (j < 3) {
                    map[i][j] = SAFE_TILE;
                }
                else {
                    map[i][j] = TILE;
                }

            }
        }
    }

    public int[][] getMap() {
        return this.map;
    }
}

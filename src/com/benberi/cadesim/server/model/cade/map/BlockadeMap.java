package com.benberi.cadesim.server.model.cade.map;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.model.player.vessel.VesselFace;
import com.benberi.cadesim.server.util.Position;

public class BlockadeMap {

    /**
     * Map dimension-x
     */
    public static final int MAP_WIDTH = 20;

    /**
     * Map dimension-y
     */
    public static final int MAP_HEIGHT = 36;

    public static final int BIG_ROCK = 1;
    public static final int SMALL_ROCK = 2;

    public static final int WIND_WEST = 3;
    public static final int WIND_EAST = 4;
    public static final int WIND_NORTH = 5;
    public static final int WIND_SOUTH = 6;

    public static final int WP_NW = 7;
    public static final int WP_NE = 8;
    public static final int WP_SW = 9;
    public static final int WP_SE = 10;

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
        this.map = MapType.DEFAULT.load();
    }

    public int[][] getMap() {
        return this.map;
    }

    public boolean isRock(int x, int y) {
        if (isOutOfBounds(x,y)) {
            return false;
        }
        return this.map[x][y] == BIG_ROCK || this.map[x][y] == SMALL_ROCK;
    }

    public boolean isBigRock(int x, int y) {
        if (isOutOfBounds(x,y)) {
            return false;
        }
         return this.map[x][y] == BIG_ROCK;
    }

    public boolean isSafe(Position p) {
        return p.getY() <= 2 || p.getY() >= 33;
    }

    public boolean isOutOfBounds(int x, int y) {
        return x > 19 | x < 0 || y > 35 || y < 0;
    }

    public boolean isActionTile(int x, int y) {
        return this.map[x][y] >= 3 && this.map[x][y] <= 10;
    }

    public int getTile(int x, int y) {
        if (isOutOfBounds(x,y)) {
            return 0;
        }
        return this.map[x][y];
    }

    public Position getNextActionTilePosition(int tile, Position pos, int phase) {
        if (tile == -1) {
            tile = map[pos.getX()][pos.getY()];
        }
        // No phase 2 (2 because 0 and 1) for winds
        if (tile >= 3 && tile <= 6 && phase == 1) {
            return pos;
        }

        switch (this.map[pos.getX()][pos.getY()]) {
            case WIND_NORTH:
                return pos.copy().addY(1);
            case WIND_SOUTH:
                return pos.copy().addY(-1);
            case WIND_WEST:
                return pos.copy().addX(-1);
            case WIND_EAST:
                return pos.copy().addX(1);

            case WP_NE:
                switch (phase) {
                    case 0:
                        return pos.copy().addY(-1);
                    case 1:
                        return pos.copy().addX(-1);
                }
            case WP_NW:
                switch (phase) {
                    case 0:
                        return pos.copy().addX(1);
                    case 1:
                        return pos.copy().addY(-1);
                }
            case WP_SE:
                switch (phase) {
                    case 0:
                        return pos.copy().addX(-1);
                    case 1:
                        return pos.copy().addY(1);
                }
            case WP_SW:
                switch (phase) {
                    case 0:
                        return pos.copy().addY(1);
                    case 1:
                        return pos.copy().addX(1);
                }
        }

        return pos;
    }

    public VesselFace getNextActionTileFace(VesselFace face) {
        return face.getNext();
    }

    public Position getNextActionTilePositionForTile(Position pos, int tile) {
        switch (tile) {
            case WIND_NORTH:
                return pos.copy().addY(1);
            case WIND_SOUTH:
                return pos.copy().addY(-1);
            case WIND_WEST:
                return pos.copy().addX(-1);
            case WIND_EAST:
                return pos.copy().addX(1);
        }

        return pos;
    }

    public boolean isWhirlpool(int tile) {
        return tile >= 7 && tile <= 10;
    }
}

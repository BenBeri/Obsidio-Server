package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.cade.BlockadeMap;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class SendMapPacket extends Packet {

    public SendMapPacket(BlockadeMap bmap) {
        super(3);

        int[][] map = bmap.getMap();

        setPacketLengthType(PacketLength.SHORT);

        int size = 0;

       for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int tile = map[x][y];
                if (tile != 0) {
                    size += 3;
                }
            }
        }

        System.out.println("setting size: " + size);
        setLength(size);

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int tile = map[x][y];
                if (tile != 0) {
                    writeByte(tile);
                    writeByte(x);
                    writeByte(y);
                }
            }
        }

    }
}

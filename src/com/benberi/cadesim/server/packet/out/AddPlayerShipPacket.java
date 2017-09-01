package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class AddPlayerShipPacket extends Packet {

    public AddPlayerShipPacket(int opcode) {
        super(opcode);
    }
}

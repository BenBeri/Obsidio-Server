package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

/**
 * Removes a player ship from the board
 */
public class RemovePlayerShipPacket extends Packet {

    public RemovePlayerShipPacket(int opcode) {
        super(opcode);
    }
}

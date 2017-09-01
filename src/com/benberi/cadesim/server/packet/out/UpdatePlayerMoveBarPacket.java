package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

/**
 * When a player places a move, server will send all clients that this player updated his moves, and this will
 * update the bar for that player in all clients
 */
public class UpdatePlayerMoveBarPacket extends Packet {

    public UpdatePlayerMoveBarPacket(int opcode) {
        super(opcode);
    }
}

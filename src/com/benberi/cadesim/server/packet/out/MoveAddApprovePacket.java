package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

/**
 * When a player tries to place a move, and the server approves that placement,
 * this packet will let the player's client know that the move placement was successful
 */
public class MoveAddApprovePacket extends Packet {

    public MoveAddApprovePacket(int opcode) {
        super(opcode);
    }
}

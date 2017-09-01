package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

/**
 * When a new player joins we need to notify the client that he has
 * successfully joined the game
 */
public class PlayerRegisteredPacket extends Packet {

    public PlayerRegisteredPacket(int opcode) {
        super(opcode);
    }
}

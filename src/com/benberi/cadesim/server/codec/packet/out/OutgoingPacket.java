package com.benberi.cadesim.server.codec.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;

public abstract class OutgoingPacket extends Packet {

    public OutgoingPacket(int opcode) {
        super(opcode);
    }

    /**
     * Encodes the packet
     */
    public abstract void encode();
}

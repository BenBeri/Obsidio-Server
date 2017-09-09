package com.benberi.cadesim.server.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.packet.out.OutgoingPacket;

public class MovePlaceVerificationPacket extends OutgoingPacket {

    private int slot;
    private int move;

    public MovePlaceVerificationPacket() {
        super(OutGoingPackets.PLACE_MOVE);
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setMove(int move) {
        this.move = move;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(slot);
        writeByte(move);
        setLength(getBuffer().readableBytes());
    }
}

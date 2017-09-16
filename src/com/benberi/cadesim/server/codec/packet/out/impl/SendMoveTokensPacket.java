package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendMoveTokensPacket extends OutgoingPacket {

    private int left;
    private int right;
    private int forward;
    private int cannons;

    public SendMoveTokensPacket() {
        super(OutGoingPackets.SEND_MOVE_TOKENS);
    }


    public void setLeft(int left) {
        this.left = left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void setForward(int forward) {
        this.forward = forward;
    }

    public void setCannons(int cannons) {
        this.cannons = cannons;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);
        writeByte(left);
        writeByte(forward);
        writeByte(right);
        writeByte(cannons);
        setLength(getBuffer().readableBytes());
    }
}

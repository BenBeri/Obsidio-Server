package com.benberi.cadesim.server.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;
import com.benberi.cadesim.server.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendDamagePacket extends OutgoingPacket {

    private int damage;
    private int bilge;

    public SendDamagePacket() {
        super(OutGoingPackets.SHIP_DAMAGE_BILGE);
    }

    public void setDamage(double damage) {
        this.damage = (int) damage;
    }

    public void setBilge(int bilge) {
        this.bilge = bilge;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        writeByte(damage);
        writeByte(bilge);
        setLength(getBuffer().readableBytes());
    }
}

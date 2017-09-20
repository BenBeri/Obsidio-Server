package com.benberi.cadesim.server.codec.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

public class LoginResponsePacket extends OutgoingPacket {

    public static final int SUCCESS = 0;
    public static final int NAME_IN_USE = 1;
    public static final int SERVER_FULL = 2;

    /**
     * The response
     */
    private int response = SUCCESS;

    public LoginResponsePacket() {
        super(OutGoingPackets.LOGIN_RESPONSE);
    }

    public void setResponse(int response) {
        this.response = response;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.BYTE);
        setLength(1);
        writeByte(response);
    }
}

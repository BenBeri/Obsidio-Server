package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;

import java.util.List;

public class SendPlayersPacket extends Packet {

    public SendPlayersPacket(List<Player> players) {
        super(4);

        setPacketLengthType(PacketLength.SHORT);

        for (Player p : players) {
            if (!p.isRegistered()) {
                continue;
            }

            writeByteString(p.getName());
            writeByte(p.getX());
            writeByte(p.getY());
            writeByte(p.getFace());
        }

        setLength(getBuffer().readableBytes());
    }
}

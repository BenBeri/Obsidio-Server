package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;

/**
 * Adds a player ship to the game board (position, ship type, player name, and ID)
 */
public class AddPlayerShipPacket extends Packet {

    public AddPlayerShipPacket(Player p) {
        super(2);
        setPacketLengthType(PacketLength.BYTE);
        setLength(p.getName().getBytes().length + 2 + 1 + 1);

        writeByteString(p.getName());
        writeByte(p.getX()); // x
        writeByte(p.getY()); // y
        writeByte(p.getFace()); // face
    }
}

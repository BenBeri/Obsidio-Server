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
        setLength(3);
        writeByte(10);
        writeByte(1); // x
        writeByte(1); // y
        writeByte(0); // face
    }
}

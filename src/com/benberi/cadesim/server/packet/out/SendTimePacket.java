package com.benberi.cadesim.server.packet.out;

import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.cade.BlockadeTimeMachine;

/**
 * Sends the blockade time and current turn time
 */
public class SendTimePacket extends Packet {

    public SendTimePacket(BlockadeTimeMachine time) {
        super(0);

        // The blockade time
        int blockadeTime = time.getGameTime();

        // The turn time
        int turnTime = time.getTurnTime();

        setPacketLengthType(PacketLength.BYTE);
        setLength(8); // 2 integers 8 bits
        writeInt(blockadeTime); // blockade time
        writeInt(turnTime); // turn time
    }
}

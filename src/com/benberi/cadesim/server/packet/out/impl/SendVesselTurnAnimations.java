package com.benberi.cadesim.server.packet.out.impl;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.vessel.VesselMovementAnimation;
import com.benberi.cadesim.server.packet.out.OutgoingPacket;

import java.util.List;

public class SendVesselTurnAnimations extends OutgoingPacket {

    private List<Player> players;

    public SendVesselTurnAnimations() {
        super(OutGoingPackets.TURN_VESSEL_ANIMS);
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public void encode() {
        setPacketLengthType(PacketLength.SHORT);

        // How many players
        writeByte(players.size());

        for (Player p : players) {

            // Write the name of the player
            writeByteString(p.getName());

            List<VesselMovementAnimation> animations = p.getAnimations();

            // Go through 4 moves
            for (VesselMovementAnimation anim : animations) {
                // write anim ID
                writeByte(anim.getId());
            }
        }

        setLength(getBuffer().readableBytes());
    }
}

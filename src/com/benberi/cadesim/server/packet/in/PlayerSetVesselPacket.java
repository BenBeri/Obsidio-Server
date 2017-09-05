package com.benberi.cadesim.server.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.packet.ServerPacketExecutor;

public class PlayerSetVesselPacket extends ServerPacketExecutor {

    public PlayerSetVesselPacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {

    }
}

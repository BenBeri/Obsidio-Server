package com.benberi.cadesim.server.packet.in;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.move.MoveType;
import com.benberi.cadesim.server.model.player.Player;
import com.benberi.cadesim.server.packet.ServerPacketExecutor;

public class SetSealGenerationTargetPacket extends ServerPacketExecutor {

    public SetSealGenerationTargetPacket(ServerContext ctx) {
        super(ctx);
    }

    @Override
    public void execute(Player pl, Packet p) {
        int type = p.readByte();
        pl.getMoveTokens().setTargetTokenGeneration(MoveType.forId(type), false);
    }
}

package com.benberi.cadesim.server.packet;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;
import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.packet.in.PlayerRegisterPacket;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ServerPacketManager {

    private Map<Integer, ServerPacketExecutor> executors = new HashMap<Integer, ServerPacketExecutor>();

    private ServerContext context;

    public ServerPacketManager(ServerContext context) {
        this.context = context;
        registerPackets();
    }

    private void registerPackets() {
        executors.put(0, new PlayerRegisterPacket(context));
    }

    public boolean process(Channel c, Packet packet) {
        System.out.println("trying to process packet");

        Player p = context.getPlayerManager().getPlayerByChannel(c);
        if (p == null) {
            System.err.println("unknown player for channel");
            return false;
        }

        for(Map.Entry<Integer, ServerPacketExecutor> entry : executors.entrySet()) {
            int opcode = entry.getKey();
            ServerPacketExecutor executor = entry.getValue();

            if (packet.getOpcode() == opcode) {
                System.out.println("peee peee");
                executor.execute(p, packet);
                return true;
            }
        }
        return false;
    }
}

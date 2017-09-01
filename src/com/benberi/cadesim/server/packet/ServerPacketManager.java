package com.benberi.cadesim.server.packet;

import com.benberi.cadesim.server.ServerContext;
import com.benberi.cadesim.server.codec.util.Packet;

import java.util.HashMap;
import java.util.Map;

public class ServerPacketManager {

    private Map<Integer, ServerPacketExecutor> executors = new HashMap<Integer, ServerPacketExecutor>();

    private ServerContext context;

    public ServerPacketManager(ServerContext ctx) {
        this.context = ctx;
    }

    public boolean process(Packet packet) {
        for(Map.Entry<Integer, ServerPacketExecutor> entry : executors.entrySet()) {
            int opcode = entry.getKey();
            ServerPacketExecutor executor = entry.getValue();

            if (packet.getOpcode() == opcode) {
                executor.execute(packet);
                return true;
            }
        }
        return false;
    }
}

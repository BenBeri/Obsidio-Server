package com.benberi.cadesim.server.codec.util;

import com.benberi.cadesim.server.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import sun.rmi.runtime.Log;

import java.util.logging.Logger;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private Logger logger = Logger.getLogger("encoder");

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buffer) throws Exception {

        if (Constants.DEBUG_PACKETS && packet.getOpcode() != 3) {
            logger.info("Writing packet in channel " + ctx.channel().remoteAddress() + " packet: " + packet.toString());
        }

        int opcode = packet.getOpcode();
        int lengthType = packet.getPacketLengthType();
        int length = packet.getLength();

        PacketLength type = PacketLength.get(lengthType);

        buffer.writeByte(opcode);
        buffer.writeByte(lengthType);

        assert type != null;
        switch(type) {
            case BYTE:
                buffer.writeByte(length);
                break;
            case SHORT:
                buffer.writeShort(length);
                break;
            case MEDIUM:
                buffer.writeMedium(length);
                break;
        }

        buffer.writeBytes(packet.getBuffer());
    }
}

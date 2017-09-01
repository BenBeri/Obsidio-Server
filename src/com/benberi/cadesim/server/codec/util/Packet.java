package com.benberi.cadesim.server.codec.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @author Jony | Ben
 */
public class Packet {

    /**
     * Packets opcode
     */
    private int opcode;

    private int packetLength;

    private int length;

    /**
     * The data buffer of the packet
     */
    private ByteBuf dataBuffer;


    public Packet(int opcode) {
        this(opcode, Unpooled.buffer());
    }

    public Packet(int opcode, ByteBuf data) {
        this.opcode = opcode;
        this.dataBuffer = data;
    }

    public Packet(int opcode, int size) {
        this.opcode = opcode;
        this.dataBuffer = Unpooled.buffer(size);

    }

    public int getPacketLengthType() {
        return this.packetLength;
    }

    public void setPacketLengthType(PacketLength length) {
        this.packetLength = length.getType();
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

    public int getSize() {
        return dataBuffer.readableBytes() + 1;
    }

    public void setOpcode(byte opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return this.opcode;
    }

    public int readInt() {
        return this.dataBuffer.readInt();
    }

    public byte readByte() {
        return this.dataBuffer.readByte();
    }

    public String readString() {
        byte[] bytes = new byte[dataBuffer.readUnsignedByte()];
        dataBuffer.readBytes(bytes);
        return new String(bytes);
    }

    public void writeString(String s) {
        this.dataBuffer.writeBytes(s.getBytes());
    }

    public void writeInt(int integer) {
        this.dataBuffer.writeInt(integer);
    }

    public void writeBytes(byte[] bytes) {
        this.dataBuffer.writeBytes(bytes);
    }

    public void writeByte(int b) {
        this.dataBuffer.writeByte(b);
    }

    public ByteBuf getBuffer() {
        return dataBuffer;
    }

    @Override
    public String toString() {
        return "[Opcode: " + opcode + ", Length: " + length + " length-type: " + packetLength + " buffer-size: " + dataBuffer.capacity();
    }
}
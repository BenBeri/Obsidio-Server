package com.benberi.cadesim.server.codec.packet.out.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.benberi.cadesim.server.codec.OutGoingPackets;
import com.benberi.cadesim.server.codec.util.PacketLength;
import com.benberi.cadesim.server.codec.packet.out.OutgoingPacket;

/**
 * Sends the blockade time and current turn time
 */
public class SendTeamNamesPacket extends OutgoingPacket {

    private String attacker = "Attacker";
    private String defender = "Defender";

    public SendTeamNamesPacket() {
        super(OutGoingPackets.SET_TEAM_NAMES);
    }

    public void getTeamNames() throws Exception {
      this.attacker = attacker;
      this.defender = defender;
    }


    @Override
    public void encode() {
    	try {
			getTeamNames();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        String attacker = this.attacker;
        String defender = this.defender;

        setPacketLengthType(PacketLength.BYTE);
        writeByteString(attacker);
        writeByteString(defender);
        setLength(getBuffer().readableBytes());
    }
}

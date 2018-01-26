package com.benberi.cadesim.server.codec.packet.out.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public void getTeamNames() throws ClassNotFoundException, SQLException {
        
        String myDriver = "org.gjt.mm.mysql.Driver";
        String myUrl = "jdbc:mysql://localhost:3306/cadesim?useSSL=false";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myUrl, "cadesim-server", "Ld2qYpYEfdb2WCzA");
                
        String query = "SELECT * FROM matches ORDER BY match_id DESC LIMIT 1";

        Statement st = conn.createStatement();
        
        ResultSet rs = st.executeQuery(query);
        
        while (rs.next())
        {
          String attacker = rs.getString("match_attacker");
          String defender = rs.getString("match_defender");
          
          this.attacker = attacker;
          this.defender = defender;
        }
        st.close();
    }


    @Override
    public void encode() {
    	try {
			getTeamNames();
		} catch (ClassNotFoundException | SQLException e) {
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

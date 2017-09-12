package com.benberi.cadesim.server.model.move;

import com.benberi.cadesim.server.model.Player;
import com.benberi.cadesim.server.model.vessel.Vessel;
import com.benberi.cadesim.server.model.vessel.VesselFace;

public enum MoveType {

    FORWARD(2),
    LEFT(1),
    RIGHT(3),
    NONE(0);

    private int id;

    MoveType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static MoveType forId(int id) {
        for(MoveType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return null;
    }

    public VesselFace getNextFace(VesselFace face) {
        switch (this) {
            case NONE:
            case FORWARD:
                return face;
            case LEFT:
                int newFace = face.getDirectionId() - 4;
                if (newFace < 0) {
                    newFace = 14;
                }
                return VesselFace.forId(newFace);
            case RIGHT:
                newFace = face.getDirectionId() + 4;
                if (newFace > 14) {
                    newFace = 2;
                }

                return VesselFace.forId(newFace);
        }

        return VesselFace.NORTH;
    }

    public void setNextPosition(Player player) {
       switch (this) {
           case FORWARD:
               switch (player.getFace()) {
                   case NORTH:
                       player.setPosition(player.getX(), player.getY() + 1);
                       break;
                   case SOUTH:
                       player.setPosition(player.getX(), player.getY() - 1);
                       break;
                   case WEST:
                       player.setPosition(player.getX() - 1, player.getY());
                       break;
                   case EAST:
                       player.setPosition(player.getX() + 1, player.getY());
                       break;
               }
               break;
           case LEFT:
               switch (player.getFace()) {
                   case NORTH:
                       player.setPosition(player.getX() - 1, player.getY() + 1);
                       break;
                   case SOUTH:
                       player.setPosition(player.getX() + 1, player.getY() - 1);
                       break;
                   case WEST:
                       player.setPosition(player.getX() - 1, player.getY() - 1);
                       break;
                   case EAST:
                       player.setPosition(player.getX() + 1, player.getY() + 1);
                       break;
               }
               break;
           case RIGHT:
               switch (player.getFace()) {
                   case NORTH:
                       player.setPosition(player.getX() + 1, player.getY() + 1);
                       break;
                   case SOUTH:
                       player.setPosition(player.getX() - 1, player.getY() - 1);
                       break;
                   case WEST:
                       player.setPosition(player.getX() - 1, player.getY() + 1);
                       break;
                   case EAST:
                       player.setPosition(player.getX() + 1, player.getY() - 1);
                       break;
               }
               break;
       }
    }
}

package com.benberi.cadesim.server.model.move;

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

}

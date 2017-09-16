package com.benberi.cadesim.server.util;


import javafx.geometry.Pos;

public class Position {

    /**
     * X-axis position
     */
    private int x;

    /**
     * Y-axis position
     */
    private int y;

    public Position() {
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }


    public Position addX(int toAdd) {
        this.x += toAdd;
        return this;
    }

    public Position addY(int toAdd) {
        this.y += toAdd;
        return this;
    }

    public Position add(int x, int y) {
        addX(x);
        addY(y);
        return this;
    }

    public Position set(int x, int y) {
        setX(x);
        setY(y);
        return this;
    }

    public Position set(Position other) {
        return set(other.getX(), other.getY());
    }


    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void resetPosition() {
        set(0, 0);
    }

    /**
     * Calculates disstance between two positions
     * @param other The other position target
     * @return  The disstance between the two positions
     */
    public double distance(Position other) {
        return Math.sqrt(Math.pow(x - other.x, 2) +  Math.pow(y - other.y, 2));
    }

    /**
     * Copies this position object
     * @return The copied object
     */
    public Position copy() {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Position) {
            Position other = (Position) o;
            return o == this || this.x == other.getX() && other.getY() == this.y;
        }

        return super.equals(o);
    }
}

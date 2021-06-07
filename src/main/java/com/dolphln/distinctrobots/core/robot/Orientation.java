package com.dolphln.distinctrobots.core.robot;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public enum Orientation {

    NORTH(new Vector(0, 0, -1), "NORTH", 180),
    EAST(new Vector(1, 0, 0), "EAST", 270),
    WEST(new Vector(-1, 0, 0), "WEST", 90),
    SOUTH(new Vector(0, 0, 1), "SOUTH", 0),;

    private Vector blockLocationAdd;
    private String type;
    private float yaw;

    Orientation(Vector blockLocationAdd, String type, float yaw) {
        this.blockLocationAdd = blockLocationAdd;
        this.type = type;
        this.yaw = yaw;
    }

    public Vector getBlockLocationAdd() {
        return blockLocationAdd;
    }

    public String getType() {
        return type;
    }

    public float getYaw() {
        return yaw;
    }

    public static Orientation getOrientation(Location loc) {
        return getOrientation(loc.getYaw());
    }

    public static Orientation getOrientation(Float yaw) {
        boolean negative = yaw < 0;
        yaw = Math.abs(yaw);
        if (yaw >= 135 && yaw <= 225) {
            return NORTH;
        } else if (yaw >= 225 && yaw <= 315) {
            return negative ? WEST : EAST;
        } else if (yaw >= 45 && yaw <= 135) {
            return negative ? EAST : WEST;
        } else {
            return SOUTH;
        }
    }
}

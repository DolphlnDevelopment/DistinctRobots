package com.dolphln.distinctrobots.core;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class BasicLocation {

    private final int x;
    private final int y;
    private final int z;
    private float yaw;
    private final String world;

    public BasicLocation(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.yaw = loc.getYaw();
        this.world = loc.getWorld().getName();
    }

    public BasicLocation(int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0;
        this.world = world;
    }

    public BasicLocation(int x, int y, int z, float yaw, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.world = world;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, yaw, 0f);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public String getWorld() {
        return world;
    }

    public BasicLocation add(Vector vector) {
        return new BasicLocation(getX()+vector.getBlockX(), getY()+vector.getBlockY(), getZ()+vector.getBlockZ(), getWorld());
    }

    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof BasicLocation)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        BasicLocation basicLocation = (BasicLocation) o;

        // Compare the data members and return accordingly
        Location loc1 = getLocation();
        Location loc2 = basicLocation.getLocation();
        System.out.println(loc1.toString());
        System.out.println(loc2.toString());
        return loc1.getBlockX() == loc2.getBlockX() &&
                loc1.getBlockY() == loc2.getBlockY() &&
                loc1.getBlockZ() == loc2.getBlockZ() &&
                loc1.getWorld().getName().equals(loc2.getWorld().getName());
    }

}

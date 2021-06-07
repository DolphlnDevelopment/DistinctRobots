package com.dolphln.distinctrobots;

import com.dolphln.distinctrobots.core.BasicLocation;
import com.dolphln.distinctrobots.core.Robot;
import com.dolphln.distinctrobots.utils.ItemUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class RobotManager {

    private final DistinctRobots plugin;

    private HashMap<Identifier, Robot> robots;
    private HashMap<UUID, ArrayList<Identifier>> playerRobots;
    private HashMap<BasicLocation, Identifier> locationRobots;

    public RobotManager(DistinctRobots plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {
        this.robots = new HashMap<>();
        this.playerRobots = new HashMap<>();
        this.locationRobots = new HashMap<>();
    }

    public void joinPlayer(Player p) {
        YamlConfiguration pdata = plugin.getStorageManager().getCacheConfig(p.getUniqueId());
        ArrayList<Identifier> robots = new ArrayList<>();

        for (String robotData : pdata.getConfigurationSection("").getKeys(false)) {
            int id = Integer.parseInt(robotData);
            Robot robot = new Robot(id, p.getUniqueId(), plugin.getConfigFile().getConfig(), pdata);

            if (robot.getPlacedBlock().getType() != Material.BEDROCK) {
                removeRobot(p, robot, false);
                giveRobot(p, robot.getType(), 1);
                return;
            }

            Identifier identifier = new Identifier(id, p.getUniqueId());

            this.robots.put(identifier, robot);
            this.locationRobots.put(new BasicLocation(robot.getLocation()), identifier);
            robots.add(identifier);

            robot.show();
            robot.placeBlock();
            robot.makeWork();
        }
        playerRobots.put(p.getUniqueId(), robots);
    }

    public void quitPlayer(Player p) {
        for (Identifier identifier : this.playerRobots.get(p.getUniqueId())) {
            Robot robot = this.robots.get(identifier);

            robot.stopWork();
            robot.hide();
            robot.placeBedRock();

            plugin.getStorageManager().saveRobot(p.getUniqueId(), robot);
        }
    }

    public void removeRobot(Player p, Robot robot) {
        removeRobot(p, robot, true);
    }

    public void removeRobot(Player p, Robot robot, boolean removeIdentifier) {
        robot.stopWork();
        robot.hide();
        robot.claim(p, false);
        robot.removeBlock();

        if (removeIdentifier) {
            Identifier id = new Identifier(robot.getId(), p.getUniqueId());

            for (Identifier identifier : this.playerRobots.get(p.getUniqueId())) {
                if (id.equals(identifier)) {
                    this.locationRobots.remove(robots.get(identifier).getBasicLocation());
                    this.robots.remove(identifier);

                    ArrayList<Identifier> gotPlayerRobots = this.playerRobots.get(p.getUniqueId());
                    gotPlayerRobots.remove(identifier);
                    this.playerRobots.put(p.getUniqueId(), gotPlayerRobots);
                    break;
                }
            }
        }

        plugin.getStorageManager().removeRobot(p.getUniqueId(), robot);
    }

    public Robot createRobot(Player p, int id) {
        Robot robot = new Robot(id, p.getUniqueId(), plugin.getConfigFile().getConfig(), plugin.getStorageManager().getCacheConfig(p.getUniqueId()));
        Identifier identifier = new Identifier(id, p.getUniqueId());
        this.robots.put(identifier, robot);
        this.locationRobots.put(robot.getBasicLocation(), identifier);

        ArrayList<Identifier> robots = this.playerRobots.get(p.getUniqueId());
        robots.add(identifier);
        this.playerRobots.put(p.getUniqueId(), robots);

        return robot;
    }

    public boolean giveRobot(Player p, String type, int amount) {
        try {
            for (int i = 0; i < amount; i++) {
                ItemStack item = ItemUtils.generateItem("robots." + type + ".item");
                NBTItem nbtItem = new NBTItem(item);
                nbtItem.setString("robot", type);
                Random random = new Random();
                nbtItem.setString(String.valueOf(random.nextInt(999999)), String.valueOf(random.nextInt(999999)));
                if (p.getInventory().firstEmpty() == -1) {
                    p.getWorld().dropItem(p.getLocation().add(0, 1, 0), nbtItem.getItem());
                } else {
                    p.getInventory().addItem(nbtItem.getItem());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Robot[] playerRobots(Player p) {
        ArrayList<Robot> robots = new ArrayList<>();
        for (Identifier identifier : playerRobots.get(p.getUniqueId())) {
            robots.add(this.robots.get(identifier));
        }
        return robots.toArray(new Robot[robots.size()]);
    }

    public Robot getRobot(Location loc) {
        return getRobot(new BasicLocation(loc));
    }

    public Robot getRobot(BasicLocation loc) {
        Identifier robotIdentifier = this.locationRobots.get(loc);
        return this.robots.get(robotIdentifier);
    }

    public Robot getRobot(Identifier identifier) {
        return this.robots.get(identifier);
    }

    public Robot[] getRobots() {
        return this.robots.values().toArray(new Robot[this.robots.size()]);
    }

    public Identifier getIdentifier(int entid) {
        for (Identifier identifier : this.robots.keySet()) {
            if (this.robots.get(identifier).getEntityId() == entid) {
                return identifier;
            }
        }
        return null;
    }

    public class Identifier {

        private final int id;
        private final UUID uuid;

        public Identifier(int id, UUID uuid) {
            this.id = id;
            this.uuid = uuid;
        }

        public int getId() {
            return id;
        }

        public UUID getUUID() {
            return uuid;
        }

        @Override
        public String toString() {
            return "ID: " + getId() +
                    "\nOwner UUID: " + getUUID().toString();
        }

        @Override
        public boolean equals(Object o) {

            // If the object is compared with itself then return true
            if (o == this) {
                return true;
            }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
            if (!(o instanceof Identifier)) {
                return false;
            }

            // typecast o to Complex so that we can compare data members
            Identifier identifier = (Identifier) o;

            // Compare the data members and return accordingly
            return identifier.getId() == this.getId() && identifier.getUUID().equals(this.getUUID());
        }
    }


}

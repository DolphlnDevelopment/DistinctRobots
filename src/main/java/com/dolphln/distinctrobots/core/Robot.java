package com.dolphln.distinctrobots.core;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.robot.Orientation;
import com.dolphln.distinctrobots.core.robot.RobotArmor;
import com.dolphln.distinctrobots.core.robot.RobotMoney;
import com.dolphln.distinctrobots.utils.HandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import java.util.List;
import java.util.UUID;

public class Robot {

    private final int id;
    private String type;

    //private String name;
    private String colored_name;
    private String hologram;
    private Material blockFront;

    private RobotArmor robotArmor;
    private RobotMoney robotMoney;
    private Orientation robotOrientation;
    private String robotReward;

    private ArmorStand armorStand;
    private ArmorStand armorStandPickaxe;
    private UUID ownerUUID;

    /* Location */
    private BasicLocation location;

    public Robot(int id, UUID ownerUUID, YamlConfiguration config, YamlConfiguration data) {
        this.id = id;
        this.type = data.getString(String.valueOf(id) + ".type");

        String dataPath = String.valueOf(id);
        String path = "robots." + data.getString(dataPath + ".type");

        //this.name = config.getString(path + ".name").replaceAll(" ", "");
        this.colored_name = config.getString(path + ".colored-name");
        this.hologram = ChatColor.translateAlternateColorCodes('&', config.getString(path + ".hologram"));
        this.blockFront = Material.getMaterial(config.getString(path + ".block-front"));

        this.robotArmor = new RobotArmor(config, path);
        this.robotMoney = new RobotMoney(this, data.getInt(dataPath + ".remaining"), config.getInt(path + ".interval"), config.getInt(path + ".money"), data.getInt(dataPath + ".balance"));
        this.robotOrientation = Orientation.valueOf(data.getString(dataPath + ".orientation").toUpperCase());
        this.robotReward = config.getString(path + ".reward");

        this.location = new BasicLocation(
                data.getInt(dataPath + ".location.x"),
                data.getInt(dataPath + ".location.y"),
                data.getInt(dataPath + ".location.z"),
                this.robotOrientation.getYaw(),
                data.getString(dataPath + ".location.world")
        );

        this.ownerUUID = ownerUUID;
    }

    public void placeBlock() {
        getPlacedBlock().setType(this.blockFront);
    }

    public void placeBedRock() {
        getPlacedBlock().setType(Material.BEDROCK);
    }

    public void removeBlock() {
        getPlacedBlock().setType(Material.AIR);
    }

    public Block getPlacedBlock() {
        return getPlacedBlockLocation().getBlock();
    }

    public Location getPlacedBlockLocation() {
        return getLocation().add(robotOrientation.getBlockLocationAdd());
    }

    public Location getLocation() {
        return location.getLocation();
    }

    public Location getCentralizedLocation() {
        return location.getLocation().add(0.5, 0, 0.5);
    }

    public BasicLocation getBasicLocation() {
        return this.location;
    }

    public String getFormattedTime() {
        int time = this.robotMoney.getRemaining();
        if (time <= 0) {
            return "0s";
        }

        int hours = time / 3600;
        int minutes = (time - hours*3600) / 60;
        int seconds = time % 60;

        if (hours <= 0) {
            if (minutes <= 0) {
                return String.valueOf(seconds) + "s";
            } else {
                return String.valueOf(minutes) + "m " + String.valueOf(seconds) + "s";
            }
        } else {
            return String.valueOf(hours) + "h " + String.valueOf(minutes) + "m " + String.valueOf(seconds) + "s";
        }
    }

    public void show() {
        if (this.armorStand == null) {
            ArmorStand stand = (ArmorStand) location.getLocation().getWorld().spawnEntity(getCentralizedLocation(), EntityType.ARMOR_STAND);

            stand.setSmall(true);
            stand.setArms(true);
            stand.setBasePlate(false);

            stand.setVisible(true);
            stand.setGravity(false);
            //stand.setInvulnerable(true);

            stand.setCanPickupItems(false);
            //stand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);

            stand.getEquipment().setHelmet(this.robotArmor.getHelmet());
            stand.getEquipment().setChestplate(this.robotArmor.getChestplate());
            stand.getEquipment().setLeggings(this.robotArmor.getLeggings());
            stand.getEquipment().setBoots(this.robotArmor.getBoots());

            // Pickaxe Armor Stand
            this.armorStandPickaxe = spawnPickaxeStand();
            /*//2f,0f,4f
            EulerAngle rightArm = new EulerAngle(HandUtils.degreesToRadians(2F),HandUtils.degreesToRadians(0F),HandUtils.degreesToRadians(4F));
            stand.setRightArmPose(rightArm);*/
            //stand.getEquipment().setItemInHand(this.robotArmor.getHand());

            stand.setCustomNameVisible(true);
            stand.setCustomName(ChatColor.WHITE + " ");

            this.armorStand = stand;
            updateHologram();
        }
    }

    private ArmorStand spawnPickaxeStand() {
        if (this.armorStandPickaxe == null) {
            ArmorStand standPickaxe = (ArmorStand) location.getLocation().getWorld().spawnEntity(getCentralizedLocation(), EntityType.ARMOR_STAND);

            standPickaxe.setSmall(true);
            standPickaxe.setArms(true);
            standPickaxe.setBasePlate(false);

            standPickaxe.setVisible(true);
            standPickaxe.setGravity(false);

            standPickaxe.setCanPickupItems(false);
            standPickaxe.getEquipment().setItemInHand(this.robotArmor.getHand());

            //344f,0f,0f
            EulerAngle rightArm = new EulerAngle(HandUtils.degreesToRadians(6F), HandUtils.degreesToRadians(0F), HandUtils.degreesToRadians(0F));
            standPickaxe.setRightArmPose(rightArm);

            return standPickaxe;
        }
        return null;
    }

    public void updateHologram() {
        if (this.armorStand != null) {
            this.armorStand.setCustomName(
                    ChatColor.translateAlternateColorCodes('&', hologram)
                    .replaceAll("%time%", getFormattedTime()).replaceAll("%balance%", String.valueOf(getBalance()))
            );
        }
    }

    public void hide() {
        if (this.armorStand != null) {
            this.armorStand.remove();
            this.armorStand = null;
        }
    }

    public Boolean isVisible() {
        return this.armorStand != null;
    }

    public int getBalance() {
        return this.robotMoney.getMoney();
    }

    public void restartBalance() {
        this.robotMoney.resetMoney();
    }

    public int getRemaining() {
        return this.robotMoney.getRemaining();
    }

    public int getId() {
        return id;
    }

    public String getColoredName() {
        return ChatColor.translateAlternateColorCodes('&', colored_name);
    }

    public void makeWork() {
        this.robotMoney.start();
    }

    public void stopWork() {
        this.robotMoney.stop();
    }

    public int getEntityId() {
        if (this.armorStand == null) {
            return -1;
        }
        return this.armorStand.getEntityId();
    }

    public String getType() {
        return type;
    }

    public void claim(Player p) {
        claim(p, true);
    }

    public void claim(Player p, Boolean message) {
        if (getBalance() == 0) {
            if (message) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', DistinctRobots.getInstance().getConfigFile().getMessage("no-balance")));
            }
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replace(this.robotReward, p.getName(), getBalance()));
        restartBalance();
    }

    private String replace(String command, String playerName, int balance) {
        return command.replaceAll("%player%", playerName).replaceAll("%balance%", String.valueOf(balance));
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }
}

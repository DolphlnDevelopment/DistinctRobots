package com.dolphln.distinctrobots.listeners;

import com.bgsoftware.superiorskyblock.api.events.IslandDisbandEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandKickEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandLeaveEvent;
import com.bgsoftware.superiorskyblock.api.events.IslandQuitEvent;
import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class IslandListener implements Listener {

    private final DistinctRobots plugin;

    public IslandListener(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIslandDisband(IslandDisbandEvent e) {
        for (Robot robot : plugin.getRobotManager().getRobots()) {
            if (e.getIsland().isInside(robot.getLocation())) {
                Player p = Bukkit.getPlayer(robot.getOwnerUUID());
                if (p != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getRobotManager().removeRobot(p, robot);
                            plugin.getRobotManager().giveRobot(p, robot.getType(), 1);
                        }
                    }.runTaskLater(plugin, 10L);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(IslandQuitEvent e) {
        Player p = e.getPlayer().asPlayer();
        for (Robot robot: plugin.getRobotManager().playerRobots(p)) {
            if (e.getIsland().isInside(robot.getLocation())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getRobotManager().removeRobot(p, robot);
                        plugin.getRobotManager().giveRobot(p, robot.getType(), 1);
                    }
                }.runTaskLater(plugin, 10L);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(IslandKickEvent e) {
        Player p = e.getTarget().asPlayer();
        if (p == null) {
            YamlConfiguration pdata = plugin.getStorageManager().getRawData(e.getTarget().getUniqueId());

            for (String robotData : pdata.getConfigurationSection("").getKeys(false)) {
                int id = Integer.parseInt(robotData);
                Robot robot = new Robot(id, e.getTarget().getUniqueId(), plugin.getConfigFile().getConfig(), pdata);
                if (e.getIsland().isInside(robot.getPlacedBlockLocation())) {
                    robot.getPlacedBlock().setType(Material.AIR);
                }
            }
            return;
        }

        for (Robot robot: plugin.getRobotManager().playerRobots(p)) {
            if (e.getIsland().isInside(robot.getLocation())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getRobotManager().removeRobot(p, robot);
                        plugin.getRobotManager().giveRobot(p, robot.getType(), 1);
                    }
                }.runTaskLater(plugin, 10L);
            }
        }
    }
}

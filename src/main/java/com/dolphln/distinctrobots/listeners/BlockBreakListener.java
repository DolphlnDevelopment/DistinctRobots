package com.dolphln.distinctrobots.listeners;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final DistinctRobots plugin;

    public BlockBreakListener(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        for (Robot robot : plugin.getRobotManager().getRobots()) {
            Location loc1 = robot.getPlacedBlockLocation();
            Location loc2 = e.getBlock().getLocation();

            if (loc1.getBlockX() == loc2.getBlockX() &&
                    loc1.getBlockY() == loc2.getBlockY() &&
                    loc1.getBlockZ() == loc2.getBlockZ() &&
                    loc1.getWorld().getName().equals(loc2.getWorld().getName())) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("robot-block")));
            }
        }
    }
}

package com.dolphln.distinctrobots.listeners;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.BasicLocation;
import com.dolphln.distinctrobots.core.Robot;
import com.dolphln.distinctrobots.core.robot.Orientation;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

public class RobotPlaceListener implements Listener {

    private DistinctRobots plugin;

    public RobotPlaceListener(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY || e.useItemInHand() == Event.Result.DENY || e.isCancelled()) {
            return;
        }

        ItemStack item = e.getItem();
        Player p = e.getPlayer();

        if (item == null || e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.hasKey("robot")) return;

        e.setCancelled(true);

        if (!plugin.getConfigFile().getConfig().getStringList("whitelisted-world").contains(p.getLocation().getWorld().getName())) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("world"))
            );
            return;
        }

        if (!p.hasPermission("robots.unlimited") && !p.isOp() && plugin.getRobotManager().playerRobots(p).length >= getAmount(p)) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("limit"))
            );
            return;
        }
        if (e.getBlockFace() != BlockFace.UP) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("place"))
            );
            return;
        }

        Orientation orientation = Orientation.getOrientation(p.getLocation().getYaw());
        Location loc = e.getClickedBlock().getLocation().add(0, 1, 0);
        boolean allowed = plugin.getSuperiorSkyblockHook().locationValid(p, new BasicLocation(loc));
        //System.out.println(allowed);
        if (!allowed) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("invalid-location"))
            );
            return;
        }

        if (loc.add(orientation.getBlockLocationAdd()).getBlock().getType() != Material.AIR) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("no-space"))
            );
            return;
        }

        if (plugin.getRobotManager().getRobot(new BasicLocation(loc)) != null) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("no-space"))
            );
            return;
        }

        // Get type from item
        String type = nbtItem.getString("robot");

        int id = plugin.getStorageManager().createRobot(p, type, orientation, e.getClickedBlock().getLocation().add(0, 1, 0));

        p.getInventory().removeItem(e.getItem());

        Robot robot = plugin.getRobotManager().createRobot(p, id);
        robot.show();
        robot.placeBlock();
        robot.makeWork();
    }

    public int getAmount(Player player) {
        String permissionPrefix = "robots.limit.";
        int limit = 100;

        for (int i = limit; i >= 0; i--) {
            if (player.hasPermission(permissionPrefix + i)) {
                return i;
            }
        }
        return 0;
    }
}

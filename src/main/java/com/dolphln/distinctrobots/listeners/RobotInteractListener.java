package com.dolphln.distinctrobots.listeners;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.RobotManager;
import com.dolphln.distinctrobots.core.Robot;
import com.dolphln.distinctrobots.guis.RobotMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RobotInteractListener implements Listener {

    private final DistinctRobots plugin;

    public RobotInteractListener(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        Entity ent = e.getRightClicked();

        if (ent.getType() != EntityType.ARMOR_STAND) {
            return;
        }

        Player p = e.getPlayer();

        RobotManager.Identifier identifier = plugin.getRobotManager().getIdentifier(ent.getEntityId());
        if (identifier == null) {
            return;
        }
        e.setCancelled(true);

        if (identifier.getUUID() != p.getUniqueId()) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("wrong-owner"))
            );
            return;
        }

        Robot robot = plugin.getRobotManager().getRobot(identifier);
        openGui(p, robot);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity ent = e.getEntity();

        if (ent.getType() != EntityType.ARMOR_STAND) {
            return;
        }

        //Player p = (Player) e.getDamager();

        RobotManager.Identifier identifier = plugin.getRobotManager().getIdentifier(ent.getEntityId());
        if (identifier == null) {
            return;
        }
        e.setCancelled(true);

        /*if (identifier.getUUID() != p.getUniqueId()) {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfigFile().getMessage("wrong-owner"))
            );
            return;
        }

        Robot robot = plugin.getRobotManager().getRobot(identifier);
        openGui(p, robot);*/
    }

    private void openGui(Player p, Robot robot) {
        RobotMenu menu = new RobotMenu(p, robot);
        String title = menu.getTitle();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.getOpenInventory().getTitle().equals(title)) {
                    cancel();
                }
                menu.updateClaim();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}

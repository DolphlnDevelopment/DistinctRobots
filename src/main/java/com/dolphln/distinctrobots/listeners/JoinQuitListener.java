package com.dolphln.distinctrobots.listeners;

import com.dolphln.distinctrobots.DistinctRobots;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final DistinctRobots plugin;

    public JoinQuitListener(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        plugin.getStorageManager().addCacheConfig(p.getUniqueId());
        plugin.getRobotManager().joinPlayer(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        plugin.getRobotManager().quitPlayer(p);
        plugin.getStorageManager().removePlayer(p.getUniqueId());
    }
}

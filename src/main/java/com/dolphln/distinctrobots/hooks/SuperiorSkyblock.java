package com.dolphln.distinctrobots.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.BasicLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SuperiorSkyblock {

    private final DistinctRobots plugin;
    private final boolean hooked;

    public SuperiorSkyblock(DistinctRobots plugin) {
        this.plugin = plugin;
        this.hooked = Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2");
    }

    public boolean locationValid(Player p, BasicLocation loc) {
        if (isHooked()) {
            Island island = SuperiorSkyblockAPI.getPlayer(p).getIsland();
            System.out.println(SuperiorSkyblockAPI.getPlayer(p).getName());

            if (island == null) {
                return false;
            }

            System.out.println(loc.getLocation().toString());
            return island.isInside(loc.getLocation());
        } else {
            return true;
        }
    }

    public boolean isHooked() {
        return hooked;
    }
}

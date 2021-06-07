package com.dolphln.distinctrobots.config;

import com.dolphln.distinctrobots.DistinctRobots;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ConfigFile {

    private final DistinctRobots plugin;

    private YamlConfiguration config;
    private File configFile;

    public ConfigFile(DistinctRobots plugin) {
        this.plugin = plugin;
        setup();
    }

    public void setup() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", true);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create config.yml file.");
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        plugin.getLogger().log(Level.FINE, "File config.yml loaded correctly.");

    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return configFile;
    }

    public String getMessage(String messageName) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages." + messageName));
    }

}

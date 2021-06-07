package com.dolphln.distinctrobots.config;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import com.dolphln.distinctrobots.core.robot.Orientation;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class StorageManager {

    private DistinctRobots plugin;

    private File dataFolder;

    private HashMap<UUID, YamlConfiguration> dataCache = new HashMap<>();

    public StorageManager(DistinctRobots plugin) {
        this.plugin = plugin;
        setup();

        new BukkitRunnable() {
            @Override
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(plugin, 30*20L, 60*20L);
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        this.dataFolder = new File(plugin.getDataFolder().getPath() + "/players");

        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdir();
        }
    }

    public void saveData() {
        for (UUID uuid : dataCache.keySet()) {
            saveData(uuid, dataCache.get(uuid));
            /*if (configToDelete.contains(uuid)) {
                dataCache.remove(uuid);
                configToDelete.remove(uuid);
            }*/
        }
    }

    public void createPlayerFile(File dataFile) {
        try {
            dataFile.createNewFile();

            YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

            data.save(dataFile);
        } catch (Exception e) {e.printStackTrace();}
    }

    public File getFile(UUID uuid) {
        File dataFile = new File(this.dataFolder, uuid.toString() + ".yml");

        if (!dataFile.exists()) {
            createPlayerFile(dataFile);
        }

        return dataFile;
    }

    public void saveData(UUID uuid, YamlConfiguration config) {
        File dataFile = getFile(uuid);
        try {
            config.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateCacheConfig(uuid, config);
    }

    public void addCacheConfig(UUID uuid) {
        File dataFile = getFile(uuid);
        this.dataCache.put(uuid, YamlConfiguration.loadConfiguration(dataFile));
    }

    public YamlConfiguration getRawData(UUID uuid) {
        return YamlConfiguration.loadConfiguration(getFile(uuid));
    }

    public void deleteCacheConfig(UUID uuid, Boolean save) {
        if (save) {
            saveData(uuid, this.dataCache.get(uuid));
        }
        this.dataCache.remove(uuid);
    }

    public void updateCacheConfig(UUID uuid, YamlConfiguration config) {
        this.dataCache.put(uuid, config);
    }

    public YamlConfiguration getCacheConfig(UUID uuid) {
        return this.dataCache.get(uuid);
    }

    /*public void addRemoveQueue(UUID uuid) {
        configToDelete.add(uuid);
    }*/

    public void removePlayer(UUID uuid) {
        saveData(uuid, getCacheConfig(uuid));
        dataCache.remove(uuid);
    }

    public int createRobot(Player p, String type, Orientation orientation, Location loc) {
        /*
        535:
          type: "test"
          orientation: "NORTH"
          remaining: 5
          interval: 5
          money: 5
          location:
            x: 0
            y: 0
            z: 0
            world: world
         */
        YamlConfiguration data = getCacheConfig(p.getUniqueId());
        String configNode = "";
        for (int i = 0; i < 10000; i++) {
            if (plugin.getStorageManager().getCacheConfig(p.getUniqueId()).getConfigurationSection(String.valueOf(i)) == null) {
                configNode = String.valueOf(i);
                break;
            }
        }
        int id = Integer.parseInt(configNode);
        String path = String.valueOf(id);
        data.set(path + ".type", type);
        data.set(path + ".orientation", orientation.getType().toUpperCase());
        data.set(path + ".remaining", 0);
        data.set(path + ".balance", 0);
        data.set(path + ".location.x", loc.getBlockX());
        data.set(path + ".location.y", loc.getBlockY());
        data.set(path + ".location.z", loc.getBlockZ());
        data.set(path + ".location.world", loc.getWorld().getName());
        return id;
    }

    public void saveRobot(UUID uuid, Robot robot) {
        YamlConfiguration data = getCacheConfig(uuid);
        String path = String.valueOf(robot.getId());
        data.set(path + ".remaining", robot.getRemaining());
        data.set(path + ".balance", robot.getBalance());
    }

    public void removeRobot(UUID uuid, Robot robot) {
        YamlConfiguration data = getCacheConfig(uuid);
        String path = String.valueOf(robot.getId());
        data.set(path, null);
    }

}

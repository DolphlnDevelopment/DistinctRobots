package com.dolphln.distinctrobots;

import com.dolphln.distinctrobots.commands.RobotCommand;
import com.dolphln.distinctrobots.config.ConfigFile;
import com.dolphln.distinctrobots.config.StorageManager;
import com.dolphln.distinctrobots.core.menus.MenuHandler;
import com.dolphln.distinctrobots.hooks.SuperiorSkyblock;
import com.dolphln.distinctrobots.listeners.*;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class DistinctRobots extends JavaPlugin {

    private static DistinctRobots instance;
    private ConsoleCommandSender console;

    private ConfigFile configFile;
    private StorageManager storageManager;
    private MenuHandler menuHandler;
    private SuperiorSkyblock superiorSkyblockHook;

    private RobotManager robotManager;

    @Override
    public void onEnable() {
        instance = this;
        console = Bukkit.getConsoleSender();

        console.sendMessage(ChatColor.AQUA + "====================================");
        console.sendMessage("");
        console.sendMessage(ChatColor.AQUA + "Enabling DistinctRobots...");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Enabling NBTAPI... (ignore message below)");
        NBTItem dummyItem = new NBTItem(new ItemStack(Material.STONE));
        dummyItem.setString("testing", "testing");
        console.sendMessage(ChatColor.GREEN + "NBTAPI enabled");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Loading Config and Data...");
        this.configFile = new ConfigFile(this);
        this.storageManager = new StorageManager(this);
        this.menuHandler = new MenuHandler();
        this.superiorSkyblockHook = new SuperiorSkyblock(this);
        console.sendMessage(ChatColor.GREEN + "Config and Data loaded successfully!");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Loading Robots...");
        this.robotManager = new RobotManager(this);
        console.sendMessage(ChatColor.GREEN + "Robots loaded successfully!");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Loading Players...");
        for (Player p : Bukkit.getOnlinePlayers()) {
            getStorageManager().addCacheConfig(p.getUniqueId());
            getRobotManager().joinPlayer(p);
        }
        console.sendMessage(ChatColor.GREEN + "Players loaded successfully!");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Registering listeners and commands...");
        registerListeners();
        registerCommands();
        console.sendMessage(ChatColor.GREEN + "DistinctRobots enabled successfully!");
        console.sendMessage("");
        console.sendMessage(ChatColor.AQUA + "====================================");
    }

    @Override
    public void onDisable() {
        console.sendMessage(ChatColor.AQUA + "====================================");
        console.sendMessage("");
        console.sendMessage(ChatColor.AQUA + "Disabling DistinctRobots...");

        console.sendMessage("");

        console.sendMessage(ChatColor.AQUA + "Saving Robots...");
        menuHandler.closeAll();
        for (Player p : Bukkit.getOnlinePlayers()) {
            getRobotManager().quitPlayer(p);
        }
        console.sendMessage(ChatColor.RED + "Robots unloaded successfully!");

        console.sendMessage(ChatColor.AQUA + "Saving Players Data...");
        for (Player p : Bukkit.getOnlinePlayers()) {
            getStorageManager().removePlayer(p.getUniqueId());
        }
        console.sendMessage(ChatColor.RED + "Players unloaded successfully!");

        console.sendMessage("");

        console.sendMessage(ChatColor.RED + "DistinctRobots successfully disabled!");
        console.sendMessage("");
        console.sendMessage(ChatColor.AQUA + "====================================");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this.menuHandler.getListeners(), instance);
        Bukkit.getPluginManager().registerEvents(new RobotPlaceListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(new RobotInteractListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(new IslandListener(instance), instance);
    }

    private void registerCommands() {
        getCommand("robot").setExecutor(new RobotCommand(instance));
    }

    public static DistinctRobots getInstance() {
        return instance;
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public SuperiorSkyblock getSuperiorSkyblockHook() {
        return superiorSkyblockHook;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

    public RobotManager getRobotManager() {
        return robotManager;
    }
}

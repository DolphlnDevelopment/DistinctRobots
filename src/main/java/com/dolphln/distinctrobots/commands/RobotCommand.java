package com.dolphln.distinctrobots.commands;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.RobotManager;
import com.dolphln.distinctrobots.config.ConfigFile;
import com.dolphln.distinctrobots.config.StorageManager;
import com.dolphln.distinctrobots.core.menus.MenuHandler;
import com.dolphln.distinctrobots.hooks.SuperiorSkyblock;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RobotCommand implements CommandExecutor {

    private final DistinctRobots plugin;
    private String invalidArgs = ChatColor.RED + "Invalid arguments. Command is /robot give <player> <robot> <amount> or /robot reload";

    public RobotCommand(DistinctRobots plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !sender.hasPermission("robots.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions to run this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(invalidArgs);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
            case "rl":
                sender.sendMessage(ChatColor.AQUA + "Reloading DistinctRobots...");
                try {
                    ConsoleCommandSender console = Bukkit.getConsoleSender();

                    console.sendMessage(ChatColor.AQUA + "====================================");
                    console.sendMessage("");
                    console.sendMessage(ChatColor.GOLD + "Reloading DistinctRobots...");
                    console.sendMessage("");
                    console.sendMessage(ChatColor.AQUA + "Saving Robots...");
                    plugin.getMenuHandler().closeAll();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.getRobotManager().quitPlayer(p);
                    }
                    console.sendMessage(ChatColor.RED + "Robots unloaded successfully!");

                    console.sendMessage(ChatColor.AQUA + "Saving Players Data...");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.getStorageManager().removePlayer(p.getUniqueId());
                    }
                    console.sendMessage(ChatColor.RED + "Players unloaded successfully!");

                    // Loading
                    console.sendMessage(ChatColor.AQUA + "Loading Config and Data...");
                    plugin.getConfigFile().setup();
                    plugin.getStorageManager().setup();
                    console.sendMessage(ChatColor.GREEN + "Config and Data loaded successfully!");

                    console.sendMessage("");

                    console.sendMessage(ChatColor.AQUA + "Loading Robots...");
                    plugin.getRobotManager().setup();
                    console.sendMessage(ChatColor.GREEN + "Robots loaded successfully!");

                    console.sendMessage("");

                    console.sendMessage(ChatColor.AQUA + "Loading Players...");
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        plugin.getStorageManager().addCacheConfig(p.getUniqueId());
                        plugin.getRobotManager().joinPlayer(p);
                    }
                    console.sendMessage(ChatColor.GREEN + "Players loaded successfully!");

                    console.sendMessage("");

                    console.sendMessage(ChatColor.GOLD + "DistinctRobots reloaded successfully!");
                    console.sendMessage("");
                    console.sendMessage(ChatColor.AQUA + "====================================");
                } catch (Exception e) {
                    ConsoleCommandSender console = Bukkit.getConsoleSender();

                    console.sendMessage(ChatColor.RED + "Error:");
                    console.sendMessage("");
                    e.printStackTrace();

                    console.sendMessage("");

                    console.sendMessage(ChatColor.RED + "DistinctRobots reload failed!");
                    console.sendMessage("");
                    console.sendMessage(ChatColor.AQUA + "====================================");
                }
                break;
            case "give":
                if (args.length != 4) {
                    sender.sendMessage(invalidArgs);
                    return true;
                }

                Player user = Bukkit.getPlayer(args[1]);

                if (user == null) {
                    sender.sendMessage(ChatColor.RED + "User " + args[1] + " isn't online or doesn't exist.");
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (Exception e) {
                    sender.sendMessage(invalidArgs);
                    return true;
                }

                if (amount > 64 || amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "Amount should be greater than 0 and lower than 64.");
                    return true;
                }

                if (plugin.getRobotManager().giveRobot(user, args[2], amount)) {
                    sender.sendMessage(ChatColor.GREEN + "Robot has been given to " + user.getName() + " successfully!");
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid robot!");
                }
                break;
            default:
                sender.sendMessage(invalidArgs);
        }

        return true;
    }

}

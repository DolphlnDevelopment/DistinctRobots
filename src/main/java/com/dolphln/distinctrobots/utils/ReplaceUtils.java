package com.dolphln.distinctrobots.utils;

import com.dolphln.distinctrobots.core.Robot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ReplaceUtils {

    public static String replacePlaceholders(String string, Robot robot) {
        return ChatColor.translateAlternateColorCodes('&', string)
                .replaceAll("%balance%", String.valueOf(robot.getBalance())).replaceAll("%time%", robot.getFormattedTime())
                .replaceAll("%robot%", robot.getColoredName());
    }

    public static String replacePlaceholders(String string, Player p) {
        return ChatColor.translateAlternateColorCodes('&', string)
                .replaceAll("%player%", p.getName());
    }

    public static List<String> replaceLore(List<String> old_role, Robot robot) {
        List<String> new_role = new ArrayList<>();

        if (old_role == null || old_role.size() == 0) {
            new_role.add(ChatColor.WHITE + " ");
            return new_role;
        }

        for (String line : old_role) {
            new_role.add(replacePlaceholders(line, robot));
        }

        return new_role;
    }

    public static ItemStack replaceItemStack(ItemStack itemStack, Robot robot) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(replacePlaceholders(meta.getDisplayName(), robot));
        meta.setLore(replaceLore(meta.getLore(), robot));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}

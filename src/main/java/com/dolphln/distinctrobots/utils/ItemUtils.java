package com.dolphln.distinctrobots.utils;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import com.mojang.authlib.GameProfile;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    public static ItemStack headStack(String texture) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

        if (!(head.getItemMeta() instanceof SkullMeta)) {
            return null;
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        // random uuid based on the b64 string
        UUID id = new UUID(
                texture.substring(texture.length() - 20).hashCode(),
                texture.substring(texture.length() - 10).hashCode()
        );
        GameProfile profile = new GameProfile(id, "aaaaa");
        profile.getProperties().put("textures", new Property("textures", texture));

        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        head.setItemMeta(meta);
        return head;
    }

    public static List<String> getLore(List<String> old_lore) {
        List<String> new_lore = new ArrayList<>();

        if (old_lore == null || old_lore.size() == 0) {
            new_lore.add(ChatColor.translateAlternateColorCodes('&', "&f"));
            return new_lore;
        }

        for (String lore : old_lore) {
            new_lore.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        return new_lore;
    }

    public static ItemStack generateItem(String path) {
        YamlConfiguration config = DistinctRobots.getInstance().getConfigFile().getConfig();
        ItemStack item;
        if (config.getBoolean(path + ".head")) {
            item = ItemUtils.headStack(config.getString(path + ".material"));
        } else {
            item = new ItemStack(Material.getMaterial(config.getString(path + ".material").toUpperCase()), 1, (short) config.getInt(path + ".data"));
        }

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".display-name")));
        meta.setLore(ItemUtils.getLore(config.getStringList(path + ".lore")));
        item.setItemMeta(meta);

        return item;
    }

}

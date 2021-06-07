package com.dolphln.distinctrobots.core.robot;

import com.dolphln.distinctrobots.utils.ColorUtils;
import com.dolphln.distinctrobots.utils.ItemUtils;
import com.dolphln.distinctrobots.utils.ReplaceUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RobotArmor {

    private YamlConfiguration config;
    private String path;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack hand;

    public RobotArmor(YamlConfiguration config, String path) {
        this.config = config;
        this.path = path;

        setup();
    }

    private void setup() {
        this.helmet = generateHelmet(this.path + ".armor-stand.head");
        this.chestplate = generateArmor(this.path + ".armor-stand.chestplate");
        this.leggings = generateArmor(this.path + ".armor-stand.leggings");
        this.boots = generateArmor(this.path + ".armor-stand.boots");
        this.hand = generateItem(this.path + ".armor-stand.hand");
    }

    private ItemStack generateHelmet(String path) {
        ItemStack item;
        if (config.getBoolean(path + ".head")) {
            item = ItemUtils.headStack(config.getString(path + ".material"));
        } else {
            item = new ItemStack(Material.getMaterial(config.getString(path + ".material").toUpperCase()), 1, (short) config.getInt(path + ".data"));
        }

        if (item == null) return null;

        if (item.getItemMeta() instanceof LeatherArmorMeta && config.getBoolean(path + ".leather_tools.enabled")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(ColorUtils.hexToColor(config.getString(path + ".leather_tools.color")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack generateArmor(String path) {
        ItemStack item = new ItemStack(Material.getMaterial(config.getString(path + ".material").toUpperCase()), 1, (short) config.getInt(path + ".data"));

        if (item == null) return null;

        if (item.getItemMeta() instanceof LeatherArmorMeta && config.getBoolean(path + ".leather_tools.enabled")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(ColorUtils.hexToColor(config.getString(path + ".leather_tools.color")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack generateItem(String path) {
        return new ItemStack(Material.getMaterial(config.getString(path + ".material").toUpperCase()), 1, (short) config.getInt(path + ".data"));
    }

    public void reload(YamlConfiguration config) {
        this.config = config;
        setup();
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public ItemStack getHand() {
        return hand;
    }
}

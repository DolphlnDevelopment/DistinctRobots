package com.dolphln.distinctrobots.core.menus;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class Button extends ItemStack {
    public Button(Material material) {
        super(material);
    }

    public Button(ItemStack item) {
        super(item);
    }

    public Button(Material material,String name,String... lore) {
        this(material);
        ItemMeta meta = getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        for (int i = 0; i < lore.length;i++) lore[i] = ChatColor.translateAlternateColorCodes('&', lore[i]);

        meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }

    public abstract void onClick(Menu menu, InventoryClickEvent event);

}

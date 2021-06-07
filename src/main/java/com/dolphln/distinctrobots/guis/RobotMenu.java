package com.dolphln.distinctrobots.guis;

import com.dolphln.distinctrobots.DistinctRobots;
import com.dolphln.distinctrobots.core.Robot;
import com.dolphln.distinctrobots.core.menus.Button;
import com.dolphln.distinctrobots.core.menus.Menu;
import com.dolphln.distinctrobots.utils.ItemUtils;
import com.dolphln.distinctrobots.utils.ReplaceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class RobotMenu {

    private Player p;
    private Robot robot;
    private YamlConfiguration config;
    private Menu menu;

    private final String title;

    public RobotMenu(Player p, Robot robot) {
        this.p = p;
        this.robot = robot;
        this.config = DistinctRobots.getInstance().getConfigFile().getConfig();
        this.title = ReplaceUtils.replacePlaceholders(
                ReplaceUtils.replacePlaceholders(
                        DistinctRobots.getInstance().getConfigFile().getConfig().getString("menu.title")
                        , p), robot);

        this.menu = buildGui(this.title);
        updateClaim();
        DistinctRobots.getInstance().getMenuHandler().openMenu(p, menu);
    }

    private Menu buildGui(String title) {
        Menu menu = new Menu(Bukkit.createInventory(null, 27, title));

        ItemStack placeholder = getPlaceholder();

        for (int i = 0; i < 27; i++) {
            if (i != 12 && i != 14) {
                menu.setButton(i, new Button(placeholder) {
                    @Override
                    public void onClick(Menu menu, InventoryClickEvent event) {
                        event.setCancelled(true);
                    }
                });
            }
        }

        menu.setButton(14, new Button(getItem("menu.pickup")) {
            @Override
            public void onClick(Menu menu, InventoryClickEvent event) {
                event.setCancelled(true);
                DistinctRobots.getInstance().getMenuHandler().closeMenu(p);
                DistinctRobots.getInstance().getRobotManager().removeRobot(p, robot);
                DistinctRobots.getInstance().getRobotManager().giveRobot(p, robot.getType(), 1);
            }
        });

        menu.setButton(12, new Button(getItem("menu.claim")) {
            @Override
            public void onClick(Menu menu, InventoryClickEvent event) {
                event.setCancelled(true);
                DistinctRobots.getInstance().getMenuHandler().closeMenu(p);
                robot.claim(p);
                robot.updateHologram();
            }
        });

        return menu;
    }

    public void updateClaim() {
        this.menu.updateItem(12, getItem("menu.claim"));
    }

    private ItemStack getPlaceholder() {
        ItemStack item = new ItemStack(Material.getMaterial(config.getString("menu.placeholder.material")), 1, (short) config.getInt("menu.placeholder.data"));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(config.getString("menu.placeholder.name"));
        meta.setLore(config.getStringList("menu.placeholder.lore"));

        item.setItemMeta(meta);
        return ReplaceUtils.replaceItemStack(item, robot);
    }

    private ItemStack getItem(String path) {
        ItemStack item;
        if (config.getBoolean(path + ".head")) {
            item = ItemUtils.headStack(config.getString(path + ".material"));
        } else {
            item = new ItemStack(Material.getMaterial(config.getString(path + ".material")), 1, (short) config.getInt(path + ".data"));
        }
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(config.getString(path + ".name"));
        meta.setLore(config.getStringList(path + ".lore"));

        item.setItemMeta(meta);
        return ReplaceUtils.replaceItemStack(item, robot);
    }

    public String getTitle() {
        return this.title;
    }

}

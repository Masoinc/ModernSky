package me.masonic.mc.Function;

import me.masonic.mc.Core;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

/**
 * Mason Project
 * 2017-7-28-0028
 */
public class Ban implements Listener {
    @EventHandler
    private void onCraft(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() == Material.STORAGE_MINECART) {
            e.getWhoClicked().sendMessage(Core.getPrefix() + "此物品已被禁用");
            e.setCancelled(true);
        }
    }
}

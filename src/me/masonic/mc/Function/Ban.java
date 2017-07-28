package me.masonic.mc.Function;

import me.masonic.mc.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

/**
 * Mason Project
 * 2017-7-28-0028
 */
public class Ban implements Listener {
    private static HashMap<Player, Long> SPAM_MAP = new HashMap<>();
    //点击栅栏的间隔
    private static final int CLICK_SPAM_DELAY = 500;
    private static final int CLICK_SPAM_DELAY_KICK = 250;

    @EventHandler
    private void onCraft(CraftItemEvent e) {
        if (e.getRecipe().getResult().getType() == Material.STORAGE_MINECART ||
                e.getRecipe().getResult().getType() == Material.HOPPER_MINECART) {
            e.getWhoClicked().sendMessage(Core.getPrefix() + "此物品已被禁用");
            e.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    private void onClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null &&
                e.getClickedBlock().getType() == Material.FENCE ||
                e.getClickedBlock().getType() == Material.NETHER_FENCE) {
            if (System.currentTimeMillis() - SPAM_MAP.getOrDefault(e.getPlayer(), System.currentTimeMillis() - 2000) < CLICK_SPAM_DELAY) {
                e.getPlayer().sendMessage(Core.getPrefix() + "你点击的太快了");
                e.setCancelled(true);
            } else if (System.currentTimeMillis() - SPAM_MAP.getOrDefault(e.getPlayer(), System.currentTimeMillis() - 2000) < CLICK_SPAM_DELAY_KICK) {
                e.getPlayer().kickPlayer(Core.getPrefix() + "操作过于频繁");
            } else {
                SPAM_MAP.put(e.getPlayer(), System.currentTimeMillis());
            }
        }

    }
}

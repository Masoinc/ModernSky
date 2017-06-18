package me.masonic.mc.Function;

import me.masonic.mc.Utility.Utility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class InvIcon implements Listener {

    private static ItemStack backpack = new ItemStack(Material.CHEST);
    private static ItemStack phone = new ItemStack(Material.WATCH);

    static {
        ItemMeta bmeta = backpack.getItemMeta();
        bmeta.setDisplayName("§2§l随身背包");
        backpack.setItemMeta(bmeta);

        ItemMeta pmeta = phone.getItemMeta();
        pmeta.setDisplayName("§7§l菜单");
        phone.setItemMeta(pmeta);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        giveInvIcon(event.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!(e.getCurrentItem() == null)
                && e.getCurrentItem().hasItemMeta()
                && e.getCurrentItem().getItemMeta().hasDisplayName()) {
            switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                case "§2§l随身背包":
                    e.setCancelled(true);
                    ((Player) e.getWhoClicked()).performCommand("backpack");
                    break;
                case "§7§l手机":
                    e.setCancelled(true);
                    ((Player) e.getWhoClicked()).performCommand("bs mskycore");
                    break;
                default:
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().hasItemMeta()
                && e.getItemDrop().getItemStack().getItemMeta().hasDisplayName()) {
            switch (e.getItemDrop().getItemStack().getItemMeta().getDisplayName()) {
                case "§7§l手机":
                    e.setCancelled(true);
                default:
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent e) {
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && e.hasItem()
                && e.getItem().hasItemMeta()
                && e.getItem().getItemMeta().hasDisplayName()
                && e.getItem().getItemMeta().getDisplayName().equals(phone.getItemMeta().getDisplayName())
                ) {
            Utility.runOp(e.getPlayer(), "bs mskycore");
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        giveInvIcon(event.getPlayer());
    }


    private void giveInvIcon(Player p) {
        p.getInventory().setItem(8, phone);
        p.getInventory().setItem(17, backpack);
    }
}





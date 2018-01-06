package me.masonic.mc.Function;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.PermissionUtil;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class InvIcon implements Listener {

    private static ItemStack phone = new ItemStack(Material.WATCH);

    private static ItemStack getBackpack(UUID p) {
        ItemStack backpack = new ItemStack(Material.CHEST);
        ItemMeta bmeta = backpack.getItemMeta();
        bmeta.setDisplayName("§8[ §2随身背包 §8]");
        List<String> lores = Arrays.asList(
                "",
                "§7◇ 你的背包目前有: §3" + PermissionUtil.getBackPackPage(Bukkit.getPlayer(p)) + " §7页",
                "",
                "§7◇ 个人专属的随身背包",
                "§7◇ 空岛等级达到 §350 §7级时开启",
                "",
                "§7○ 目前空岛等级: §6Lv." + ASkyBlockAPI.getInstance().getIslandLevel(p),
                "",
                "§8[ModernSky] reward");
        bmeta.setLore(lores);
        backpack.setItemMeta(bmeta);
        return backpack;

    }

    static {
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
                case "§8[ §2随身背包 §8]":
                    e.setCancelled(true);
                    if (ASkyBlockAPI.getInstance().getIslandLevel(e.getWhoClicked().getUniqueId()) >= 50) {
                        ((Player) e.getWhoClicked()).performCommand("backpack");
                    } else {
                        e.getWhoClicked().sendMessage(Core.getPrefix() + "你还没有解锁随身背包呢，加油升级吧！");
                    }

                    break;
                case "§7§l菜单":
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
                case "§7§l菜单":
                    e.setCancelled(true);
                    break;
                case "§8[ §2随身背包 §8]":
                    e.setCancelled(true);
                    break;
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
            PermissionUtil.runOp(e.getPlayer(), "bs mskycore");
        }
    }

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        giveInvIcon(event.getPlayer());
    }


    private void giveInvIcon(Player p) {
        p.getInventory().setItem(8, phone);
        p.getInventory().setItem(17, getBackpack(p.getUniqueId()));
    }
}





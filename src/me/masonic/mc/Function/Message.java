package me.masonic.mc.Function;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Mason Project
 * 2017-6-16-0016
 */
public class Message implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage("§8[§6+§8] §7" + e.getPlayer().getName());
    }
//    @EventHandler
//    private void onquit(PlayerQuitEvent e) {
//        e.setQuitMessage("§8[§c-§8] §7"+e.getPlayer().getName());
//    }

}

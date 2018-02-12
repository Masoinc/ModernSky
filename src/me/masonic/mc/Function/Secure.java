package me.masonic.mc.Function;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Secure implements Listener {
    private static List<String> BANNEDCMD = Arrays.asList("/pl", "plugins", "/?", "/bukkit:?", "/bukkit:help", "/bukkit:pl", "/bukkit:plugins");

    @EventHandler
    void onCommand(PlayerCommandPreprocessEvent e) {
        if (BANNEDCMD.contains(e.getMessage())) {
            e.setCancelled(true);
        }
    }

}

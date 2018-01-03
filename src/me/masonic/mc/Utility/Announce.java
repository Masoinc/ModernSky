package me.masonic.mc.Utility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;

/**
 * Masonic Project
 * 2017/5/30
 */
public abstract class Announce {
    private static Iterator i$;
    private static Player p;
    public static void announceMsg(String msg) {
        i$ = Bukkit.getOnlinePlayers().iterator();
        while(i$.hasNext()) {
            p = (Player)i$.next();
            p.sendMessage(msg);
        }
    }
}

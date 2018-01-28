package me.masonic.mc.Function.Privilege;

import org.bukkit.entity.Player;

public interface Privilege {

    long getExpire();

    boolean isExist();
    boolean isExpired();
    String getFormattedExpire();
    static void expireHandler(Player p) {};

}

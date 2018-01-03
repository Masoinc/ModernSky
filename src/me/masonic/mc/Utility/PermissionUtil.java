package me.masonic.mc.Utility;

import org.bukkit.entity.Player;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class PermissionUtil {
    public static void runOp(Player p, String cmd) {
        if (p.isOp()) {
            p.performCommand(cmd);
            return;
        }
        p.setOp(true);
        p.performCommand(cmd);
        p.setOp(false);
    }
}

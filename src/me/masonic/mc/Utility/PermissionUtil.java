package me.masonic.mc.Utility;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.UUID;

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

    public static int getBackPackPage(Player p) {
        int result = 1;
        for (int i = 2; i <= 10; i++) {
            result = (PermissionsEx.getUser(p).has("backpack." + String.valueOf(i)) ? i : result);
        }
        return result;
    }
}

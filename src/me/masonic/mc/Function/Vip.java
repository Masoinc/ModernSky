package me.masonic.mc.Function;

import me.masonic.mc.Utility.SqlUtility;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.HashMap;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Vip {
    private static HashMap<String, Integer> VIPMAP = new HashMap<>();
    static {
        VIPMAP.put("default", 0);
        VIPMAP.put("VIP", 1);
        VIPMAP.put("VIP+", 2);
        VIPMAP.put("SVIP", 3);
    }

    public static String getVipRank(Player p) {
        String res = "default";
        for (String group : VIPMAP.keySet()) {
            if (PermissionsEx.getUser(p).inGroup(group) && VIPMAP.get(group) > VIPMAP.get(res)) {
                res = group;
            }
        }
        return res;
    }
}

package me.masonic.mc.Function;

import me.masonic.mc.Utility.SqlUtility;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Vip implements Listener {
    private static HashMap<String, Integer> VIPMAP = new HashMap<>();
    private static HashMap<String, String> VIPMAP_NAME = new HashMap<>();
    private static HashMap<String, List<String>> VIPMAP_JOINMSG = new HashMap<>();

    static {
        VIPMAP.put("default", 0);
        VIPMAP.put("VIP", 1);
        VIPMAP.put("SVIP", 2);
        VIPMAP.put("SVIP+", 3);

        VIPMAP_NAME.put("default", "§8无");
        VIPMAP_NAME.put("VIP", "§2Vip");
        VIPMAP_NAME.put("SVIP", "§6Svip");
        VIPMAP_NAME.put("SVIP+", "§cSvip+");

    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        switch (getVipRank(p)) {
            case "default":
                return;
            case "VIP":
                List<String> v1msg = Arrays.asList(
                        "§2§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一",
                        "",
                        "                    §8[ §2 Vip §8] §6" + p.getName() + "§a进入了空岛",
                        "",
                        "§2§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一");
                for (Player players :Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[])v1msg.toArray());
                }
                return;
            case "SVIP":
                List<String> v2msg = Arrays.asList(
                        "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一",
                        "",
                        "                    §8[ §6 Svip §8] §6" + p.getName() + "§a进入了空岛",
                        "",
                        "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一");
                for (Player players :Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[])v2msg.toArray());
                }
                return;
            case "SVIP+":
                List<String> v3msg = Arrays.asList(
                        "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一",
                        "",
                        "                    §8[ §c Svip+ §8] §6" + p.getName() + "§a进入了空岛",
                        "",
                        "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一");
                for (Player players :Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[])v3msg.toArray());
                }
                return;

        }
        e.setJoinMessage("§8[§6+§8] §7" + e.getPlayer().getName());
    }

    @EventHandler
    private void onquit(PlayerQuitEvent e) {
        e.setQuitMessage("§8[§c-§8] §7" + e.getPlayer().getName());
    }


    private static String getCoolDown$Formatted(long cd) {
        long day = cd / 86400;
        long hour = (cd - day * 86400) / 3600;
        return "§3" + day + " §7天§3 " + hour + " §7小时";
    }


    public static String getVip$Expiration(Player p) throws SQLException {
        if (SqlUtility.getIfExist(p, "vip")) {
            return SqlUtility.getIntValue(p, "vip", "expiration") == 0 ?
                    "§30 §7天 §30 §7小时" :
                    getCoolDown$Formatted(SqlUtility.getIntValue(p, "vip", "expiration") - System.currentTimeMillis() / 1000);

        }
        return "§30 §7天 §30 §7小时";
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

    public static String getVipRank$Formatted(Player p) {
        return VIPMAP_NAME.get(getVipRank(p));
    }

}

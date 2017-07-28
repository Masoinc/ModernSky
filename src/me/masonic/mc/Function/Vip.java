package me.masonic.mc.Function;

import me.masonic.mc.Utility.SqlUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Vip implements Listener {
    private static HashMap<String, Integer> VIPMAP = new HashMap<>();
    private static HashMap<String, String> VIPMAP_NAME = new HashMap<>();
    private static HashMap<String, Double> VIPMAP_EXP = new HashMap<>();
    private static HashMap<String, String> VIPMAP_DAILY = new HashMap<>();

    static {
        VIPMAP.put("default", 0);
        VIPMAP.put("VIP", 1);
        VIPMAP.put("SVIP", 2);
        VIPMAP.put("SVIP+", 3);

        VIPMAP_NAME.put("default", "§8无");
        VIPMAP_NAME.put("VIP", "§2Vip");
        VIPMAP_NAME.put("SVIP", "§6Svip");
        VIPMAP_NAME.put("SVIP+", "§cSvip+");

        VIPMAP_EXP.put("default", 1d);
        VIPMAP_EXP.put("VIP", 1.05d);
        VIPMAP_EXP.put("SVIP", 1.1d);
        VIPMAP_EXP.put("SVIP+", 1.2d);

        VIPMAP_DAILY.put("VIP", "kitvipv1e");
        VIPMAP_DAILY.put("SVIP", "kitvipv2e");
        VIPMAP_DAILY.put("SVIP+", "kitvipv3e");
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent e) {

    }

    @EventHandler
    private void onExp(PlayerExpChangeEvent e) {
        e.setAmount((int) (e.getAmount() * VIPMAP_EXP.get(getVipRank(e.getPlayer()))));
    }

    private void getVipDaily(Player p) throws SQLException {
        if (getVipRank(p).equals("default")) {
            return;
        }
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (SqlUtility.getIfExist(p, "vipdaily")) {//存在

            if (SqlUtility.getIntValue(p, "vipdaily", "cooldown") == day) {//已领过
                return;
            }
            //+每日奖励
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + p.getName() + " " + VIPMAP_DAILY.get(getVipRank(p)) + " 1");
            p.sendMessage("§8[ §6ModernSky §8] §7您的每日奖励已发放，请进入§6 补给箱 §7菜单领取");

            //设置冷却
            SqlUtility.uploadIntValue(p, "vipdaily", "cooldown", day);
            return;
        }
        //不存在
        SqlUtility.createColumn(p, "vipdaily");


        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + p.getName() + " " + VIPMAP_DAILY.get(getVipRank(p)) + " 1");
        p.sendMessage("§8[ §6ModernSky §8] §7您的每日奖励已发放，请进入§6 补给箱 §7菜单领取");

        SqlUtility.uploadIntValue(p, "vipdaily", "cooldown", day);

    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        try {
            getVipDaily(p);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        e.setJoinMessage("§8[§6+§8] §7" + e.getPlayer().getName());
        switch (getVipRank(p)) {
            case "default":
                return;
            case "VIP":
                List<String> v1msg = Arrays.asList(
                        "§2§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一",
                        "",
                        "                    §8[ §2 Vip §8] §6" + p.getName() + " §7进入了空岛",
                        "",
                        "§2§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一");
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[]) v1msg.toArray());
                }
                return;
            case "SVIP":
                List<String> v2msg = Arrays.asList(
                        "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一",
                        "",
                        "                    §8[ §6 Svip §8] §6" + p.getName() + " §7进入了空岛",
                        "",
                        "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一一一");
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[]) v2msg.toArray());
                }
                return;
            case "SVIP+":
                List<String> v3msg = Arrays.asList(
                        "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一",
                        "",
                        "                    §8[ §c Svip+ §8] §6" + p.getName() + " §7进入了空岛",
                        "",
                        "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一");
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage((String[]) v3msg.toArray());
                }
                return;

        }

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

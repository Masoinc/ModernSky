package me.masonic.mc.Function;

import com.avaje.ebean.RawSql;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.MessageUtil;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.SlimefunGuide;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Package implements Listener {
    private static Core plugin;

    public Package(Core plugin) {
        this.plugin = plugin;
    }

    private final static String COL_USER_NAME = "user_name";
    private final static String COL_RECORD = "package_record";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_EXPIRE = "expire";
    private final static String COL_TYPE = "type";
    private final static String SHEET = "package";

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColExpire() {
        return COL_EXPIRE;
    }

    public static String getColType() {
        return COL_TYPE;
    }

    public static String getSHEET() {
        return SHEET;
    }

    public static String getColRecord() {
        return COL_RECORD;
    }

    public static String getPackageState(Player p) {
        if (isExpired(p)) {
            return "§7未开通增值包";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//这个是你要转成后的时间的格式
            return "将于 §6" + sdf.format(new Date(Long.parseLong(String.valueOf(getExpire(p))) * 1000)) + " §7过期";
        }

    }


    public static Boolean isExpired(Player p) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (exist) {
                String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
                ResultSet value = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPIRE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
                return (value.getInt(1) < System.currentTimeMillis() / 1000);
            } else {
                String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`, `{4}`) VALUE(''{5}'', ''{6}'', {7}, ''{8}'')";
                sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPIRE, COL_TYPE, p.getUniqueId().toString(), p.getPlayerListName(), 0, "A");
                SqlUtil.update(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getExpire(Player p) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (exist) {
                String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
                ResultSet value = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPIRE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
                return value.getInt(1);
            } else {
                String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`, `{4}`) VALUE(''{5}'', ''{6}'', {7}, ''{8}'')";
                sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPIRE, COL_TYPE, p.getUniqueId().toString(), p.getPlayerListName(), 0, "A");
                SqlUtil.update(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String sendPackage(Player p, int time) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (exist) {
                String sql = "UPDATE {0} SET {1} = {2} WHERE {3} = ''{4}''";
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_EXPIRE, (isExpired(p) ? Long.toString(System.currentTimeMillis() / 1000 + 86400 * time) : Long.toString(getExpire(p) + 86400 * time)), COL_USER_UUID, p.getUniqueId().toString()));
                return "§7已续费增值包 §8[ §630天 §8]";
            } else {
                String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`, `{4}`) VALUE(''{5}'', ''{6}'', {7}, ''{8}'')";
                sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPIRE, COL_TYPE, p.getUniqueId().toString(), p.getPlayerListName(), Long.toString(System.currentTimeMillis() / 1000 + 86400 * time), "A");
                SqlUtil.update(sql);
                return "§7已开通增值包 §8[ §630天 §8]";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "出现异常，请联系管理员 QQ:954590000";
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        int current_date = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        Gson gson = new Gson();
        Boolean exist = null;
        ArrayList<String> result = null;
        try {
            exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);

            if (exist) {
                String sql = "SELECT {0} FROM {1} WHERE {2} = '{3}' LIMIT 1;";
                ResultSet sign = SqlUtil.getResults(MessageFormat.format(sql, COL_RECORD, SHEET, COL_USER_UUID, COL_USER_NAME));
                assert sign != null;
                result = gson.fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
                }.getType());

            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        Bukkit.getServer().getScheduler().runTaskLater(plugin,
                () -> MessageUtil.sendFullMsg(p,
                        Arrays.asList("    §7你好，亲爱的 §6" + p.getPlayerListName(),
                                "    §7今日的增值包奖励: ",
                                "    §3尘晶 §7x 180",
                                "    §6探索值 §7x 10",
                                "    §7已经发放给你啦，请注意查收",
                                "    §8     ----基地后勤处")), 20);


    }
//        Player p = e.getPlayer();
//        if (!isExpired(p)) {
//            final ChestMenu menu = new ChestMenu(" 增值包奖励");
//
//            menu.addMenuOpeningHandler(new ChestMenu.MenuOpeningHandler() {
//                @Override
//                public void onOpen(Player p) {
//                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F);
//                }
//            });
//
//
//            ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
//            ItemMeta meta = icon.getItemMeta();
//            meta.setDisplayName("§8[ §6增值包日常奖励 §8]");
//            List<String> lores = new ArrayList<>();
//            lores.add("");
//            lores.add("§7你好，亲爱的 §6" + p.getDisplayName());
//            lores.add("§7今日的增值包奖励: ");
//            lores.add("§3尘晶 §7x 180");
//            lores.add("§6探索值 §7x 10");
//            lores.add("§7已经发放给你啦，请注意查收");
//            lores.add("");
//            lores.add("§8     ----基地后勤处");
//            lores.add("");
//            lores.add("§8[ ModernSky ] reward");
//            meta.setLore(lores);
//            icon.setItemMeta(meta);
//            menu.addItem(4, icon);
//
//
////            menu.addMenuClickHandler(4, new ChestMenu.MenuClickHandler() {
////                @Override
////                public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
////
////                    return false;
////            }
////            });
//            menu.open(p);
//        }


}

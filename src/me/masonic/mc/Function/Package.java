package me.masonic.mc.Function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Package implements Listener {
    private static Core plugin;
    private static String type;

    public Package(Core plugin) {
        Package.plugin = plugin;
    }

    private static Gson gson = new Gson();
    private final static String COL_USER_NAME = Core.getInstance().getConfig().getString("SQL.sheet.sign.name");
    private final static String COL_USER_UUID = Core.getInstance().getConfig().getString("SQL.sheet.sign.uuid");
    private final static String COL_EXPIRE = Core.getInstance().getConfig().getString("SQL.sheet.sign.expire");
    private final static String SHEET = Core.getInstance().getConfig().getString("SQL.sheet.sign.sheet");

    private final static ArrayList<String> AVAILABLE_TYPE = new ArrayList<>(Arrays.asList("A"));

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColExpire() {
        return COL_EXPIRE;
    }

    public static String getSHEET() {
        return SHEET;
    }

    public static String getPackageState(Player p, String type) {
        if (isExpired(p, type)) {
            return "§7未开通增值包";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return "§7将于 §6" + sdf.format(new Date(Long.parseLong(String.valueOf(getExpire(p, type))) * 1000)) + " §7过期";
    }

    /**
     * 查询增值包是否过期
     *
     * @param p 要查询的玩家
     * @return 如果无记录，创建记录并返回true；如果有记录，查询记录是否过期并返回结果
     */

    public static Boolean isExpired(Player p, String type) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (!exist) {
                insertRecord(p);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (type) {
            case "A":
                try {
                    String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
                    ResultSet value = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPIRE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
                    HashMap<String, String> expire_map = gson.fromJson(value.getString(1), new TypeToken<HashMap<String, String>>() {
                    }.getType());
                    return expire_map.containsKey(type) && Integer.valueOf(expire_map.get(type)) < System.currentTimeMillis() / 1000;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return true;
    }

    /**
     * 查询增值包过期时间
     *
     * @param p 要查询的玩家
     * @return 如果无记录，返回0；否则返回增值包过期的时间，格式为时间戳(秒)
     */
    public static int getExpire(Player p, String type) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (!exist) {
                insertRecord(p);
                return 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (type) {
            case "A":
                String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
                try {
                    ResultSet value = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPIRE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
                    HashMap<String, String> expire_map = gson.fromJson(value.getString(1), new TypeToken<HashMap<String, String>>() {
                    }.getType());
                    return expire_map.containsKey(type) ? Integer.valueOf(expire_map.get(type)) : 0;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        return 0;
    }

    /**
     * 发放增值包
     *
     * @param p    玩家
     * @param time 增值包的有效期，单位为天
     * @param type 增值包类型
     * @return 增值包发放后的消息
     */
    public static String sendPackage(Player p, int time, String type) {
        try {
            Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
            if (!exist) {
                insertRecord(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        switch (type) {
            case "A":
                HashMap<String, String> expires = new HashMap<>();
                expires.put("A", Long.toString(isExpired(p, type) ? System.currentTimeMillis() / 1000 + 86400 * time : getExpire(p, type) + 86400 * time));
                String json = gson.toJson(expires);

                String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_EXPIRE, json, COL_USER_UUID, p.getUniqueId().toString()));
                return isExpired(p, type) ? "§7已开通A类增值包 §8[ §6" + String.valueOf(time) + "天 §8]" : "§7已续费A类增值包 §8[ §6" + String.valueOf(time) + "天 §8]";

        }
        return "";
    }

    private static void insertRecord(Player p) {
        String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUE(''{4}'', ''{5}'', ''{6}'')";
        sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPIRE, p.getUniqueId().toString(), p.getPlayerListName(), "[]");
        SqlUtil.update(sql);
    }

}

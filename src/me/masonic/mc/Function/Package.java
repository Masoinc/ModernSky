package me.masonic.mc.Function;

import lombok.Getter;
import lombok.SneakyThrows;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Privilege.AbilityPrivilege;
import me.masonic.mc.Function.Privilege.BackPackPrivilege;
import me.masonic.mc.Function.Privilege.ExpPriviledge;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class Package implements Listener {

    private final static String COL_USER_NAME = Core.getInstance().getConfig().getString("SQL.sheet.package.name");
    private final static String COL_USER_UUID = Core.getInstance().getConfig().getString("SQL.sheet.package.uuid");
    private final static String COL_EXPIRE = Core.getInstance().getConfig().getString("SQL.sheet.package.expire");
    private final static String SHEET = Core.getInstance().getConfig().getString("SQL.sheet.package.sheet");
    @Getter static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS {0}(`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8", Package.getSheetName(), Package.getColUserName(), Package.getColUserUuid(), Package.getColExpire());
//    private final static ArrayList<String> AVAILABLE_TYPE = new ArrayList<>(Arrays.asList("A"));

//    public static String getInitQuery() {
//        return INIT_QUERY;
//    }

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColExpire() {
        return COL_EXPIRE;
    }

    public static String getSheetName() {
        return SHEET;
    }

    public static String getPackageState(Player p, String type) {
        if (isExpired(p, type)) {
            return "§7未开通增值包";
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(new Timestamp(getExpire(p, type) * 1000).getTime());

        StringBuilder t = new StringBuilder();
        t.append(calendar.get(Calendar.YEAR)).append(" §7年 §6");
        t.append(calendar.get(Calendar.MONTH) + 1).append(" §7月§6 ");
        t.append(calendar.get(Calendar.DATE)).append(" §7日");

        return "§7将于 §6" + t + " §7过期";
    }

    /**
     * 查询增值包是否过期
     *
     * @param p 要查询的玩家
     * @return 如果无记录，创建记录并返回true；如果有记录，查询记录是否过期并返回结果
     */

    private static Boolean isExpired(Player p, String type) {
        Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
        if (!exist) {
            insertRecord(p);
            return true;
        }
        switch (type) {
            case "A":
                HashMap<String, String> expire_map = getPackageInfo(p);
                return expire_map.containsKey(type) && Integer.valueOf(expire_map.get(type)) < (System.currentTimeMillis() / 1000);
            default:
                return true;
        }
    }

    /**
     * 查询增值包过期时间
     *
     * @param p 要查询的玩家
     * @return 如果无记录，返回当前时间；否则返回增值包过期的时间，格式为时间戳(秒)
     */
    public static long getExpire(Player p, String type) {
        Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
        if (!exist) {
            insertRecord(p);
            return System.currentTimeMillis() / 1000;
        }

        switch (type) {
            case "A":
                HashMap<String, String> expire_map = getPackageInfo(p);
                return expire_map.containsKey(type) ? Integer.valueOf(expire_map.get(type)) : System.currentTimeMillis() / 1000;
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
        switch (type) {
            case "A":
                HashMap<String, String> expires = new HashMap<>();
                String msg;

                if (isExpired(p, type)) {
                    expires.put("A", Long.toString(((System.currentTimeMillis() / 1000) + 86400 * time)));
                    msg = "§7已开通A类增值包 §8[ §6" + String.valueOf(time) + "天 §8]";
                } else {
                    expires.put(type, String.valueOf((getExpire(p, type) + 86400 * time)));
                    msg = "§7已续费A类增值包 §8[ §6" + String.valueOf(time) + "天 §8]";
                }

                String json = new Gson().toJson(expires);
                SqlUtil.update(MessageFormat.format("UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''", SHEET, COL_EXPIRE, json, COL_USER_UUID, p.getUniqueId().toString()));

                ExpPriviledge.send(p, time * 86400, 150);
                AbilityPrivilege.send(p, time * 86400, 8, 30);
                BackPackPrivilege.send(p, time * 86400, 4);
                return msg;
        }
        return "";
    }

    private static void insertRecord(Player p) {
        String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUE(''{4}'', ''{5}'', ''{6}'')";
        sql = MessageFormat.format(sql, SHEET, COL_USER_UUID, COL_USER_NAME, COL_EXPIRE, p.getUniqueId().toString(), p.getPlayerListName(), "[]");
        SqlUtil.update(sql);
    }

    private static HashMap<String, String> getPackageInfo(Player p) {
        try {
            if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
                return new HashMap<>();
            }
            String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
            ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, COL_EXPIRE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
            assert rs != null;
            return new Gson().fromJson(rs.getString(1), new TypeToken<HashMap<String, String>>() {
            }.getType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}

package me.masonic.mc.Function.Privilege;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Objects.Icons;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Mason Project
 * 2018-1-16-0016
 */
public class PrivilegeManager implements Listener {

    private final static String COL_USER_NAME = Core.getInstance().getConfig().getString("SQL.sheet.privilege.name");
    private final static String COL_USER_UUID = Core.getInstance().getConfig().getString("SQL.sheet.privilege.uuid");
    private final static String COL_PRIVILEGE = Core.getInstance().getConfig().getString("SQL.sheet.privilege.privilege");
    private final static String SHEET = Core.getInstance().getConfig().getString("SQL.sheet.privilege.sheet");
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS {0}(`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8", PrivilegeManager.getSheetName(), PrivilegeManager.getColUserName(), PrivilegeManager.getColUserUuid(), PrivilegeManager.getColPrivilege());

    public PrivilegeManager() {
    }

    public static String getInitQuery() {
        return INIT_QUERY;
    }

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColPrivilege() {
        return COL_PRIVILEGE;
    }

    public static String getSheetName() {
        return SHEET;
    }
//    public HashMap<String, HashMap<String, Long>> RAW_MAP = new HashMap<>();

//    public static void setRawMap(Player p) {
//        if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
//        }
//    }

    static HashMap<String, HashMap<String, Long>> getRawMap(Player p) {
        if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
            createRecord(p);
            return new HashMap<>();
        }
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";

        try {
            ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, COL_PRIVILEGE, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
            return new Gson().fromJson(rs.getString(1), new TypeToken<HashMap<String, HashMap<String, Long>>>() {
            }.getType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();

    }

    static void setRawMap(Player p, HashMap<String, HashMap<String, Long>> map) {
        String json = new Gson().toJson(map);
        String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";
        SqlUtil.update(MessageFormat.format(sql, PrivilegeManager.getSheetName(), PrivilegeManager.getColPrivilege(), json, PrivilegeManager.getColUserUuid(), p.getUniqueId().toString()));
    }


    private static void createRecord(Player p) {
        String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'', ''{5}'', ''{6}'')";
        SqlUtil.update(MessageFormat.format(sql, SHEET, COL_PRIVILEGE, COL_USER_NAME, COL_USER_UUID, "[]", p.getPlayerListName(), p.getUniqueId().toString()));
    }

    /**
     * @param expire 以秒计
     */
    public static String getSendMsg(String keyword, Long expire) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(new Timestamp(expire * 1000).getTime());

        StringBuilder t = new StringBuilder();
        t.append(" §6");
        t.append(calendar.get(Calendar.YEAR)).append(" §7年 §6");
        t.append(calendar.get(Calendar.MONTH) + 1).append(" §7月§6 ");
        t.append(calendar.get(Calendar.DATE)).append(" §7日");

        String raw = "§7已开通{0}§7，有效期至{1}";
        switch (keyword) {
            case "exp":
                return MessageFormat.format(raw, "经验特权", t);
            case "bp":
                return MessageFormat.format(raw, "背包加成", t);
            case "ability":
                return MessageFormat.format(raw,"天赋加成", t);
        }
        return "";
    }

    public static void openPrivilegeMenu(Player p) {
        final ChestMenu menu = new ChestMenu(" 增值包与奖励");

        menu.addMenuOpeningHandler(p1 -> p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));
        Icons.addBaseIcon(menu, "back");
        Icons.addPrivIcon(menu, "pipe");
        menu.open(p);
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        ExpPriviledge.expireHandler(e.getPlayer());
        BackPackPrivilege.expireHandler(e.getPlayer());
        AbilityPrivilege.expireHandler(e.getPlayer());
    }
}


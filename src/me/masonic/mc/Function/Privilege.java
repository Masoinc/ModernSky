package me.masonic.mc.Function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Objects.Icons;
import me.masonic.mc.Utility.PermissionUtil;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Mason Project
 * 2018-1-16-0016
 */
public class Privilege implements Listener {

    private final static String COL_USER_NAME = Core.getInstance().getConfig().getString("SQL.sheet.privilege.name");
    private final static String COL_USER_UUID = Core.getInstance().getConfig().getString("SQL.sheet.privilege.uuid");
    private final static String COL_PRIVILEGE = Core.getInstance().getConfig().getString("SQL.sheet.privilege.privilege");
    private final static String SHEET = Core.getInstance().getConfig().getString("SQL.sheet.privilege.sheet");
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS {0}(`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8", Privilege.getSheetName(), Privilege.getColUserName(), Privilege.getColUserUuid(), Privilege.getColPrivilege());

    public Privilege() {
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

    private Long expire_time;

    public Long getExpire_time() {
        return expire_time;
    }

    /**
     * @param expire 以秒计
     */
    public Privilege(long expire) {
        this.expire_time = expire;
    }
//    public HashMap<String, HashMap<String, Long>> RAW_MAP = new HashMap<>();

    public static void setRawMap(Player p) {
        if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
        }
    }

    /**
     * @param p
     * @return
     */
    public static HashMap<String, HashMap<String, Long>> getRawMap(Player p) {
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

    /**
     * 获取经验特权对象，若无此特权，返回的对象的exist属性为false
     * 注: 自动创建记录
     *
     * @param p 玩家
     * @return 经验特权
     */
    public static ExpPriviledge getPlayerExpInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = getRawMap(p);
        HashMap<String, Long> expmap = rawmap.getOrDefault("exp", new HashMap<>());

        return expmap.containsKey("expire") && expmap.containsKey("amp") ?
                new ExpPriviledge(expmap.get("expire"), expmap.get("amp"), true) :
                new ExpPriviledge(0, 100, false);
    }

    private static void createRecord(Player p) {
        String sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'', ''{5}'', ''{6}'')";
        SqlUtil.update(MessageFormat.format(sql, SHEET, COL_PRIVILEGE, COL_USER_NAME, COL_USER_UUID, "[]", p.getPlayerListName(), p.getUniqueId().toString()));
    }

    /**
     * @param type   内定类型
     * @param expire 以秒计
     * @return
     */
    public static String getSendMsg(String type, Long expire) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(new Timestamp(expire * 1000).getTime());

        StringBuilder t = new StringBuilder();
        t.append(" §6");
        t.append(calendar.get(Calendar.YEAR)).append(" §7年 §6");
        t.append(calendar.get(Calendar.MONTH) + 1).append(" §7月§6 ");
        t.append(calendar.get(Calendar.DATE)).append(" §7日");

        String raw = "§7已开通{0}§7，有效期至{1}";
        switch (type) {
            case "exp":
                return MessageFormat.format(raw, "经验特权", t);
        }
        return "";
    }

    public String getFormattedExpire() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(new Timestamp(this.getExpire_time() * 1000).getTime());

        StringBuilder t = new StringBuilder();
        t.append(" §6");
        t.append(calendar.get(Calendar.YEAR)).append(" §7年 §6");
        t.append(calendar.get(Calendar.MONTH) + 1).append(" §7月§6 ");
        t.append(calendar.get(Calendar.DATE)).append(" §7日");
        return t.toString();
    }

    //    public ArrayList<Privilege> getPrivilegeList(Player p) {
//
//    }
    public static void openPrivilegeMenu(Player p) {
        final ChestMenu menu = new ChestMenu(" 增值包与奖励");

        menu.addMenuOpeningHandler(p1 -> p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));
        Icons.addBaseIcon(menu, "back");
        Icons.addPrivIcon(menu, "pipe");
        menu.open(p);
    }
}


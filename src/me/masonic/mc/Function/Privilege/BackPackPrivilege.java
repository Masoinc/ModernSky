package me.masonic.mc.Function.Privilege;

import com.google.gson.Gson;
import me.masonic.mc.Utility.MessageUtil;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

// bp
// - page
// - expire
public class BackPackPrivilege implements Privilege {
    private long page;
    private boolean exist;
    private long expire;
    private static final String KEYWORD = "bp";

    public BackPackPrivilege() {
    }

    /**
     * @param expire defauly = 0
     * @param page   default = 1
     * @param exist  default = false
     */
    private BackPackPrivilege(long expire, long page, boolean exist) {
        this.page = page;
        this.exist = exist;
        this.expire = expire;
    }

    public static BackPackPrivilege getInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = PrivilegeManager.getRawMap(p);
        HashMap<String, Long> bpmap = rawmap.getOrDefault(KEYWORD, new HashMap<>());

        return bpmap.containsKey("expire") && bpmap.containsKey("page") && bpmap.get("expire") > System.currentTimeMillis() / 1000 ?
                new BackPackPrivilege(bpmap.get("expire"), bpmap.get("page"), true) :
                new BackPackPrivilege(0L, 1L, false);
    }

    /**
     * @param p      玩家
     * @param period 时长，以秒计
     * @param page   页数，请输入加成后所得的页数
     */
    public static void send(Player p, long period, long page) {
        HashMap<String, Long> rawmap = new HashMap<>();
        BackPackPrivilege bp = getInstance(p);

        long expire = (!bp.exist || bp.isExpired()) ? System.currentTimeMillis() / 1000 + period : bp.getExpire() + period;
        page = bp.page > page ? bp.page : page;
        rawmap.put("page", page);
        rawmap.put("expire", expire);

        HashMap<String, HashMap<String, Long>> map = PrivilegeManager.getRawMap(p);
        map.put(KEYWORD, rawmap);
        String json = new Gson().toJson(map);
        String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";

        SqlUtil.update(MessageFormat.format(sql, PrivilegeManager.getSheetName(), PrivilegeManager.getColPrivilege(), json, PrivilegeManager.getColUserUuid(), p.getUniqueId().toString()));
        MessageUtil.sendFullMsg(p, PrivilegeManager.getSendMsg(KEYWORD, expire));

        PermissionsEx.getUser(p).addPermission("backpack." + String.valueOf(page));
        PermissionsEx.getUser(p).addPermission("msky.privilege.backpack");
    }

    @Override
    public String getFormattedExpire() {
        if (!exist || isExpired()) {
            return "未开通或已过期";
        }
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(new Timestamp(this.getExpire() * 1000).getTime());

        StringBuilder t = new StringBuilder();
        t.append("§6");
        t.append(calendar.get(Calendar.YEAR)).append(" §7年 §6");
        t.append(calendar.get(Calendar.MONTH) + 1).append(" §7月§6 ");
        t.append(calendar.get(Calendar.DATE)).append(" §7日");
        return t.toString();
    }

    public long getPage() {
        return page;
    }

    public String getFormattedPage() {
        return (!this.exist || this.isExpired() || this.page == 1) ?
                "§61 §7页§8[§7基础值§8]" :
                "§61 §7页§8[§7基础值§8] §7+ §3" + String.valueOf(this.page - 1);
    }

    @Override
    public boolean isExist() {
        return exist;
    }

    @Override
    public long getExpire() {
        return expire;
    }

    @Override
    public boolean isExpired() {
        return (!exist && this.expire <= (System.currentTimeMillis() / 1000));
    }


    public static void expireHandler(Player p) {
        if (getInstance(p).exist && getInstance(p).isExpired()) {
            for (int i = 2; i <= 10; i++) {
                PermissionsEx.getUser(p).removePermission("backpack." + String.valueOf(i));
            }
            MessageUtil.sendFullMsg(p, "您的§8[ §6背包加成 §8]§7奖励已过期了哦");
            HashMap<String, HashMap<String, Long>> map = PrivilegeManager.getRawMap(p);
            map.remove(KEYWORD);
            PrivilegeManager.setRawMap(p, map);

            PermissionsEx.getUser(p).removePermission("msky.privilege.backpack");
        }
    }
}

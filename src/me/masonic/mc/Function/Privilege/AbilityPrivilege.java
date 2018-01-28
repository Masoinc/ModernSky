package me.masonic.mc.Function.Privilege;

import me.masonic.mc.Utility.MessageUtil;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

// ability
// - discount
// - limit
// - expire
public class AbilityPrivilege implements Privilege {
    private long expire;
    private long limit;
    private long discount;
    private boolean exist;
    private static String KEYWORD = "ability";

    public AbilityPrivilege(long expire, long limit, long discount, boolean exist) {
        this.expire = expire;
        this.limit = limit;
        this.discount = discount;
        this.exist = exist;
    }

    public long getLimit() {
        return limit;
    }

    public long getDiscount() {
        return discount;
    }

    public static AbilityPrivilege getInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = PrivilegeManager.getRawMap(p);
        HashMap<String, Long> bpmap = rawmap.getOrDefault(KEYWORD, new HashMap<>());

        return bpmap.containsKey("expire") && bpmap.containsKey("discount") && bpmap.containsKey("limit") && bpmap.get("expire") > System.currentTimeMillis() / 1000 ?
                new AbilityPrivilege(bpmap.get("expire"), bpmap.get("limit"), bpmap.get("discount"), true) :
                new AbilityPrivilege(0L, 5L, 0L, false);
    }

    public String getFormattedDiscount() {
        return (!this.exist || this.isExpired() || this.discount == 0) ?
                "§7暂无" :
                "§7-&6" + String.valueOf(this.discount) + "%";
    }

    public String getFormattedLimit() {
        return (!this.exist || this.isExpired() || this.limit == 5) ?
                "&65 &7项§8[§7基础值§8]" :
                "&65 &7项§8[§7基础值§8] + &3" + String.valueOf(this.limit - 5);
    }

    public static void send(Player p, long period, long limit, long discount) {
        HashMap<String, Long> rawmap = new HashMap<>();
        AbilityPrivilege ab = getInstance(p);

        long expire = (!ab.exist || ab.isExpired()) ? System.currentTimeMillis() / 1000 + period : ab.expire + period;
        limit = ab.limit > limit ? ab.limit : limit;
        discount = ab.discount > discount ? ab.discount : discount;

        rawmap.put("limit", limit);
        rawmap.put("expire", expire);
        rawmap.put("discount", discount);

        HashMap<String, HashMap<String, Long>> map = PrivilegeManager.getRawMap(p);
        map.put(KEYWORD, rawmap);
        PrivilegeManager.setRawMap(p, map);
        MessageUtil.sendFullMsg(p, PrivilegeManager.getSendMsg(KEYWORD, expire));


        PermissionsEx.getUser(p).addPermission("msky.privilege.ability");
        PermissionsEx.getUser(p).addPermission("advancedabilities.discount." + discount);
        PermissionsEx.getUser(p).addPermission("advancedabilities.ablity." + limit);
    }

    @Override
    public long getExpire() {
        return expire;
    }

    @Override
    public boolean isExist() {
        return exist;
    }

    @Override
    public boolean isExpired() {
        return (!exist && this.expire <= (System.currentTimeMillis() / 1000));
    }

    public static void expireHandler(Player p) {
        if (getInstance(p).exist && getInstance(p).isExpired()) {

            MessageUtil.sendFullMsg(p, "您的§8[ §6背包加成 §8]§7奖励已过期了哦");
            HashMap<String, HashMap<String, Long>> map = PrivilegeManager.getRawMap(p);
            map.remove("bp");
            PrivilegeManager.setRawMap(p, map);

            PermissionsEx.getUser(p).removePermission("msky.privilege.ability");
            PermissionsEx.getUser(p).removePermission("advancedabilities.discount." + getInstance(p).discount);
            PermissionsEx.getUser(p).removePermission("advancedabilities.ablity." + getInstance(p).limit);
        }
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
}

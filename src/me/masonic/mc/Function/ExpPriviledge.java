package me.masonic.mc.Function;

import com.google.gson.Gson;
import me.masonic.mc.Utility.MessageUtil;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.HashMap;

public class ExpPriviledge extends Privilege {
    long amplifier;
    boolean exist;

    /**
     * @param expire    以秒计的时间戳
     * @param amplifier 经验倍率，默认为100
     * @param exist     是否有此特权
     */
    public ExpPriviledge(long expire, long amplifier, boolean exist) {
        super(expire);
        this.amplifier = amplifier;
        this.exist = exist;
    }

    public long getAmplifier() {
        return amplifier;
    }

    public boolean isExist() {
        return exist;
    }

    public boolean isExpired() {
        return this.getExpire_time() <= System.currentTimeMillis() / 1000;
    }

    /**
     * 获取经验特权对象，若无此特权，返回的对象的exist属性为false
     * 注: 自动创建记录
     *
     * @param p 玩家
     * @return 经验特权
     */
    public static ExpPriviledge getPlayerInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = Privilege.getRawMap(p);
        HashMap<String, Long> expmap = rawmap.getOrDefault("exp", new HashMap<>());

        return expmap.containsKey("expire") && expmap.containsKey("amp") ?
                new ExpPriviledge(expmap.get("expire"), expmap.get("amp"), true) :
                new ExpPriviledge(0, 100, false);
    }

    public static HashMap<String, Long> getExpRawMap(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = Privilege.getRawMap(p);

        return rawmap.getOrDefault("exp", new HashMap<>());
    }

    public static String getFormattedAmplifier(Player p) {
        return (getPlayerInstance(p).getAmplifier() == 1) ?
                "§6100§7%§8[§7基础值§8]" :
                "§6100§7%§8[§7基础值§8] §7+ §3" + String.valueOf(getPlayerInstance(p).getAmplifier() - 100) + "§7%";
    }

//    public static void sendExpPrivilege(Player p, long period) {
//        if (getPlayerInstance(p).isExist())
//
//    }

    /**
     * 发放经验特权
     *
     * @param p         玩家
     * @param period    特权时长，以秒计
     * @param amplifier 经验倍率，默认为1
     */
    public static void sendExpPrivilege(Player p, long period, long amplifier) {
        HashMap<String, Long> rawmap = new HashMap<>();
        long expire;
        if (!getPlayerInstance(p).isExist() || getPlayerInstance(p).isExpired()) {
            expire = System.currentTimeMillis() / 1000 + period;
            rawmap.put("amp", amplifier);
        } else {
            expire = getPlayerInstance(p).getExpire_time() + period;
            rawmap.put("amp", getPlayerInstance(p).getAmplifier());
        }
        rawmap.put("expire", expire);
        HashMap<String, HashMap<String, Long>> map = Privilege.getRawMap(p);
        map.put("exp", rawmap);
        String json = new Gson().toJson(map);
        String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";

        SqlUtil.update(MessageFormat.format(sql, Privilege.getSheetName(), Privilege.getColPrivilege(), json, Privilege.getColUserUuid(), p.getUniqueId().toString()));
        MessageUtil.sendFullMsg(p, Privilege.getSendMsg("exp", expire));
    }

}

package me.masonic.mc.Function;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class ExpPriviledge extends Privilege {
    double amplifier;
    boolean exist;

    /**
     * @param expire    以秒计的时间戳
     * @param amplifier 经验倍率，默认为1
     * @param exist     是否有此特权
     */
    public ExpPriviledge(long expire, long amplifier, boolean exist) {
        super(expire);
        this.amplifier = amplifier;
        this.exist = exist;
    }

    public double getAmplifier() {
        return amplifier;
    }

    public boolean isExist() {
        return exist;
    }

    public boolean isExpired() {
        return this.getExpire_time() <= System.currentTimeMillis();
    }

    /**
     * 获取经验特权对象，若无此特权，返回的对象的exist属性为false
     *
     * @param p 玩家
     * @return 经验特权
     */
    public static ExpPriviledge getPlayerInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = Privilege.getRawMap(p);
        HashMap<String, Long> expmap = rawmap.getOrDefault("exp", new HashMap<>());

        return expmap.containsKey("expire") && expmap.containsKey("amp") ?
                new ExpPriviledge(expmap.get("expire"), expmap.get("amp"), true) :
                new ExpPriviledge(0, 1, false);
    }

    public static HashMap<String, Long> getRawMap(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = Privilege.getRawMap(p);
        return rawmap.getOrDefault("exp", new HashMap<>());
    }

    public static String getFormattedAmplifier(Player p) {
        return (getPlayerInstance(p).getAmplifier() == 1) ?
                "§6100§7%§8[§7基础值§8]" :
                "§6100§7%§8[§7基础值§8] §7+ §3" + String.valueOf((getPlayerInstance(p).getAmplifier() - 1) * 100) + "§7%";
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
        if (!getPlayerInstance(p).isExist() || getPlayerInstance(p).isExpired()) {
            HashMap<String, Long> rawmap = new HashMap<>();
            rawmap.put("expire", System.currentTimeMillis() / 1000 + period);
            rawmap.put("amp", amplifier);
            Privilege.getRawMap(p).put("exp", rawmap);

        } else {
            long crt = getPlayerInstance(p).getExpire_time();
        }
    }

}

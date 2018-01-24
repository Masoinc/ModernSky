package me.masonic.mc.Function;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class ExpPriviledge extends Privilege {
    public double amplifier;

    public ExpPriviledge(long expire, long amplifier, boolean exist) {
        super(expire);
        this.amplifier = amplifier;
    }

    public double getAmplifier() {
        return amplifier;
    }

    /**
     * 获取经验特权对象，若无此特权，返回的对象的exist属性为false
     *
     * @param p 玩家
     * @return 经验特权
     */
    public static ExpPriviledge getPlayerInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = getRawmap(p);
        HashMap<String, Long> expmap = rawmap.getOrDefault("exp", new HashMap<>());

        return expmap.containsKey("expire") && expmap.containsKey("amp") ?
                new ExpPriviledge(expmap.get("expire"), expmap.get("amp"), true) :
                new ExpPriviledge(0, 1, false);
    }

    public static String getFormattedAmplifier(Player p) {
        return (getPlayerInstance(p).getAmplifier() == 1) ?
                "§6100§7%§8[§7基础值§8]" :
                "§6100§7%§8[§7基础值§8] §7+ §3" + String.valueOf((getPlayerInstance(p).getAmplifier() - 1) * 100) + "§7%";
    }

}

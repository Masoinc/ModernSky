package me.masonic.mc.Function;

import com.google.gson.Gson;
import me.masonic.mc.Utility.MessageUtil;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;
import net.aufdemrand.denizen.nms.NMSHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.text.MessageFormat;
import java.util.HashMap;

public class ExpPriviledge extends Privilege {
    long amplifier;
    boolean exist;

    public ExpPriviledge() {

    }
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

    public static HashMap<String, Long> getExpRawMap(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = Privilege.getRawMap(p);

        return rawmap.getOrDefault("exp", new HashMap<>());
    }

    public static String getFormattedAmplifier(Player p) {
        ExpPriviledge exp = Privilege.getPlayerExpInstance(p);
        return (exp.getAmplifier() == 1) ?
                "§6100§7%§8[§7基础值§8]" :
                "§6100§7%§8[§7基础值§8] §7+ §3" + String.valueOf(exp.getAmplifier() - 100) + "§7%";
    }

    /**
     * 发放经验特权
     *
     * @param p         玩家
     * @param period    特权时长，以秒计
     * @param amplifier 经验倍率，默认为1
     */
    public static void sendExpPrivilege(Player p, long period, long amplifier) {
        HashMap<String, Long> rawmap = new HashMap<>();
        ExpPriviledge exp = Privilege.getPlayerExpInstance(p);
        long expire;
        if (!exp.isExist() || exp.isExpired()) {
            expire = System.currentTimeMillis() / 1000 + period;
            rawmap.put("amp", amplifier);
        } else {
            expire = exp.getExpire_time() + period;
            rawmap.put("amp", exp.getAmplifier());
        }
        rawmap.put("expire", expire);
        HashMap<String, HashMap<String, Long>> map = Privilege.getRawMap(p);
        map.put("exp", rawmap);
        String json = new Gson().toJson(map);
        String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";

        SqlUtil.update(MessageFormat.format(sql, Privilege.getSheetName(), Privilege.getColPrivilege(), json, Privilege.getColUserUuid(), p.getUniqueId().toString()));
        MessageUtil.sendFullMsg(p, Privilege.getSendMsg("exp", expire));
    }

    @EventHandler
    private void onExp(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        String msg = MessageFormat.format("§8[§6 经验特权 §8] §7获得的经验: §3+{0}%", Privilege.getPlayerExpInstance(p).getAmplifier() - 100);
        NMSHandler.getInstance().getPacketHelper().sendActionBarMessage(p, msg);
        e.setAmount((int) (e.getAmount() * getPlayerExpInstance(e.getPlayer()).getAmplifier()));

    }
}

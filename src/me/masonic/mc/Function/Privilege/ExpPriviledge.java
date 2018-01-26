package me.masonic.mc.Function.Privilege;

import com.google.gson.Gson;
import me.masonic.mc.Utility.MessageUtil;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.entity.Player;
import net.aufdemrand.denizen.nms.NMSHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

// exp
// - amp
// - expire
public class ExpPriviledge implements Privilege, Listener {
    private long amplifier;
    private boolean exist;
    private long expire;

    public ExpPriviledge() {
    }

    /**
     * @param expire    以秒计的时间戳
     * @param amplifier 经验倍率，默认为100
     * @param exist     是否有此特权
     */
    private ExpPriviledge(long expire, long amplifier, boolean exist) {
        this.expire = expire;
        this.amplifier = amplifier;
        this.exist = exist;
    }

    /**
     * 获取经验特权对象，若无此特权，返回的对象的exist属性为false
     * 注: 自动创建记录
     *
     * @param p 玩家
     * @return 经验特权
     */
    public static ExpPriviledge getInstance(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = PrivilegeManager.getRawMap(p);
        HashMap<String, Long> expmap = rawmap.getOrDefault("exp", new HashMap<>());

        return expmap.containsKey("expire") && expmap.containsKey("amp") ?
                new ExpPriviledge(expmap.get("expire"), expmap.get("amp"), true) :
                new ExpPriviledge(0, 100, false);
    }

    /**
     * 发放经验特权
     *
     * @param p         玩家
     * @param period    特权时长，以秒计
     * @param amplifier 经验倍率，默认为1
     */

    public static void send(Player p, long period, long amplifier) {
        HashMap<String, Long> rawmap = new HashMap<>();
        ExpPriviledge exp = getInstance(p);
        long expire;
        if (!exp.exist || exp.isExpired()) {
            expire = System.currentTimeMillis() / 1000 + period;
            rawmap.put("amp", amplifier);
        } else {
            expire = exp.getExpire() + period;
            rawmap.put("amp", exp.getAmplifier());
        }
        rawmap.put("expire", expire);
        HashMap<String, HashMap<String, Long>> map = PrivilegeManager.getRawMap(p);
        map.put("exp", rawmap);
        String json = new Gson().toJson(map);
        String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";

        SqlUtil.update(MessageFormat.format(sql, PrivilegeManager.getSheetName(), PrivilegeManager.getColPrivilege(), json, PrivilegeManager.getColUserUuid(), p.getUniqueId().toString()));
        MessageUtil.sendFullMsg(p, PrivilegeManager.getSendMsg("exp", expire));
    }

    @Override
    public long getExpire() {
        return this.expire;
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

    public long getAmplifier() {
        return amplifier;
    }

    public String getFormattedAmplifier() {
        return (!this.exist || this.isExpired() || this.amplifier == 100) ?
                "§6100§7%§8[§7基础值§8]" :
                "§6100§7%§8[§7基础值§8] §7+ §3" + String.valueOf(this.getAmplifier() - 100) + "§7%";
    }

    @Override
    public boolean isExist() {
        return exist;
    }

    @Override
    public boolean isExpired() {
        return this.expire <= (System.currentTimeMillis() / 1000);
    }

    public static HashMap<String, Long> getExpRawMap(Player p) {
        HashMap<String, HashMap<String, Long>> rawmap = PrivilegeManager.getRawMap(p);
        return rawmap.getOrDefault("exp", new HashMap<>());
    }

    @EventHandler
    private void onExp(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        if (!getInstance(p).isExist()) {
            return;
        }
        if (getInstance(p).isExpired()) {
            return;
        }
        String msg = MessageFormat.format("§8[§6 经验特权 §8] §7获得的经验: §3+{0}%", getInstance(p).getAmplifier() - 100);
        NMSHandler.getInstance().getPacketHelper().sendActionBarMessage(p, msg);
        e.setAmount((int) (e.getAmount() * getInstance(p).getAmplifier()));
    }
}

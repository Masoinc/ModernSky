package me.masonic.mc.Function;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.SqlUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.UUID;

/**
 * Mason Project
 * 2018-2-17-0017
 */
public class Online {

    public final static String COL_USER_NAME = "user_name";
    public final static String COL_USER_UUID = "user_uuid";
    public final static String COL_ONTIME = "ontime";
    public final static String SHEET = "online";
    public final static String INIT_QUERY = MessageFormat.format(
            "CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_ONTIME);

    static Cache<UUID, Long> cache;
    public final static OnlineListener LISTENER = new OnlineListener();

//    private HashMap<String, HashMap<String, Integer>> getCache() {
//        return cache.containsKey(this.player) ? cache.get(this.player).classified_stock : classifier(this.player);
//    }

    public static void initCache() {
        OnlineCache.init();
    }

    public static void addOnlineStat(UUID p, long period) {
        OnlineStat t = OnlineStat.getInstance(p);
        assert t != null;
        t.ontime_total += period;
        t.ontime_today += period;
        t.save();
    }
}

class OnlineCache {
    static void init() {
        CacheManager cacheManager = Core.getCacheManager();
        Online.cache = cacheManager.createCache("online_cache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, Long.class, ResourcePoolsBuilder.heap(10)));
    }
}

class OnlineStat {
    long ontime_today;
    long ontime_total;
    UUID p;

    public OnlineStat(long ontime_today, long ontime_total, UUID p) {
        this.ontime_today = ontime_today;
        this.ontime_total = ontime_total;
        this.p = p;
    }

    public static OnlineStat getInstance(UUID p) {
        String sql;
        OnlineStat output = new OnlineStat(0, 0, p);
        String json = new Gson().toJson(output);
        if (!SqlUtil.ifExist(p, Online.SHEET, Online.COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'', ''{5}'', ''{6}'');";
            SqlUtil.update(MessageFormat.format(sql, Online.SHEET, Online.COL_USER_NAME, Online.COL_USER_UUID, Online.COL_ONTIME, Bukkit.getPlayer(p).getPlayerListName(), p, json));
            return new OnlineStat(0, 0, p);
        }
        sql = "SELECT `{0}` FROM {1} WHERE `{2}` = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, Online.COL_ONTIME, Online.SHEET, Online.COL_USER_UUID, p));
        try {
            assert rs != null;
            while (rs.next()) {
                String raw_json = rs.getString(1);
                return new Gson().fromJson(raw_json, new TypeToken<OnlineStat>() {
                }.getType());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new OnlineStat(0, 0, p);
    }

    public void save() {
        String json = new Gson().toJson(this);
        String sql;
        if (!SqlUtil.ifExist(p, Online.SHEET, Online.COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'', ''{5}'', ''{6}'');";
            SqlUtil.update(MessageFormat.format(sql, Online.SHEET, Online.COL_USER_NAME, Online.COL_USER_UUID, Online.COL_ONTIME, Bukkit.getPlayer(this.p).getPlayerListName(), this.p, json));
            return;
        }
        sql = "UPDATE {0} SET `{1}` = ''{2}'' WHERE `{3}` = ''{4}'';";
        SqlUtil.update(MessageFormat.format(sql, Online.SHEET, Online.COL_ONTIME, json, Online.COL_USER_UUID, this.p));
    }
}

class OnlineListener implements Listener {
    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        Online.cache.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        System.out.println(Online.cache.toString());
    }

    @EventHandler
    void onExit(PlayerQuitEvent e) {
        if (!Online.cache.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        long login = Online.cache.get(e.getPlayer().getUniqueId());
        long logout = System.currentTimeMillis();
        Online.cache.remove(e.getPlayer().getUniqueId());
//        if (logout - login < 60000) {
//            return;
//        }
        Online.addOnlineStat(e.getPlayer().getUniqueId(), logout - login);
    }
}
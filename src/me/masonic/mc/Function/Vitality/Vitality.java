package me.masonic.mc.Function.Vitality;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Reward;
import me.masonic.mc.Objects.Icons;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

// - COL_PROGRESSSET
//    - CODENAME
//      - Progress1 进度(int/100)
//      - Progress2 进度
//      - Progress3 进度
public class Vitality {

    public final static String COL_USER_NAME = "user_name";
    public final static String COL_USER_UUID = "user_uuid";
    public final static String COL_VITALITY = "vitality";
    public final static String COL_PROGRESS_SET = "progress";
    public final static String SHEET = "vitality";
    public final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL, `{4}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_VITALITY, COL_PROGRESS_SET);

    static Cache<UUID, VitalityRecord> cache;

    static List<Integer> quest_slot = new LinkedList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));

    public static void initCache() {
        VitalityCache.init();
    }

    public static void openvi(Player p) {
        final ChestMenu menu = new ChestMenu("  日常-活跃度");
        Icons.addBaseIcon(menu, "back", 43);
        menu.addItem(37,VitalityRecord.getInstance(p.getUniqueId()).getStat());
        menu.addMenuClickHandler(37, (p1, p2, p3, p4) -> false);
        int index = 0;
        for (VitalityQuest vq : VitalityQuest.values()) {
            menu.addItem(quest_slot.get(index), vq.getIcon());
            menu.addMenuClickHandler(quest_slot.get(index), (p1, p2, p3, p4) -> false);
            index++;
        }
        menu.open(p);
    }

    public static VitalityListener getListener() {
        return new VitalityListener();
    }
}

class VitalityRecord {
    HashMap<String, HashMap<String, Integer>> progress;
    UUID p;
    int vitality;

    VitalityRecord(UUID p, HashMap<String, HashMap<String, Integer>> progress, int vitality) {
        this.progress = progress;
        this.vitality = vitality;
        this.p = p;
    }

    ItemStack getStat() {
        ItemStack icon = new ItemStack(Material.DIAMOND);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§8[ §6活跃度信息 §8]");
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7◇ 目前活跃度: " + this.vitality);
//        lores.add("§7◇ 已完成的任务链:  " + this.desc);
        lores.add("");
        lores.add("§8[ ModernSky ] vitality");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }

    static VitalityRecord getInstance(UUID p) {
        return Vitality.cache.containsKey(p) ? Vitality.cache.get(p) : new VitalityRecord(p, getProgressRecord(p),0);
    }

    int getProgress(String quest_codename) {
        return this.progress.getOrDefault(quest_codename, new HashMap<>()).getOrDefault("pro1", 0);
    }

    void setProgress(String quest_codename, int progress) {
        this.progress.put(quest_codename, new HashMap<String, Integer>() {{
            put("pro1", progress);
        }});

        this.save();
        refreshCache();
    }

    public void refreshCache() {
        Vitality.cache.put(this.p, getInstance(p));
    }

    void save() {
        String sql;
        if (!SqlUtil.ifExist(this.p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`, `{4}`) VALUES(''{5}'',''{6}'',''{7}'', ''{8}'');";
            SqlUtil.update(MessageFormat.format(sql, Vitality.SHEET, Vitality.COL_USER_NAME, Vitality.COL_USER_UUID, Vitality.COL_PROGRESS_SET,
                    Vitality.COL_VITALITY, Bukkit.getPlayer(p).getPlayerListName(), p, new Gson().toJson(progress), 0));
            return;
        }

        sql = "UPDATE {0} SET `{1}` = ''{2}'' WHERE `{3}` = ''{4}'';";
        SqlUtil.update(MessageFormat.format(sql, Vitality.SHEET, Vitality.COL_PROGRESS_SET, new Gson().toJson(progress), Vitality.COL_USER_UUID, this.p));
    }

    static HashMap<String, HashMap<String, Integer>> getProgressRecord(UUID p) {
        if (!SqlUtil.ifExist(p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            return new HashMap<>();
        }
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, Vitality.COL_PROGRESS_SET, Vitality.SHEET, Vitality.COL_USER_UUID, p));
        assert rs != null;
        try {
            return new Gson().fromJson(rs.getString(1), new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
            }.getType());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}

class VitalityCache {
    static void init() {
        CacheManager cacheManager = Core.getCacheManager();
        Vitality.cache = cacheManager.createCache("vitality_cache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, VitalityRecord.class, ResourcePoolsBuilder.heap(10)));
    }
}

enum VitalityQuest {
    LOGIN1("LOGIN1", "登入游戏", 5, new Reward(50, 0, new HashMap<String, Integer>() {{
    }})),
    PLAY1("PLAY1", "在线 §61 §7小时", 10, new Reward(100, 20, new HashMap<String, Integer>() {{
    }})),
    PLAY2("PLAY2", "§611:00-13:00 §7期间在线", 5, new Reward(50, 20, new HashMap<String, Integer>() {{
    }})),
    PLAY3("PLAY3", "§617:00-19:00 §7期间在线", 5, new Reward(100, 20, new HashMap<String, Integer>() {{
    }})),
    SLIMEFUN1("SLIMEFUN1", "制造任意一种太阳能发电机", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf3", 1);
    }})),
    SLIMEFUN2("SLIMEFUN2", "制造任意一种蓄电池", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf13", 4);
    }})),
    SLIMEFUN3("SLIMEFUN3", "制造 §9末影之尘 §8- §2III §7x3", 20, new Reward(80, 0, new HashMap<String, Integer>() {{
        put("sf8", 10);
    }})),
    ADVANCEDAB1("ADVANCEDAB1", "解锁任意被动天赋", 20, new Reward(150, 0, new HashMap<String, Integer>() {{
    }})),
    KILL1("KILL1", "无伤击杀 §620 §7只僵尸", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
    }})),
    POTION1("POTION1", "酿造 §63 §7种药水", 15, new Reward(60, 20, new HashMap<>()).appendRawItem(new HashMap<Material, Integer>() {{
        put(Material.SAND, 10);
    }}));

    String desc;
    String codename;
    int vitality;
    Reward reward;

    VitalityQuest(String codename, String desc, int vitality, Reward reward) {
        this.desc = desc;
        this.codename = codename;
        this.vitality = vitality;
        this.reward = reward;
    }

    ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.BOOK);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(MessageFormat.format("§8[ §6活跃度任务·{0} §8]", this.codename));
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ 任务目标:");
        lores.add("§7○ " + this.desc);
        lores.add("");
        lores.add("§7▽ 任务奖励: ");
        lores.addAll(this.reward.getLore());
        lores.add("§7○ §6活跃度§7 x " + String.valueOf(this.vitality));
        lores.add("");
        lores.add("§8[ ModernSky ] vitality");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }


}

class VitalityListener implements Listener {

    @EventHandler
    private void onJoin(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        VitalityRecord vr = VitalityRecord.getInstance(p.getUniqueId());

        if (vr.getProgress(VitalityQuest.LOGIN1.codename) < 100) {
            vr.setProgress(VitalityQuest.LOGIN1.codename, 100);
            VitalityQuest.LOGIN1.reward.send(p);
            p.sendMessage(Core.getPrefix() + MessageFormat.format("日常活跃度任务§8[ §6{0} §8]奖励已发放", VitalityQuest.LOGIN1.desc));
        }
    }
}

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ehcache.CacheManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

// Data Structure
// COL_PROGRESS
// - VitalityRecord(json)
//   - p: uuid
//   - vitality: v
//   - progress:  HashMap<String, Integer>
//     - quest_codename: progress[100,0,2]
//     - quest_codename: progress[100]

// 数据存储对象的成员变量必须初始化
// 使用ResultSet的get方法务必注意结果为空的情况
public class Vitality implements Listener {

    public final static String COL_USER_NAME = "user_name";
    public final static String COL_USER_UUID = "user_uuid";
    public final static String COL_PROGRESS = "progress";
    public final static String SHEET = "vitality";
    public final static String INIT_QUERY = MessageFormat.format(
            "CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_PROGRESS);

    public final static VitalityListener LISTENER = new VitalityListener();

    static List<Integer> quest_slot = new LinkedList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));

    public static void initCache() {
        VitalityCache.init();
    }

    public static void setVitality(Player p, int v) {
        VitalityRecord.getInstance(p.getUniqueId()).setVitality(v);
    }

    public static void addVitality(UUID p, int v) {
        VitalityRecord.getInstance(p).addVitality(v);
    }

    public static int getVitality(Player p) {
        return VitalityRecord.getInstance(p.getUniqueId()).vitality;
    }

    public static void setProgress(Player p, String codename, Integer progress) {
        VitalityRecord.getInstance(p.getUniqueId()).setProgress(codename, progress);
    }

    public static int getProgress(Player p, String codename) {
        return VitalityRecord.getInstance(p.getUniqueId()).getQuestProgress(codename);
    }

    public static VitalityListener getListener() {
        return new VitalityListener();
    }

    public static void openvi(Player p) {
        final ChestMenu menu = new ChestMenu("  日常-活跃度");
        Icons.addBaseIcon(menu, "back", 43);
        menu.addItem(37, VitalityRecord.getInstance(p.getUniqueId()).getStat());
        menu.addMenuClickHandler(37, (p1, p2, p3, p4) -> false);
        int index = 0;
        for (VitalityQuest vq : VitalityQuest.values()) {
            boolean completed = VitalityRecord.getInstance(p.getUniqueId()).getQuestProgress(vq.codename) >= 100;
            menu.addItem(quest_slot.get(index), vq.getIcon(completed));
            menu.addMenuClickHandler(quest_slot.get(index), (p1, p2, p3, p4) -> false);
            index++;
        }
        menu.open(p);
    }
}

class VitalityRecord {
    // 类变量使用泛型时要指定类型进行初始化
    private HashMap<String, Integer> progress = new HashMap();
    private UUID p;
    int vitality = 0;

    private VitalityRecord(UUID p, HashMap<String, Integer> progress, int vitality) {
        this.progress = progress;
        this.vitality = vitality;
        this.p = p;
    }

    public HashMap<String, Integer> getProgress() {
        return this.progress;
    }

    ItemStack getStat() {
        ItemStack icon = new ItemStack(Material.DIAMOND);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§8[ §6活跃度信息 §8]");
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7◇ 目前活跃度: §6" + this.vitality);
        lores.add("§7△ 活跃度每日 §64:00 §7清零");
//        lores.add("§7◇ 已完成的任务链:  " + this.desc);
        lores.add("");
        lores.add("§8[ ModernSky ] vitality");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }

    static VitalityRecord getInstance(UUID p) {
        return new VitalityRecord(p, getProgressRecord(p), getVitalityValue(p));
    }

    int getQuestProgress(String quest_codename) {
        HashMap<String, Integer> quest_map = this.getProgress();

        return quest_map == null ? 0 : (quest_map.getOrDefault(quest_codename, 0));
    }

    void setProgress(String quest_codename, Integer progress) {
        HashMap<String, Integer> quest_map = this.progress;
        quest_map.put(quest_codename, progress);
        this.progress = quest_map;
        this.save();
//        refreshCache();
    }

    void setVitality(int v) {
        this.vitality = v;
        this.save();
    }

    void addVitality(int v) {
        this.vitality += v;
        this.save();
    }

//    public void refreshCache() {
//        Vitality.cache.put(this.p, getInstance(p));
//    }

    private void save() {
        String sql;
        if (!SqlUtil.ifExist(this.p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'',''{5}'',''{6}'');";
            String sql_processed = MessageFormat.format(sql,
                    Vitality.SHEET,
                    Vitality.COL_USER_NAME,
                    Vitality.COL_USER_UUID,
                    Vitality.COL_PROGRESS,
                    Bukkit.getPlayer(p).getPlayerListName(),
                    p,
                    new Gson().toJson(this.progress),
                    this.vitality);
            SqlUtil.update(sql_processed);
            if (Core.SQL_DEBUG) {
                System.out.println(sql_processed);
            }
            return;
        }
        sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}'';";
        SqlUtil.update(MessageFormat.format(sql,
                Vitality.SHEET,
                Vitality.COL_PROGRESS,
                new Gson().toJson(this),
                Vitality.COL_USER_UUID,
                this.p));
    }

    private static int getVitalityValue(UUID p) {
        if (!SqlUtil.ifExist(p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            return 0;
        }
        String sql = "SELECT {0} FROM {1} WHERE `{2}` = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, Vitality.COL_PROGRESS, Vitality.SHEET, Vitality.COL_USER_UUID, p));
        try {
            assert rs != null;
            while(rs.next()) {
                String raw_json = rs.getString(1);
                VitalityRecord vr = new Gson().fromJson(raw_json, new TypeToken<VitalityRecord>() {
                }.getType());
                return vr.vitality;
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 从数据库取得 Progress 记录并翻译
     *
     * @param p 玩家
     * @return HashMap<String, Integer> 格式的 Progress 记录
     */
    private static HashMap<String, Integer> getProgressRecord(UUID p) {
        HashMap<String, Integer> init_map = new HashMap<String, Integer>() {{
            put("sf8", 10);
        }};
        if (!SqlUtil.ifExist(p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            return init_map;
        }
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, Vitality.COL_PROGRESS, Vitality.SHEET, Vitality.COL_USER_UUID, p));
        assert rs != null;
        try {
            // 忘加resultset为空的判断
            Boolean empty = true;
            while(rs.next()) {
                String raw_json = rs.getString(1);
                VitalityRecord vr = new Gson().fromJson(raw_json, new TypeToken<VitalityRecord>() {
                }.getType());
                return vr.progress;
            }
            return init_map;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return init_map;
    }
}

class VitalityCache {
    static void init() {
        CacheManager cacheManager = Core.getCacheManager();
//        Vitality.cache = cacheManager.createCache("vitality_cache",
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, VitalityRecord.class, ResourcePoolsBuilder.heap(10)));
    }
}

class VitalityListener implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (Vitality.getProgress(p, VitalityQuest.LOGIN1.codename) < 100) {
            Vitality.setProgress(p, VitalityQuest.LOGIN1.codename, 100);
            VitalityQuest.LOGIN1.reward.send(p.getUniqueId());
            p.sendMessage(Core.getPrefix() + MessageFormat.format("日常活跃度任务§8[ §6{0} §8]§7奖励已发放", VitalityQuest.LOGIN1.desc));
        }
    }


}

enum VitalityQuest {
    LOGIN1("§7每日打卡 §8- §6I", "LOGIN1", "登入游戏", new Reward(50, 0, new HashMap<String, Integer>() {{
    }}).appendVitality(5)),
    PLAY1("§7每日打卡 §8- §6II", "PLAY1", "在线 §61 §7小时", new Reward(100, 20, new HashMap<String, Integer>() {{
    }}).appendVitality(5)),
    PLAY2("§7每日打卡 §8- §6III", "PLAY2", "§611:00-13:00 §7期间在线", new Reward(50, 20, new HashMap<String, Integer>() {{
    }}).appendVitality(5)),
    PLAY3("§7每日打卡 §8- §6IV", "PLAY3", "§617:00-19:00 §7期间在线", new Reward(100, 20, new HashMap<String, Integer>() {{
    }}).appendVitality(5)),
    SLIMEFUN1("§7Technawlgy is pawer §8- §6I", "SLIMEFUN1", "制造任意一种太阳能发电机", new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf3", 1);
    }}).appendVitality(15)),
    SLIMEFUN2("§7Technawlgy is pawer §8- §6II", "SLIMEFUN2", "制造任意一种蓄电池", new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf13", 4);
    }}).appendVitality(15)),
    SLIMEFUN3("§7Magika is pawer §8- §6I", "SLIMEFUN3", "制造 §9末影之尘 §8- §2III §7x3", new Reward(80, 0, new HashMap<String, Integer>() {{
        put("sf8", 10);
    }}).appendVitality(20)),
    ADVANCEDAB1("§7天选之人 §8- §6I", "ADVANCEDAB1", "解锁任意被动天赋", new Reward(150, 0, new HashMap<String, Integer>() {{
    }}).appendVitality(20)),
    KILL1("§7天选之人 §8- §6I", "KILL1", "无伤击杀 §620 §7只僵尸", new Reward(120, 0, new HashMap<String, Integer>() {{
    }}).appendVitality(15)),
    POTION1("§7魔法师的必修课 §8- §6I", "POTION1", "酿造 §63 §7种药水", new Reward(60, 20, new HashMap<>()).appendRawItem(new HashMap<Material, Integer>() {{
        put(Material.SAND, 10);
    }}));

    String desc;
    String codename;
    String display;
    Reward reward;

    VitalityQuest(String display, String codename, String desc, Reward reward) {
        this.display = display;
        this.desc = desc;
        this.codename = codename;
        this.reward = reward;
    }

    ItemStack getIcon(boolean completed) {
        ItemStack icon = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meta = icon.getItemMeta();
        if (completed) {
            meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.setDisplayName(MessageFormat.format("§8[ §6活跃度任务§8 · {0} §8]", completed ? "§7已完成" : this.display));
        ArrayList<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ 任务目标:");
        lores.add("§7○ " + (completed ? "§m" : "") + this.desc);
        lores.add("");//;
        lores.add("§7▽ 任务奖励: ");
        lores.addAll(this.reward.getLore());
        lores.add("§7○ §6活跃度§7 x " + String.valueOf(this.reward.getVitality()));
        lores.add("");
        lores.add("§8[ ModernSky ] vitality");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }

    void bonousHandler(UUID p) {
        if (VitalityRecord.getInstance(p).getQuestProgress(this.codename) >= 100) {
            this.reward.send(p);
        } else {

        }
    }
}

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

// - COL_PROGRESSSET
//    - CODENAME
//      - Progress1 进度(int/100)
//      - Progress2 进度
//      - Progress3 进度
public class Vitality implements Listener {

    public final static String COL_USER_NAME = "user_name";
    public final static String COL_USER_UUID = "user_uuid";
    public final static String COL_PROGRESS = "progress";
    public final static String SHEET = "vitality";
    public final static String INIT_QUERY = MessageFormat.format(
            "CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_PROGRESS);

//    static Cache<UUID, VitalityRecord> cache;

    static List<Integer> quest_slot = new LinkedList<>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));

    public static void initCache() {
        VitalityCache.init();
    }

    public static void setVitality(Player p, int v) {
        VitalityRecord.getInstance(p.getUniqueId()).setVitality(v);
    }

    public static int getVitality(Player p) {
        return VitalityRecord.getInstance(p.getUniqueId()).vitality;
    }

    public static void setProgress(Player p, String codename, int progress) {
        VitalityRecord.getInstance(p.getUniqueId()).setProgress(codename, progress);
    }

    public static int getProgress(Player p, String codename) {
        return VitalityRecord.getInstance(p.getUniqueId()).getProgress(codename);
    }

    public static void openvi(Player p) {
        final ChestMenu menu = new ChestMenu("  日常-活跃度");
        Icons.addBaseIcon(menu, "back", 43);
        menu.addItem(37, VitalityRecord.getInstance(p.getUniqueId()).getStat());
        menu.addMenuClickHandler(37, (p1, p2, p3, p4) -> false);
        int index = 0;
        for (VitalityQuest vq : VitalityQuest.values()) {
            boolean completed = VitalityRecord.getInstance(p.getUniqueId()).getProgress(vq.codename) >= 100;
            menu.addItem(quest_slot.get(index), vq.getIcon(completed));
            menu.addMenuClickHandler(quest_slot.get(index), (p1, p2, p3, p4) -> false);
            index++;
        }
        menu.open(p);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (getProgress(p, VitalityQuest.LOGIN1.codename) < 100) {
            setProgress(p, VitalityQuest.LOGIN1.codename, 100);
            p.sendMessage(VitalityQuest.valueOf("LOGIN1").reward.toString());
            p.sendMessage(Core.getPrefix() + MessageFormat.format("日常活跃度任务§8[ §6{0} §8]§7奖励已发放", VitalityQuest.LOGIN1.desc));
        }
    }
}

class VitalityRecord {
    private HashMap<String, HashMap<String, Integer>> progress;
    private UUID p;
    int vitality;

    private VitalityRecord(UUID p, HashMap<String, HashMap<String, Integer>> progress, int vitality) {
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
//        return Vitality.cache.containsKey(p) ? Vitality.cache.get(p) : new VitalityRecord(p, getProgressRecord(p),0);
        return new VitalityRecord(p, getProgressRecord(p), getVitalityValue(p));
    }

    int getProgress(String quest_codename) {
        return this.progress.getOrDefault(quest_codename, new HashMap<>()).getOrDefault("pro1", 0);
    }

    void setProgress(String quest_codename, int progress) {
        this.progress.put(quest_codename, new HashMap<String, Integer>() {{
            put("pro1", progress);
        }});
        this.save();
//        refreshCache();
    }

    void setVitality(int v) {
        this.vitality = v;
        this.save();
    }

//    public void refreshCache() {
//        Vitality.cache.put(this.p, getInstance(p));
//    }

    private void save() {
        String sql;
        if (!SqlUtil.ifExist(this.p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'',''{5}'',''{6}'');";
            SqlUtil.update(MessageFormat.format(sql,
                    Vitality.SHEET,
                    Vitality.COL_USER_NAME,
                    Vitality.COL_USER_UUID,
                    Vitality.COL_PROGRESS,
                    Bukkit.getPlayer(p).getPlayerListName(),
                    p,
                    new Gson().toJson(this),
                    this.vitality));
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
            VitalityRecord vr = new Gson().fromJson(rs.getString(1), new TypeToken<VitalityRecord>() {
            }.getType());
            return vr.vitality;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static HashMap<String, HashMap<String, Integer>> getProgressRecord(UUID p) {
        if (!SqlUtil.ifExist(p, Vitality.SHEET, Vitality.COL_USER_UUID)) {
            return new HashMap<>();
        }
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, Vitality.COL_PROGRESS, Vitality.SHEET, Vitality.COL_USER_UUID, p));
        assert rs != null;
        try {
            VitalityRecord vr = new Gson().fromJson(rs.getString(1), new TypeToken<VitalityRecord>() {
            }.getType());
            return vr.progress;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}

class VitalityCache {
    static void init() {
        CacheManager cacheManager = Core.getCacheManager();
//        Vitality.cache = cacheManager.createCache("vitality_cache",
//                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, VitalityRecord.class, ResourcePoolsBuilder.heap(10)));
    }
}

enum VitalityQuest {
    LOGIN1("§7每日打卡 §8- §6I", "LOGIN1", "登入游戏", 5, new Reward(50, 0, new HashMap<String, Integer>() {{
    }})),
    PLAY1("§7每日打卡 §8- §6II", "PLAY1", "在线 §61 §7小时", 10, new Reward(100, 20, new HashMap<String, Integer>() {{
    }})),
    PLAY2("§7每日打卡 §8- §6III", "PLAY2", "§611:00-13:00 §7期间在线", 5, new Reward(50, 20, new HashMap<String, Integer>() {{
    }})),
    PLAY3("§7每日打卡 §8- §6IV", "PLAY3", "§617:00-19:00 §7期间在线", 5, new Reward(100, 20, new HashMap<String, Integer>() {{
    }})),
    SLIMEFUN1("§7Technawlgy is pawer §8- §6I", "SLIMEFUN1", "制造任意一种太阳能发电机", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf3", 1);
    }})),
    SLIMEFUN2("§7Technawlgy is pawer §8- §6II", "SLIMEFUN2", "制造任意一种蓄电池", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
        put("sf13", 4);
    }})),
    SLIMEFUN3("§7Magika is pawer §8- §6I", "SLIMEFUN3", "制造 §9末影之尘 §8- §2III §7x3", 20, new Reward(80, 0, new HashMap<String, Integer>() {{
        put("sf8", 10);
    }})),
    ADVANCEDAB1("§7天选之人 §8- §6I", "ADVANCEDAB1", "解锁任意被动天赋", 20, new Reward(150, 0, new HashMap<String, Integer>() {{
    }})),
    KILL1("§7天选之人 §8- §6I", "无伤击杀 §620 §7只僵尸", "KILL1", 15, new Reward(120, 0, new HashMap<String, Integer>() {{
    }})),
    POTION1("§7魔法师的必修课 §8- §6I", "POTION1", "酿造 §63 §7种药水", 15, new Reward(60, 20, new HashMap<>()).appendRawItem(new HashMap<Material, Integer>() {{
        put(Material.SAND, 10);
    }}));

    String desc;
    String codename;
    String display;
    int vitality;
    Reward reward;

    VitalityQuest(String display, String codename, String desc, int vitality, Reward reward) {
        this.display = display;
        this.desc = desc;
        this.codename = codename;
        this.vitality = vitality;
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
        lores.add("§7○ §6活跃度§7 x " + String.valueOf(this.vitality));
        lores.add("");
        lores.add("§8[ ModernSky ] vitality");
        meta.setLore(lores);
        icon.setItemMeta(meta);
        return icon;
    }
}
//
//class VitalityListener implements Listener {
//
//
//}

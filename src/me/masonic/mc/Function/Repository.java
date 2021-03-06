package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Objects.Icons;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
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

public class Repository {

    public final static String COL_USER_NAME = "user_name";
    public final static String COL_USER_UUID = "user_uuid";
    public final static String COL_ITEMS = "items";
    public final static String SHEET = "repository";
    public final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_ITEMS);
    static Cache<UUID, RepositoryCache> cache;
    private HashMap<String, Integer> items;
    private UUID player;

    private final int[] stock = new int[]{0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33, 36, 37, 38, 39, 40, 41, 42};

    private Repository(UUID p, HashMap<String, Integer> items) {
        this.items = items;
        this.player = p;
    }


    private HashMap<String, HashMap<String, Integer>> getCache() {
        return cache.containsKey(this.player) ? cache.get(this.player).classified_stock : classifier(this.player);
    }

    public static void initCache() {
        RepositoryCache.init();
    }

    public static void openRp(Player p) {

        final ChestMenu menu = new ChestMenu("    后勤仓库");
        Icons.addPipe(menu, new int[]{7, 16, 25, 34, 43, 52, 45, 46, 47, 49, 50, 51});
        Icons.addBaseIcon(menu, "back", 48);

        RepositoryCategory.addCateIcon(menu);

        menu.open(p);
    }

    private static HashMap<String, ArrayList<String>> TYPE_MAP = new HashMap<>();

    public static ArrayList<String> getStorableMap() {
        return STORABLE_MAP;
    }

    private static ArrayList<String> STORABLE_MAP = new ArrayList<>();

    static {
        TYPE_MAP.put("SLIMEFUN", new ArrayList<String>() {
            {
                add("sf1");
                add("sf2");
            }
        });
        for (ArrayList<String> s : TYPE_MAP.values()) {
            STORABLE_MAP.addAll(s);
        }
    }

    public static Repository getInstance(Player p) {
        return new Repository(p.getUniqueId(), getRawItems(p.getUniqueId()));
    }

    public static Repository getInstance(UUID p) {
        return new Repository(p, getRawItems(p));
    }

    private void save() {
        String sql;
        if (!SqlUtil.ifExist(this.player, SHEET, COL_USER_UUID)) {
            sql = "INSERT INTO {0}(`{1}`, `{2}`, `{3}`) VALUES(''{4}'',''{5}'',''{6}'');";
            SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_NAME, COL_USER_UUID, COL_ITEMS, Bukkit.getPlayer(this.player).getPlayerListName(), this.player.toString(), new Gson().toJson(this.items)));
            return;
        }
        sql = "UPDATE {0} SET `{1}` = ''{2}'' WHERE `{3}` = ''{4}'';";
        SqlUtil.update(MessageFormat.format(sql, SHEET, COL_ITEMS, new Gson().toJson(this.items), COL_USER_UUID, this.player.toString()));
    }

    public void saveItem(HashMap<String, Integer> add) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();
        for (String iname : add.keySet()) {
//            System.out.println(this.items.getOrDefault(iname, 0));
            Bukkit.getPlayer(this.player).sendMessage(Core.getPrefix() + MessageFormat.format("物资 §8[ {0} §7x §6{1} §8] §7已发放至后勤仓库 ", mapi.getGameManagerAPI().getItemManagerAPI().getItem(iname).getItemMeta().getDisplayName(), add.get(iname)));
            // this.items.get(iname)与add.get(iname)相加时会报错
            // Caused by: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
            // 原因尚不明确
            // 原因: http://www.cnblogs.com/zhanjindong/p/3803114.html
            this.items.put(iname, add.get(iname) + Integer.parseInt(String.valueOf(this.items.getOrDefault(iname, 0))));

        }
        refreshCache();
        this.save();
    }

    public boolean removeItem(String item, int amount) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();

        if (!this.items.containsKey(item)) {
            return false;
        }
        int stock = Integer.parseInt(String.valueOf(this.items.get(item)));
        if (stock < amount) {
            return false;
        }
        Bukkit.getPlayer(this.player).sendMessage(Core.getPrefix() + MessageFormat.format("后勤仓库中的物资 §8[ {0} §7x §6{1} §8] §7已被取出使用", mapi.getGameManagerAPI().getItemManagerAPI().getItem(item).getItemMeta().getDisplayName(), amount));
        this.items.put(item, stock - amount);
        this.save();
        refreshCache();
        return true;
    }

    public boolean removeItem(HashMap<String, Integer> remove) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();
        for (String iname : remove.keySet()) {
//            System.out.println(this.items.getOrDefault(iname, 0));
            // this.items.get(iname)与add.get(iname)相加时会报错
            // Caused by: java.lang.ClassCastException: java.lang.String cannot be cast to java.lang.Integer
            // 原因尚不明确
            if (!this.items.containsKey(iname) || this.items.get(iname) < remove.get(iname)) {
                return false;
            }
            Bukkit.getPlayer(this.player).sendMessage(Core.getPrefix() + MessageFormat.format("后勤仓库中的物资 §8[ {0} §7x §6{1} §8] §7已被取出使用", mapi.getGameManagerAPI().getItemManagerAPI().getItem(iname).getItemMeta().getDisplayName(), remove.get(iname)));

            this.items.compute(iname, (k, v) -> v - remove.get(iname));
        }
        refreshCache();
        this.save();
        return true;
    }

    private static HashMap<String, Integer> getRawItems(UUID p) {
        if (!SqlUtil.ifExist(p, SHEET, COL_USER_UUID)) {
            return new HashMap<>();
        }
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, COL_ITEMS, SHEET, COL_USER_UUID, p.toString()));
        assert rs != null;
        try {
            return new Gson().fromJson(rs.getString(1), new TypeToken<HashMap<String, String>>() {
            }.getType());
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    void setStock(ChestMenu menu, String type) {
        HashMap<String, HashMap<String, Integer>> classified_map = getCache();
        refreshCache();
        MyItemsAPI mapi = MyItemsAPI.getInstance();

        int slot = 0;
        if ((!classified_map.containsKey(type)) || classified_map.get(type).size() == 0) {
            for (int i : stock) {
                menu.replaceExistingItem(i, new ItemStack(Material.AIR));
            }
            return;
        }
        for (String iname : classified_map.get(type).keySet()) {
            ItemStack base = mapi.getGameManagerAPI().getItemManagerAPI().getItem(iname).clone();
            ItemMeta meta = base.getItemMeta();
            List<String> lores = meta.getLore();

            if (lores != null) {
                lores.set(lores.size() - 1, "§8>>>>>>>>>>>>>");
            } else {
                lores = new ArrayList<>();
            }

            lores.add("");
            lores.add(MessageFormat.format("§7◇ 仓库目前存有此类物品 §6{0} §7个", classified_map.get("SLIMEFUN").get(iname)));
            lores.add("");
            lores.add("§8[ ModernSky ] Repository");
            meta.setLore(lores);

            base.setItemMeta(meta);

            menu.replaceExistingItem(slot, base);
            menu.addMenuClickHandler(slot, (player, i, itemStack, clickAction) -> false);
            slot++;
            if (slot % 7 == 0) {
                slot += 2;
                if (slot >= 35) {
                    break;
                }
            }

        }
    }

    private HashMap<String, HashMap<String, Integer>> classifier(UUID p) {
        HashMap<String, Integer> src = getInstance(p).items;
        HashMap<String, HashMap<String, Integer>> output = new HashMap<>();
        for (String Tk : TYPE_MAP.keySet()) {
            HashMap<String, Integer> temp = new HashMap<>();
            // HashMap在遍历时不可删除对象，使用Iterator即可
            Iterator<String> it = src.keySet().iterator();
            while (it.hasNext()) {
                String Sk = it.next();
                if (TYPE_MAP.get(Tk).contains(Sk)) {
                    temp.put(Sk, src.get(Sk));
                    it.remove();
                }
            }
            output.put(Tk, temp);
        }
        return output;
    }

    public void refreshCache() {
        cache.put(this.player, new RepositoryCache(classifier(this.player)));
    }


    public boolean containsAtLeast(String item, int amount) {
        HashMap<String, Integer> src = items;
        // http://ask.csdn.net/questions/168262
        // Integer类型对象与int类型对象直接比较会出错
        //        return src.containsKey(item) && src.get(item) >= amount;

        return src.containsKey(item) && Integer.parseInt(String.valueOf(src.get(item))) >= amount;
    }

}


class RepositoryCache {
    HashMap<String, HashMap<String, Integer>> classified_stock;

    RepositoryCache(HashMap<String, HashMap<String, Integer>> classified_stock) {
        this.classified_stock = classified_stock;
    }

    static void init() {
        CacheManager cacheManager = Core.getCacheManager();
        Repository.cache = cacheManager.createCache("repository_cache",
                CacheConfigurationBuilder.newCacheConfigurationBuilder(UUID.class, RepositoryCache.class, ResourcePoolsBuilder.heap(10)));
    }
}

enum RepositoryCategory {
    SLIMEFUN(Material.SLIME_BALL, "§8[ §6粘液科技 §8]", "SLIMEFUN", 8),
    MATERIAL(Material.DIAMOND, "§8[ §6材料 §8]", "MATERIAL", 17),
    CONSUMABLE(Material.TIPPED_ARROW, "§8[ §6消耗品 §8]", "CONSUMABLE", 26);

    private static HashMap<RepositoryCategory, ItemStack> en_items = new HashMap<>();
    private static HashMap<RepositoryCategory, ItemStack> non_en_items = new HashMap<>();

    static {
        for (RepositoryCategory r : RepositoryCategory.values()) {
            ItemStack i = new ItemStack(r.m);
            ItemMeta meta = i.getItemMeta();
            meta.setDisplayName(r.name);
            i.setItemMeta(meta);
            non_en_items.put(r, i);
            ItemStack ei = i.clone();
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ei.setItemMeta(meta);
            en_items.put(r, ei);
        }
    }

    Material m;
    String name;
    String codename;
    int slot;

    RepositoryCategory(Material m, String name, String codename, int slot) {
        this.m = m;
        this.name = name;
        this.codename = codename;
        this.slot = slot;
    }

    public static void addCateIcon(ChestMenu menu) {
        for (RepositoryCategory r : RepositoryCategory.values()) {
            menu.addItem(r.slot, non_en_items.get(r));
            menu.addMenuClickHandler(r.slot, (p0, inv, item, action) -> {
                for (RepositoryCategory r0 : non_en_items.keySet()) {
                    menu.replaceExistingItem(r0.slot, r0.slot == r.slot ? en_items.get(r0) : non_en_items.get(r0));
                }
                Repository.getInstance(p0).setStock(menu, r.codename);
                return false;
            });
        }
    }
}
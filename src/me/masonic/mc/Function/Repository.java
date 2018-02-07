package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Objects.Icons;
import me.masonic.mc.Utility.SqlUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

public class Repository {

    private final static String COL_USER_NAME = "uesr_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_ITEMS = "items";
    private final static String SHEET = "repository";
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_ITEMS);

    HashMap<String, Integer> items;
    UUID player;

    public Repository(UUID p, HashMap<String, Integer> items) {
        this.items = items;
        this.player = p;
    }

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getSheetName() {
        return SHEET;
    }

    public static String getColItems() {
        return COL_ITEMS;
    }

    public static String getInitQuery() {
        return INIT_QUERY;
    }

    public static void openRp(Player p) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();

        final ChestMenu menu = new ChestMenu("    后勤仓库");
        Icons.addPipe(menu, new int[]{7, 16, 25, 34, 43, 52, 45, 46, 47, 49, 50, 51});
        Icons.addBaseIcon(menu, "back", 48);

        Repository rp = getInstance(p);
        int slot = 0;
        for (String iname : rp.items.keySet()) {
            ItemStack base = mapi.getGameManagerAPI().getItemManagerAPI().getItem(iname).clone();
            ItemMeta meta = base.getItemMeta();
            List<String> lores = meta.getLore();
            if (lores != null) {
                lores.set(lores.size() - 1, "§8>>>>>>>>>>>>>");
            } else {
                lores = new ArrayList<>();
            }
            lores.add("");
            lores.add(MessageFormat.format("§7◇ 仓库目前存有此类物品 §6{0} §7个", rp.items.get(iname)));
            lores.add("");
            lores.add("§8[ ModernSky ] Repository");
            meta.setLore(lores);

            base.setItemMeta(meta);

            menu.addItem(slot, base);
            menu.addMenuClickHandler(slot, (player, i, itemStack, clickAction) -> false);

            slot++;
            if (slot % 7 == 0) {
                slot += 2;
                if (slot >= 35) {
                    break;
                }
            }
        }

        menu.open(p);
    }

    public static Repository getInstance(Player p) {
        return new Repository(p.getUniqueId(), getRawItems(p));
    }

    public void save() {
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
        for (String iname : add.keySet()) {
            System.out.println(this.items.getOrDefault(iname, 0));
//            Integer m = this.items.getOrDefault(iname, 0);
//            int n = m.intValue();
            this.items.put(iname, add.getOrDefault(iname, 0));
        }
        this.save();

    }

    static HashMap<String, Integer> getRawItems(Player p) {
        if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
            return new HashMap<>();
        }

        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'';";
        ResultSet rs = SqlUtil.getResults(MessageFormat.format(sql, COL_ITEMS, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
        assert rs != null;
        try {
            return new Gson().fromJson(rs.getString(1), new TypeToken<HashMap<String, String>>() {
            }.getType());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

}

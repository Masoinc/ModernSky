package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Reward.Reward;
import me.masonic.mc.Utility.PermissionUtil;
import me.masonic.mc.Utility.SqlUtil;
import me.masonic.mc.Utility.TimeUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

public class Sign {
    private final static String COL_USER_NAME = "user_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_SIGN = "sign_record";
    private final static String COL_SIGN_KITS = "sign_kits";
    private final static String SHEET = "sign";
    private final static String INIT_QUERY = MessageFormat.format("CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL, `{4}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8",
            SHEET, COL_USER_NAME, COL_USER_UUID, COL_SIGN, COL_SIGN_KITS);

    public static String getInitQuery() {
        return INIT_QUERY;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getSheetName() {
        return SHEET;
    }

    private static HashMap<Integer, Integer> KIT_SLOT = new HashMap<>();

    static {
        KIT_SLOT.put(45, 3);
        KIT_SLOT.put(46, 7);
        KIT_SLOT.put(47, 15);
        KIT_SLOT.put(48, 21);
        KIT_SLOT.put(49, 28);
    }

    private static ItemStack getKitIcon(int days, Boolean signed) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();
        ItemStack base = mapi.getGameManagerAPI().getItemManagerAPI().getItem("head_chest").clone();
        ItemMeta meta = base.getItemMeta();
        meta.setDisplayName(signed ? "§8[ §7连续签到 §6" + String.valueOf(days) + " §7天奖励·已领取 §8]" : "§8[ §7连续签到 §6" + String.valueOf(days) + " §7天奖励 §8]");

        List<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ 可领取的奖励:");
        lores.addAll(SignReward.getSignKitReward(days).getLore());
        lores.add("");
        lores.add("§8[ ModernSky §8]");

        meta.setLore(lores);


        meta.setLore(lores);
        base.setItemMeta(meta);
        return base;
    }

    private static ItemStack getSignStatIcon(String sum) {
        ItemStack stat = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = stat.getItemMeta();
        meta.setDisplayName("§8[ §6签到统计 §8]");
        ArrayList<String> lores = new ArrayList<>(4);
        lores.add("");
        lores.add("§7◇ 本月已累计签到 §3" + sum + " §7天");
        lores.add("");
        lores.add("§8[ModernSky] reward");
        meta.setLore(lores);
        stat.setItemMeta(meta);
        return stat;
    }

    /**
     * 渲染签到图标
     *
     * @param date   日期
     * @param signed 是否已签到
     * @param today  是否是今日
     */
    private static ItemStack getSignIcon(int date, Boolean signed, Boolean today, List<String> addition_lores) {
        ItemStack icon = new ItemStack(signed ? Material.MAP : Material.EMPTY_MAP);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§8[ §6" + date + " §7日" + (today ? (signed ? "·已签到" : "·点击签到") : (signed ? "·已领取" : "")) + " §8]");
        List<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ " + (today ? "今日" : "签到") + "奖励:");
        if (SignReward.getSignReward(date).getLore() != null) {
            lores.addAll(SignReward.getSignReward(date).getLore());
        }
        if (!addition_lores.isEmpty()) {
            lores.addAll(addition_lores);
        } else {
            lores.add("");
        }
        lores.add("§8[ ModernSky ] reward");
        meta.setLore(lores);
        if (today) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        icon.setItemMeta(meta);
        return icon;
    }


    public static void openSignMenu(Player p) {
        final ChestMenu menu = new ChestMenu("   签到 §8[ §6" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "月 §8]");
        int current_date = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        menu.addMenuOpeningHandler(p1 -> p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));

        Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);

        ArrayList<String> sign_static = exist ? getSignResult(p) : new ArrayList<>();

        Boolean today_signed = sign_static.contains(String.valueOf(current_date));

        List<String> lores = new ArrayList<>();
        if (Exploration.getExplorationRank(p).getSign_additional_money() != 0) {
            lores.add("");
            lores.add("§7▽ " + Exploration.getExplorationRank(p).getTagname() + " §7额外签到奖励:");
            lores.add("§7○ §3黑币§7 x §6" + Exploration.getExplorationRank(p).getSign_additional_money());
            lores.add("");
        }

        // 框架类
        {
            // 往日签到
            for (int i$ = 1; i$ < TimeUtil.getDayOfMonth(); i$++) {
                if (i$ == current_date) {
                    continue;
                }
                menu.addItem(i$ - 1, getSignIcon(i$, exist && sign_static.contains(String.valueOf(i$)), false, lores));
                menu.addMenuClickHandler(i$ - 1, (arg0, arg1, arg2, arg3) -> false);
            }
            // 统计信息
            menu.addItem(53, getSignStatIcon(String.valueOf(sign_static.size())));
            menu.addMenuClickHandler(53, (arg0, arg1, arg2, arg3) -> false);
            // 分割线
            int[] PIPE = new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44};
            for (int i$ : PIPE) {
                menu.addItem(i$, new CustomItem(Material.STAINED_GLASS_PANE, "", 15, 1, new ArrayList<>()));
                menu.addMenuClickHandler(i$, (arg0, arg1, arg2, arg3) -> false);
            }

            // 返回
            menu.addItem(40, new CustomItem(Material.REDSTONE, "§8[ §c返回 §8]", 0, 1, new ArrayList<>()));
            menu.addMenuClickHandler(40, (p15, arg1, arg2, arg3) -> {
                PermissionUtil.runOp(p15, "bs mskycore");
                return false;
            });
        }

        if (exist) {
            // 月度清理数据
            if (current_date == 1) {
                clearData(p);
                return;
            }
            for (String d : sign_static) {
                if (Integer.valueOf(d) > current_date) {
                    clearData(p);
                    return;
                }
            }

            if (today_signed) {
                menu.addItem(current_date - 1, getSignIcon(current_date, true, true, new ArrayList<>()));
                menu.addMenuClickHandler(current_date - 1, (pl2, i, itemStack, clickAction) -> {
                    pl2.sendMessage(Core.getPrefix() + "今天已签到过了哦，明天再来吧");
                    return false;
                });
            } else {
                menu.addItem(current_date - 1, getSignIcon(current_date, false, true, lores));
                menu.addMenuClickHandler(current_date - 1, (p13, i, itemStack, clickAction) -> {
                    ArrayList<String> sign_dynamic = getSignResult(p);
                    sign_dynamic.add(Integer.toString(current_date));
                    String json = new Gson().toJson(sign_dynamic);

                    SqlUtil.update(MessageFormat.format("UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}'';", SHEET, COL_SIGN, json, COL_USER_UUID, p.getUniqueId().toString()));

                    // 给予奖励
                    p13.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                    SignReward.sendReward(p13, current_date);
                    menu.replaceExistingItem(53, getSignStatIcon(String.valueOf(sign_dynamic.size())));
                    menu.replaceExistingItem(current_date - 1, getSignIcon(current_date, true, true, new ArrayList<>()));
                    menu.addMenuClickHandler(current_date - 1, (player, i12, itemStack12, clickAction12) -> false);
                    return false;
                });
            }
        } else {
            menu.addItem(current_date - 1, getSignIcon(current_date, false, true, new ArrayList<>()));
            menu.addMenuClickHandler(current_date - 1, (p14, i, itemStack, clickAction) -> {
                ArrayList<String> sign_init = new ArrayList<>(Collections.singletonList(Integer.toString(current_date)));
                String json = new Gson().toJson(sign_init);

                // 写入记录
                String sql = "INSERT INTO {0}(`{1}`,`{2}`,`{3}`,`{4}`) VALUES(''{5}'',''{6}'',''{7}'',''{8}'');";
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_NAME, COL_USER_UUID, COL_SIGN, COL_SIGN_KITS, p14.getPlayerListName(), p14.getUniqueId().toString(), json, "[]"));

                // 给予奖励
                p14.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                SignReward.sendReward(p14, current_date);
                menu.replaceExistingItem(53, getSignStatIcon("1"));
                menu.replaceExistingItem(current_date - 1, getSignIcon(current_date, true, true, lores));
                menu.addMenuClickHandler(current_date - 1, (player, i1, itemStack1, clickAction1) -> false);
                return false;
            });
        }

//         累签奖励
        {

            for (int slot : KIT_SLOT.keySet()) {
                ArrayList<String> kit_static = getSignKitResult(p);
                menu.addItem(slot, getKitIcon(KIT_SLOT.get(slot), kit_static.contains(String.valueOf(KIT_SLOT.get(slot)))));

                menu.addMenuClickHandler(slot, (clicler, slot_dynamic, arg2, arg3) -> {
                    int sign_dynamic_sum = getSignResult(p).size();
                    ArrayList<String> kit_dynamic = getSignKitResult(p);
                    if (!exist || sign_dynamic_sum < KIT_SLOT.get(slot_dynamic)) {
                        p.sendMessage(Core.getPrefix() + "签到天数不足哦");
                        return false;
                    }

                    if (kit_dynamic.contains(String.valueOf(KIT_SLOT.get(slot_dynamic)))) {
                        p.sendMessage(Core.getPrefix() + "本月已领取");
                        return false;
                    }

                    // 发放奖励
                    SignReward.getSignKitReward(KIT_SLOT.get(slot_dynamic)).send(p.getUniqueId());
                    p.sendMessage(Core.getPrefix() + "累签奖励已发放~");

                    String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";
                    kit_dynamic.add(String.valueOf(KIT_SLOT.get(slot_dynamic)));
                    SqlUtil.update(MessageFormat.format(sql, SHEET, COL_SIGN_KITS, new Gson().toJson(kit_dynamic), COL_USER_UUID, p.getUniqueId().toString()));

                    return false;
                });
            }
        }
        menu.open(p);
    }


    private static ArrayList<String> getSignResult(Player p) {
        try {
            if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
                return new ArrayList<>();
            }
            ResultSet sign = SqlUtil.getResults(MessageFormat.format("SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;", COL_SIGN, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
            return new Gson().fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
            }.getType());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (p.getPlayerListName().equalsIgnoreCase("Masonic")) {
            p.sendMessage("Crushed");
        }
        return new ArrayList<>();
    }

    /**
     * 获取累签奖励记录
     *
     * @param p 玩家
     * @return 如果无记录则返回空ArrayList，否则返回记录
     */
    private static ArrayList<String> getSignKitResult(Player p) {
        if (!SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID)) {
            return new ArrayList<>();
        }
        ResultSet sign = SqlUtil.getResults(MessageFormat.format("SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;", COL_SIGN_KITS, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
        try {
            assert sign != null;
            return new Gson().fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
            }.getType());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (p.getPlayerListName().equalsIgnoreCase("Masonic")) {
            p.sendMessage("Crushed");
        }
        return new ArrayList<>();
    }

    private static void clearData(Player p) {
        String sql = "DELETE from {0} WHERE {1} = ''{2}'';";
        SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
        p.closeInventory();
        openSignMenu(p);
    }
}

class SignReward extends Reward {
    SignReward(int money, int point, HashMap<String, Integer> items) {
        super(money, point, items);
    }


    static void sendReward(Player p, int day) {
        Reward reward = getSignReward(day);
        if (reward == null) {
            return;
        }
        if (reward.getMoney() != 0) {
            Core.getEconomy().depositPlayer(p, reward.getMoney());
        }

        if (reward.getPoint() != 0) {
            Core.getPlayerPoints().getAPI().give(p.getUniqueId(), reward.getPoint());
        }

        for (String item : reward.getItems().keySet()) {
            if (Repository.getStorableMap().contains(item)) {
                Repository.getInstance(p).saveItem(new HashMap<String, Integer>() {{
                    put(item, reward.getItems().get(item));
                }});
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "myitems:mi load custom " + item + " " + p.getPlayerListName() + " " + reward.getItems().get(item));
            }
        }
        if (Exploration.getExplorationRank(p).getSign_additional_money() != 0) {
            Core.getEconomy().depositPlayer(p, Exploration.getExplorationRank(p).getSign_additional_money());
        }
//        if (!Package.isExpired(p,"A")) {
//            Exploration.setExploreValue(p, Exploration.getExploreValue(p) + 10);
//            p.sendMessage(Core.getPrefix() + "签到获得了 §610 §7点探索值");
//        }
    }


    static Reward getSignKitReward(int days) {
        HashMap<String, Integer> item = new HashMap<>();
        switch (days) {
            case 3:
                item.put("sf1", 1);
                return new Reward(1500, 0, item);
            case 7:
                return new Reward(0, 160, item);
            case 15:
                item.put("sf2", 1);
                return new Reward(1500, 0, item);
            case 21:
                return new Reward(0, 220, item);
            case 28:
                item.put("sf2", 1);
                return new Reward(0, 260, item);
            default:
                return new Reward(0, 0, item);
        }
    }

    static Reward getSignReward(int day) {
        HashMap<String, Integer> item = new HashMap<>();
        List<Integer> money = Arrays.asList(2, 8, 15, 22, 29);
        List<Integer> dust = Arrays.asList(1, 7, 14, 21, 28);
        List<Integer> sf1 = Arrays.asList(3, 9, 16, 23);
        if (money.contains(day)) {
            return new Reward(1500, 0, item);
        } else if (dust.contains(day)) {
            return new Reward(0, 60, item);
        } else if (sf1.contains(day)) {
            item.put("sf1", 1);
            return new Reward(0, 0, item);
        } else {
            switch (day) {
                case 4:
                    item.put("sf3", 1);
                    return new Reward(0, 0, item);
                case 5:
                    item.put("sf8", 8);
                    return new Reward(0, 0, item);
                case 6:
                    item.put("sf11", 5);
                    return new Reward(0, 0, item);
                case 10:
                    item.put("sf4", 5);
                    return new Reward(0, 0, item);
                case 11:
                    item.put("sf9", 5);
                    return new Reward(0, 0, item);
                case 12:
                    item.put("sf10", 2);
                    return new Reward(0, 0, item);
                case 13:
                    item.put("sf12", 5);
                    return new Reward(0, 0, item);
                case 17:
                    item.put("sf5", 5);
                    return new Reward(0, 0, item);
                case 18:
                    item.put("sf8", 8);
                    return new Reward(0, 0, item);
                case 19:
                    item.put("sf11", 5);
                    return new Reward(0, 0, item);
                case 20:
                    item.put("sf12", 5);
                    return new Reward(0, 0, item);
                case 24:
                    item.put("sf6", 3);
                    return new Reward(0, 0, item);
                case 25:
                    item.put("sf9", 8);
                    return new Reward(0, 0, item);
                case 26:
                    item.put("sf10", 2);
                    return new Reward(0, 0, item);
                case 27:
                    item.put("sf12", 5);
                    return new Reward(0, 0, item);
                case 30:
                    item.put("sf2", 1);
                    return new Reward(0, 0, item);
                case 31:
                    item.put("sf7", 3);
                    return new Reward(0, 0, item);
                default:
                    return null;
            }
        }
    }
}
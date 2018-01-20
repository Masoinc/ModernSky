package me.masonic.mc.Cmd;

import api.praya.myitems.main.MyItemsAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Exploration;
import me.masonic.mc.Function.Reward;
import me.masonic.mc.Utility.PermissionUtil;
import me.masonic.mc.Utility.SqlUtil;
import me.masonic.mc.Utility.TimeUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MskySign implements CommandExecutor {

    private final static String COL_USER_NAME = Core.getInstance().getConfig().getString("SQL.sheet.sign.name");
    private final static String COL_USER_UUID = Core.getInstance().getConfig().getString("SQL.sheet.sign.uuid");
    private final static String COL_SIGN = Core.getInstance().getConfig().getString("SQL.sheet.sign.record");
    private final static String COL_SIGN_KITS = Core.getInstance().getConfig().getString("SQL.sheet.sign.kits");
    private final static String SHEET = Core.getInstance().getConfig().getString("SQL.sheet.sign.sheet");

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColSign() {
        return COL_SIGN;
    }

    public static String getColSignKits() {
        return COL_SIGN_KITS;
    }

    public static String getSheetName() {
        return SHEET;
    }

    private ItemStack getKitIcon(int days, Boolean signed) {
        MyItemsAPI mapi = MyItemsAPI.getInstance();
        ItemStack base = mapi.getGameManagerAPI().getItemManagerAPI().getItem("head_chest").clone();
        ItemMeta meta = base.getItemMeta();

        meta.setDisplayName("§8[ §7连续签到 §6" + String.valueOf(days) + " §7天奖励 §8]");

        List<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ 可领取的奖励:");
        lores.addAll(Reward.getLore(Reward.getSignKitReward(days)));
        lores.add("");
        lores.add("§8[ ModernSky §8]");

        meta.setLore(lores);
        if (signed) {
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setLore(lores);
        base.setItemMeta(meta);
        return base;
    }

    private ItemStack getSignStatIcon(String sum) {
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
    private ItemStack getSignIcon(int date, Boolean signed, Boolean today, List<String> addition_lores) {
        ItemStack icon = new ItemStack(signed ? Material.MAP : Material.EMPTY_MAP);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§8[ §6" + date + " §7日" + (today ? (signed ? "·已签到" : "·点击签到") : (signed ? "·已领取" : "")) + " §8]");
        List<String> lores = new ArrayList<>();
        lores.add("");
        lores.add("§7▽ " + (today ? "今日" : "签到") + "奖励:");
        if (Reward.getLore(Reward.getSignReward(date)) != null) {
            lores.addAll(Reward.getLore(Reward.getSignReward(date)));
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

    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        if (c instanceof Player) {
            Player p = (Player) c;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "open":
                            try {
                                openSignMenu(p);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            return true;
                    }
            }
        }
        return false;
    }

    private void openSignMenu(Player p) throws SQLException {
        final ChestMenu menu = new ChestMenu("   签到 §8[ §6" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "月 §8]");
        int current_date = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        menu.addMenuOpeningHandler(p1 -> p1.playSound(p1.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F));

        Boolean exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
        ArrayList<String> sign_static = exist ? getSignResult(p) : new ArrayList<>();

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
                menu.addItem(i$ - 1, getSignIcon(i$, exist && sign_static.contains(Integer.toString(i$)), false, lores));
                menu.addMenuClickHandler(i$ - 1, (arg0, arg1, arg2, arg3) -> false);
            }
            // 统计信息
            menu.addItem(53, getSignStatIcon(String.valueOf(sign_static.size())));
            menu.addMenuClickHandler(53, (arg0, arg1, arg2, arg3) -> false);
            // 分割线
            int[] PIPE = new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44};
            for (int i$ : PIPE) {
                menu.addItem(i$, new CustomItem(Material.STAINED_GLASS_PANE, "", 0, 1, new ArrayList<>()));
                menu.addMenuClickHandler(i$, (arg0, arg1, arg2, arg3) -> false);
            }

            // 返回
            menu.addItem(40, new CustomItem(Material.REDSTONE, "§8[ §c返回 §8]", 0, 1, new ArrayList<>()));
            menu.addMenuClickHandler(40, (p15, arg1, arg2, arg3) -> {
                PermissionUtil.runOp(p15, "bs mskycore");
                return false;
            });
        }

        // 月度清理数据
        if (exist && current_date == 1) {
            String sql = "DELETE from {0} WHERE {1} = ''{2}'';";
            SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
        }

        // 今天已签到
        if (exist && sign_static.contains(Integer.toString(current_date))) {
            menu.addItem(current_date - 1, getSignIcon(current_date, true, true, lores));
            menu.addMenuClickHandler(current_date - 1, (p12, arg1, arg2, arg3) -> {
                p12.sendMessage(Core.getPrefix() + "今天已签到过了哦，明天再来吧");
                return false;
            });
            // 今天未签到
        } else if (exist) {
            menu.addItem(current_date - 1, getSignIcon(current_date, false, true, lores));
            menu.addMenuClickHandler(current_date - 1, (p13, arg1, arg2, arg3) -> {
                // 更新记录
                ArrayList<String> sign_dynamic = getSignResult(p);
                String json = new Gson().toJson(sign_dynamic.add(Integer.toString(current_date)));
                p.sendMessage(json);
                SqlUtil.update(MessageFormat.format("UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}'';", SHEET, COL_SIGN, json, COL_USER_UUID, p.getUniqueId().toString()));

                // 给予奖励
                p13.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                Reward.sendSignReward(p13, current_date);
                menu.replaceExistingItem(current_date - 1, getSignIcon(current_date, true, true, new ArrayList<>()));
                menu.addMenuClickHandler(current_date - 1, (player, i, itemStack, clickAction) -> false);
                return false;
            });
        }

        if (!exist) {
            menu.addItem(current_date - 1, getSignIcon(current_date, false, true, new ArrayList<>()));
            menu.addMenuClickHandler(current_date - 1, (p14, arg1, arg2, arg3) -> {
                ArrayList<String> sign_init = new ArrayList<>(Collections.singletonList(Integer.toString(current_date)));
                String json = new Gson().toJson(sign_init);

                // 写入记录
                String sql = "INSERT INTO {0}(`{1}`,`{2}`,`{3}`,`{4}`) VALUES(''{5}'',''{6}'',''{7}'',''{8}'');";
                SqlUtil.update(MessageFormat.format(sql, SHEET, COL_USER_NAME, COL_USER_UUID, COL_SIGN, COL_SIGN_KITS, p14.getPlayerListName(), p14.getUniqueId().toString(), json, "[]"));

                // 给予奖励
                p14.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                Reward.sendSignReward(p14, current_date);
                menu.replaceExistingItem(current_date - 1, getSignIcon(current_date, true, true, lores));
                menu.addMenuClickHandler(current_date - 1, (player, i, itemStack, clickAction) -> false);
                return false;
            });
        }


////         累签奖励
//        {
//            List<Integer> slots = new ArrayList<>(Arrays.asList(45, 46, 47, 48, 49));
//            int[] day_of_kit = new int[]{3, 7, 15, 21, 28};
//
//            assert kit_result != null;
//
//            for (int i = 0; i < slots.size(); i++) {
//                ArrayList<String> kit_result = getSignKitResult(p);
//                menu.addItem(slots.get(i), getKitIcon(day_of_kit[i], kit_result.contains(String.valueOf(day_of_kit[i]))));
////                menu.addItem(slots.get(i), getKitIcon(day_of_kit[i], false));
//                int finalI = i;
//
//                menu.addMenuClickHandler(slots.get(i), (arg0, arg1, arg2, arg3) -> {
//                    sign_record = getSignResult(p);
//                    kit_result = getSignKitResult(p);
//
//                    if (kit_result.contains(String.valueOf(day_of_kit[finalI]))) {
//                        p.sendMessage(Core.getPrefix() + "本月已领取");
//                        return false;
//                    }
//
//                    if (!exist || sign_result.size() < 3) {
//                        p.sendMessage(Core.getPrefix() + "签到天数不足哦");
//                        return false;
//                    }
//
//                    // 发放奖励
//                    Reward.sendReward(p, Reward.getSignKitReward(day_of_kit[finalI]));
//                    p.sendMessage(Core.getPrefix() + "累签奖励已发放~");
//
//                    String sql = "UPDATE {0} SET {1} = ''{2}'' WHERE {3} = ''{4}''";
//
//                    kit_result.add(String.valueOf(day_of_kit[finalI]));
//
//                    Statement stmt = null;
//                    try {
//                        stmt = Core.getConnection().createStatement();
//                        stmt.execute(MessageFormat.format(sql, SHEET, COL_SIGN_KITS, new Gson().toJson(kit_result), COL_USER_UUID, p.getUniqueId().toString()));
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//
//                    return false;
//                });
//            }
//
//        }
        menu.open(p);
    }


    private static ArrayList<String> getSignResult(Player p) {
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
        try {
            ResultSet sign = SqlUtil.getResults(MessageFormat.format(sql, COL_SIGN, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
            if (sign == null) {
                return new ArrayList<>();
            }
            if (sign.next()) {
                return new Gson().fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
                }.getType());
            }

        } catch (SQLException e) {
            e.printStackTrace();
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
        String sql = "SELECT {0} FROM {1} WHERE {2} = ''{3}'' LIMIT 1;";
        try {
            ResultSet sign = SqlUtil.getResults(MessageFormat.format(sql, COL_SIGN_KITS, SHEET, COL_USER_UUID, p.getUniqueId().toString()));
            if (sign == null) {
                return new ArrayList<String>();
            }
            if (sign.next()) {
                return new Gson().fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
                }.getType());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();

    }
}

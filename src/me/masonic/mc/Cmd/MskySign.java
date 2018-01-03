package me.masonic.mc.Cmd;

import api.praya.myitems.main.MyItemsAPI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.masonic.mc.Core;

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
import org.bukkit.material.MaterialData;

import java.sql.*;
import java.util.*;

public class MskySign implements CommandExecutor {
    private static final int[] PIPE = new int[]{36, 37, 38, 39, 40, 41, 42, 43, 44};
    private static HashMap<Integer, ItemStack> REWARD = new HashMap<>();

    private final static String COL_USER_NAME = "user_name";
    private final static String COL_USER_UUID = "user_uuid";
    private final static String COL_SIGN = "sign_record";
    private final static String SHEET = "sign";
    private static int SUM_OF_SIGN = 0;

    public static String getColUserName() {
        return COL_USER_NAME;
    }

    public static String getColUserUuid() {
        return COL_USER_UUID;
    }

    public static String getColSign() {
        return COL_SIGN;
    }

    public static String getSheetName() {
        return SHEET;
    }

    private void initReward() {
        MyItemsAPI mapi = MyItemsAPI.getInstance();
        ItemStack bp_a = mapi.getGameManagerAPI().getItemManagerAPI().getItem("sf1");
        ItemStack bp_b = mapi.getGameManagerAPI().getItemManagerAPI().getItem("sf2");
        for (int i = 1; i <= 31; i++) {
            REWARD.put(i, bp_a);
        }

    }

    /**
     * 渲染签到图标
     *
     * @param date   日期
     * @param signed 是否已签到
     * @param today  是否是今日
     */
    private ItemStack getIcon(int date, Boolean signed, Boolean today) {
        ItemStack icon = new ItemStack(signed ? Material.MAP : Material.EMPTY_MAP);
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName("§8[ §6" + date + " §7日" + (today ? (signed ? "·已签到" : "·点击签到") : (signed ? "·已领取" : "")) + " §8]");
        List<String> lores = Arrays.asList(
                "",
                "§7▽ " + (today ? "今日" : "签到") + "奖励:",
                "§7○ §3黑币 §7x §6350",
                "§7○ " + REWARD.get(date).getItemMeta().getDisplayName(),
                "",
                "§8[ModernSky] reward"
        );
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
                            initReward();
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
        final ChestMenu menu = new ChestMenu("   签到 §8[ §6" + Calendar.getInstance().get(Calendar.MONTH) + "月 §8]");
        int current_date = java.util.Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        menu.addMenuOpeningHandler(new ChestMenu.MenuOpeningHandler() {
            @Override
            public void onOpen(Player p) {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 0.7F, 0.7F);
            }
        });

        Gson gson = new Gson();
        Boolean exist = null;
        try {
            exist = SqlUtil.ifExist(p.getUniqueId(), SHEET, COL_USER_UUID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        {
            assert exist != null;
            // 存在记录
            if (exist) {
                ResultSet sign = SqlUtil.getResults("SELECT " + COL_SIGN + " FROM " + SHEET + " WHERE " + COL_USER_UUID + " = '" + p.getUniqueId().toString() + "' LIMIT 1;");
                assert sign != null;
                ArrayList<String> result = gson.fromJson(sign.getString(1), new TypeToken<ArrayList<String>>() {
                }.getType());

                SUM_OF_SIGN = result.size();
                // 往日签到
                {
                    for (int i$ = 1; i$ < TimeUtil.getDayOfMonth(); i$++) {
                        if (i$ == current_date) {
                            continue;
                        }
                        menu.addItem(i$ - 1, getIcon(i$, result.contains(Integer.toString(i$)), false));
                        menu.addMenuClickHandler(i$ - 1, new ChestMenu.MenuClickHandler() {
                            @Override
                            public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
                                return false;
                            }
                        });
                    }
                }

                // 今日签到
                {
                    // 今天已签到
                    if (result.contains(Integer.toString(current_date))) {
                        menu.addItem(current_date - 1, getIcon(current_date, true, true));
                        menu.addMenuClickHandler(current_date - 1, new ChestMenu.MenuClickHandler() {
                            @Override
                            public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
                                p.sendMessage(Core.getPrefix() + "今天已签到过了哦，明天再来吧");
                                return false;
                            }
                        });
                        // 今天未签到
                    } else {
                        menu.addItem(current_date - 1, getIcon(current_date, false, true));
                        menu.addMenuClickHandler(current_date - 1, new ChestMenu.MenuClickHandler() {
                            @Override
                            public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
                                ResultSet sign = null;
                                try {
                                    // 更新记录
                                    result.add(Integer.toString(current_date));
                                    String json = gson.toJson(result);
                                    PreparedStatement stmt = Core.getConnection().prepareStatement("UPDATE " + getSheetName() + " SET " + getColSign() + " = '" + json + "' WHERE " + getColUserUuid() + " = '" + p.getUniqueId().toString() + "'");
                                    stmt.executeUpdate();

                                    // 给予奖励
                                    p.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                                    Core.getEconomy().depositPlayer(p, 350);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        });
                    }
                }
                // 不存在记录
            } else {
                // 往日操作
                {
                    for (int i$ = 1; i$ < TimeUtil.getDayOfMonth(); i$++) {
                        if (i$ == current_date) {
                            continue;
                        }
                        menu.addItem(i$ - 1, getIcon(i$, false, false));
                        menu.addMenuClickHandler(i$ - 1, new ChestMenu.MenuClickHandler() {
                            @Override
                            public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
                                return false;
                            }
                        });
                    }
                }
                // 今日签到及写入记录操作
                {
                    menu.addItem(current_date - 1, getIcon(current_date, false, true));
                    menu.addMenuClickHandler(current_date - 1, new ChestMenu.MenuClickHandler() {
                        @Override
                        public boolean onClick(Player p, int arg1, ItemStack arg2, ClickAction arg3) {
                            ArrayList<String> sign = new ArrayList<>(Arrays.asList(Integer.toString(current_date)));
                            String json = gson.toJson(sign);

                            // 写入记录
                            try {
                                PreparedStatement stmt = Core.getConnection().prepareStatement("INSERT INTO " + SHEET + "(" + COL_USER_NAME + ", " + COL_USER_UUID + ", " + COL_SIGN + ") VALUE(?,?,?);");
                                stmt.setObject(1, p.getName());
                                stmt.setObject(2, p.getUniqueId().toString());
                                stmt.setObject(3, json);
                                stmt.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            // 给予奖励
                            p.sendMessage(Core.getPrefix() + "签到奖励已发放~");
                            Core.getEconomy().depositPlayer(p, 350);
                            return false;
                        }
                    });
                }
            }
        }

        // 统计信息
        {
            ItemStack stat = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta meta = stat.getItemMeta();
            meta.setDisplayName("§8[ §6签到统计 §8]");
            List<String> lores = Arrays.asList(
                    "",
                    "§7◇ 本月已累计签到 §3" + SUM_OF_SIGN + " §7天",
                    "",
                    "§8[ModernSky] reward"
            );
            meta.setLore(lores);
            stat.setItemMeta(meta);
            menu.addItem(53, stat);
            menu.addMenuClickHandler(53, new ChestMenu.MenuClickHandler() {

                @Override
                public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
                    return false;
                }
            });
        }

        // 分割线
        for (int i$ : PIPE) {
            menu.addItem(i$, new CustomItem(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 7), " "));
            menu.addMenuClickHandler(i$, new ChestMenu.MenuClickHandler() {

                @Override
                public boolean onClick(Player arg0, int arg1, ItemStack arg2, ClickAction arg3) {
                    return false;
                }
            });
        }

        menu.open(p);
    }

}

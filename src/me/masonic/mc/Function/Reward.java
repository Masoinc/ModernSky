package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import me.masonic.mc.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

// 注: 添加普通物品时，请为物品添加自定义名称后存入Myitem物品数据库中。
public class Reward {
    public int money = 0;
    public int point = 0;
    public HashMap<String, Integer> items = null;

    public Reward(int money, int point, HashMap<String, Integer> items) {
        this.money = money;
        this.point = point;
        this.items = items;
    }

    public int getMoney() {
        return money;
    }

    public int getPoint() {
        return point;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public static Reward getSignKitReward(int days) {
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

    public static Reward getSignReward(int day) {
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

    public static List<String> getLore(Reward reward) {
        if (reward == null) {
            return null;
        }
        List<String> lores = new ArrayList<>();
        MyItemsAPI mapi = MyItemsAPI.getInstance();

        if (!(reward.getMoney() == 0)) {
            lores.add("§7○ §3黑币 §7x §6" + reward.getMoney());
        }
        if (!(reward.getPoint() == 0)) {
            lores.add("§7○ §3尘晶 §7x §6" + reward.getPoint());
        }
        if (!(reward.getItems() == null)) {
            for (String item : reward.getItems().keySet()) {
                lores.add("§7○ " + mapi.getGameManagerAPI().getItemManagerAPI().getItem(item).getItemMeta().getDisplayName() + " §7x" + reward.getItems().get(item));
            }
        }

        return lores;
    }

    public static void sendReward(Player p, Reward reward) {
        if (reward ==null) {return;}

        if (reward.getMoney() != 0) {
            Core.getEconomy().depositPlayer(p, reward.getMoney());
        }

        if (reward.getPoint() != 0) {
            Core.getPlayerPoints().getAPI().give(p.getUniqueId(), reward.getPoint());
        }

        for (String item : reward.items.keySet()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "myitems:mi load custom " + item + " " + p.getPlayerListName() + " " + reward.getItems().get(item));
        }

    }

    public static void sendSignReward(Player p, int day) {
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

        for (String item : reward.items.keySet()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "myitems:mi load custom " + item + " " + p.getPlayerListName() + " " + reward.getItems().get(item));
        }
        if (Exploration.getExplorationRank(p).getSign_additional_money() != 0) {
            Core.getEconomy().depositPlayer(p, Exploration.getExplorationRank(p).getSign_additional_money());
        }
//        if (!Package.isExpired(p,"A")) {
//            Exploration.setExploreValue(p, Exploration.getExploreValue(p) + 10);
//            p.sendMessage(Core.getPrefix() + "签到获得了 §610 §7点探索值");
//        }
    }
}


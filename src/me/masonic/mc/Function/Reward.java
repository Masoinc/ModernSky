package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import me.masonic.mc.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 注: 添加普通物品时，请为物品添加自定义名称后存入Myitem物品数据库中。
public class Reward {
    private int money = 0;
    private int point = 0;
    private HashMap<String, Integer> items = null;
    private HashMap<Material, Integer> raw_items = null;

    public Reward(int money, int point, HashMap<String, Integer> items) {
        this.money = money;
        this.point = point;
        this.items = items;
    }

    public Reward(int money, int point, HashMap<String, Integer> items, HashMap<Material, Integer> raw_items) {
        this.money = money;
        this.point = point;
        this.items = items;
        this.raw_items = raw_items;
    }

    public HashMap<Material, Integer> getRaw_items() {
        return raw_items;
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
        if (reward.raw_items.size() != 0) {
            for (Material m : reward.raw_items.keySet()) {
                ItemStack i = new ItemStack(m);
                i.setAmount(reward.raw_items.get(m));
                p.getInventory().addItem(i);
            }
        }

    }
}




package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import me.masonic.mc.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// 注: 添加普通物品时，请为物品添加自定义名称后存入Myitem物品数据库中。
public class Reward {
    private int money = 0;
    private int point = 0;
    private HashMap<String, Integer> items = null;


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

    public RawReward appendRawItem(HashMap<Material, Integer> raw_items) {
        return new RawReward(this.money, this.money, this.items, raw_items);
    }

    public List<String> getLore() {
        List<String> lores = new ArrayList<>();
        MyItemsAPI mapi = MyItemsAPI.getInstance();

        if (!(this.getMoney() == 0)) {
            lores.add("§7○ §3黑币 §7x §6" + this.getMoney());
        }
        if (!(this.getPoint() == 0)) {
            lores.add("§7○ §3尘晶 §7x §6" + this.getPoint());
        }
        if (!(this.getItems() == null)) {
            for (String item : this.getItems().keySet()) {
                lores.add("§7○ " + mapi.getGameManagerAPI().getItemManagerAPI().getItem(item).getItemMeta().getDisplayName() + " §7x" + this.getItems().get(item));
            }
        }
        return lores;
    }

    public void send(Player p) {
        if (this.getMoney() != 0) {
            Core.getEconomy().depositPlayer(p, this.getMoney());
        }
        if (this.getPoint() != 0) {
            Core.getPlayerPoints().getAPI().give(p.getUniqueId(), this.getPoint());
        }
        for (String item : this.items.keySet()) {
            if (Repository.getStorableMap().contains(item)) {
                int amount = this.items.get(item);
                Repository.getInstance(p).saveItem(new HashMap<String, Integer>() {{
                    put(item, amount);
                }});
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "myitems:mi load custom " + item + " " + p.getPlayerListName() + " " + this.items.get(item));
            }
        }
//        if (reward.raw_items.size() != 0) {
//            for (Material m : reward.raw_items.keySet()) {
//                ItemStack i = new ItemStack(m);
//                i.setAmount(reward.raw_items.get(m));
//                p.getInventory().addItem(i);
//            }
//        }
    }
}

class RawReward extends Reward {
    private HashMap<Material, Integer> raw_items = null;

    public HashMap<Material, Integer> getRaw_items() {
        return raw_items;
    }

    public RawReward(int money, int point, HashMap<String, Integer> items, HashMap<Material, Integer> raw_items) {
        super(money, point, items);
        this.raw_items = raw_items;
    }
}



package me.masonic.mc.Function;

import api.praya.myitems.main.MyItemsAPI;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Vitality.Vitality;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// 注: 添加普通物品时，请为物品添加自定义名称后存入Myitem物品数据库中。
public class Reward {
    private int money = 0;
    private int point = 0;
    private HashMap<String, Integer> items = null;
    private HashMap<Material, Integer> raw_items = null;
    private int vitality = 0;


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

    public int getVitality() {
        return vitality;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    //
//    public RawReward appendRawItem(HashMap<Material, Integer> raw_items) {
//        return new RawReward(this.money, this.money, this.items, raw_items);
//    }
    public Reward appendRawItem(HashMap<Material, Integer> raw_items) {
        this.raw_items = raw_items;
        return this;
    }

    public Reward appendVitality(int v) {
        this.vitality = v;
        return this;
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

    public void send(UUID p) {
        if (this.getMoney() != 0) {
            Core.getEconomy().depositPlayer(Bukkit.getPlayer(p), this.getMoney());
        }
        if (this.getPoint() != 0) {
            Core.getPlayerPoints().getAPI().give(p, this.getPoint());
        }
        for (String item : this.items.keySet()) {
            if (Repository.getStorableMap().contains(item)) {
                int amount = this.items.get(item);
                Repository.getInstance(p).saveItem(new HashMap<String, Integer>() {{
                    put(item, amount);
                }});
            } else {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "myitems:mi load custom " + item + " " + Bukkit.getPlayer(p).getPlayerListName() + " " + this.items.get(item));
            }
        }
        if (this.raw_items != null && this.raw_items.size() != 0) {
            for (Material m : this.raw_items.keySet()) {
                ItemStack i = new ItemStack(m);
                i.setAmount(this.raw_items.get(m));
                Bukkit.getPlayer(p).getInventory().addItem(i);
            }
        }

        if (this.vitality != 0) {
            Vitality.addVitality(p, this.vitality);
        }
    }
}




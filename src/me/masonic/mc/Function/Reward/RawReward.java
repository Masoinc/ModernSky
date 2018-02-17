package me.masonic.mc.Function.Reward;

import org.bukkit.Material;

import java.util.HashMap;

public class RawReward extends Reward {
    private HashMap<Material, Integer> raw_items = null;

    public HashMap<Material, Integer> getRaw_items() {
        return raw_items;
    }

    public RawReward(int money, int point, HashMap<String, Integer> items, HashMap<Material, Integer> raw_items) {
        super(money, point, items);
        this.raw_items = raw_items;
    }
}

package me.masonic.mc.Utility;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
    public static void takeItem(Inventory inventory, String displayname, int qty) {
        int found_items = 0;
        ItemStack[] arr$ = inventory.getContents();
        int len$ = arr$.length;
        for(int i$ = 0; i$ < len$; ++i$) {
            ItemStack it = arr$[i$];
            if (found_items < qty&& it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equalsIgnoreCase(displayname)) {
                int amt = it.getAmount();
                if (found_items + it.getAmount() > qty) {
                    it.setAmount(it.getAmount() - (qty - found_items));
                }
                inventory.removeItem(it);
                found_items += amt;
            }
        }
    }
}

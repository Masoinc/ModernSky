package me.masonic.mc.Objects;

import me.masonic.mc.Utility.PermissionUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import org.bukkit.Material;

import java.util.ArrayList;

public class Icons {
    static CustomItem getDefault() {
        return new CustomItem(Material.AIR, "", 0, 1, new ArrayList<>());
    }

    public static void addBaseIcon(ChestMenu menu, String type, int slot) {
        switch (type) {
            case "back":
                menu.addItem(slot, new CustomItem(Material.REDSTONE, "§8[ §c返回 §8]", 0, 1, new ArrayList<>()));
                menu.addMenuClickHandler(slot, (p15, arg1, arg2, arg3) -> {
                    PermissionUtil.runOp(p15, "bs mskycore");
                    return false;
                });
        }
    }

    public static void addPrivIcon(ChestMenu menu, String type) {
        switch (type) {
            case "pipe":
                int[] PIPE = new int[]{14, 15, 16, 32, 33, 34, 30, 29, 28};
                for (int i : PIPE) {
                    menu.addItem(i, new CustomItem(Material.STAINED_GLASS_PANE, "§8[ §6未激活的奖励 §8]", 15, 1, new ArrayList<>()));
                    menu.addMenuClickHandler(i, (p15, arg1, arg2, arg3) -> false);
                }
        }
    }


    public static void addPipe(ChestMenu menu, int[] slots) {
        for (int i$ : slots) {
            menu.addItem(i$, new CustomItem(Material.STAINED_GLASS_PANE, "§7", 15, 1, new ArrayList<>()));
            menu.addMenuClickHandler(i$, (arg0, arg1, arg2, arg3) -> false);
        }
    }

}

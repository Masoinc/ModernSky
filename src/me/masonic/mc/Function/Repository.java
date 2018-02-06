package me.masonic.mc.Function;

import me.masonic.mc.Objects.Icons;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;

import javax.swing.*;
import java.util.Calendar;

public class Repository {
    void openRp(Player p) {
        final ChestMenu menu = new ChestMenu(" 后勤仓库");
        Icons.addPipe(menu, new int[]{7, 16, 25, 34, 43, 52, 45, 46, 47, 49, 50, 51});
        Icons.addBaseIcon(menu, "back", 48);

        menu.open(p);
    }
}

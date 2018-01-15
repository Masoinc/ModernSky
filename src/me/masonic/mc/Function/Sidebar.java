package me.masonic.mc.Function;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.masonic.mc.Core;
import net.aufdemrand.denizen.nms.impl.Sidebar_v1_11_R1;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Sidebar implements Listener {
    private static Core plugin;

    public Sidebar(Core plugin) {
        Sidebar.plugin = plugin;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent e) {
        sendBar(e.getPlayer());
    }

    public static void sendBar(Player p) {
        Sidebar_v1_11_R1 bar = new Sidebar_v1_11_R1(p);
        bar.remove();
        bar.sendUpdate();
        bar.setTitle("      §8[ §6个人信息 §8]      ");
        bar.setLines(getSideInfo(p));
        bar.sendUpdate();
    }

    private static List<String> getSideInfo(Player p) {
        ArrayList<String> info = new ArrayList<>(Arrays.asList("§7",
                "§6◇ §7银行余额:    ",
                "§8-> §8[ §6" + Core.getEconomy().getBalance(p) + " §7黑币 §8]",
                "§0",
                "§6◇ §7尘晶余额:    ",
                "§8-> §8[ §3" + Core.getPlayerPoints().getAPI().look(p.getUniqueId()) + "§7 枚 §8]",
                "",
                "§8-> §8[ §7空岛等级: §6Lv."+ ASkyBlockAPI.getInstance().getIslandLevel(p.getUniqueId())+" §8]",
                "§9",
                "§8[ModernSky] alpha v0.0.1"));

        return info;
    }

    public void sendSchedulely() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    sendBar(p);
                }
            }
        }.runTaskTimer(plugin, 0, 1200);
    }
}

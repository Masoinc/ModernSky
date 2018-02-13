package me.masonic.mc.Cmd;

import me.masonic.mc.Function.Repository;
import me.masonic.mc.Function.Vitality;
import me.masonic.mc.Utility.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MskyCore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        PermissionUtil.checkPermission(c);
        if (args.length == 0) {
            c.sendMessage("§7-------------------------------------");
            c.sendMessage("");
            c.sendMessage("§7>>>>> §6ModernSky");
            c.sendMessage("§7>>> §7Author: Masonic§8[§6QQ: 954590000§8]");
            c.sendMessage("§7>>> §7Version: §6v0.2.5");
            c.sendMessage("");
            c.sendMessage("§7-------------------------------------");
            return true;
        }
        Player p;
        switch (args[0].toLowerCase()) {
            case "rp":
                switch (args[1]) {
                    case "open":
                        Repository.openRp((Player) c);
                        return true;
                    //mskycore rp send [player] [item] [amount]
                    case "send":
                        p = Bukkit.getPlayer(args[2]);
                        Repository.getInstance(p).saveItem(new HashMap<String, Integer>() {
                            {
                                put(args[3], Integer.valueOf(args[4]));
                            }
                        });
                        Repository.getInstance(p).refreshCache();
                        return true;
                    case "test":
                        p = Bukkit.getPlayer(args[2]);
                        Repository.getInstance(p).saveItem(new HashMap<String, Integer>() {
                            {
                                put("sf1", 1);
                                put("sf2", 1);
                                put("sf3", 1);
                                put("sf4", 1);
                                put("sf5", 1);
                            }
                        });
                        return true;

                }
            case "vi":
                switch (args[1]) {
                    case "open":
                        Vitality.openvi((Player) c);
                }
            case "test":

                System.out.println(Material.AIR.name());
        }
        return true;
    }

}

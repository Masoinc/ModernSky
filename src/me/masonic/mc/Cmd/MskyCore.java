package me.masonic.mc.Cmd;

import me.masonic.mc.Function.Repository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MskyCore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
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
        switch (args[0].toLowerCase()) {
            case "rp":
                Repository.openRp((Player)c);
        }
        return true;
    }

}

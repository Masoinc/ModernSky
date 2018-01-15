package me.masonic.mc.Cmd;

import me.masonic.mc.Function.Exploration;
import me.masonic.mc.Function.Package;
import me.masonic.mc.Utility.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MskyPackage implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        c.sendMessage(c.getName());
        if ((c instanceof Player && c.isOp()) || c.getName().equalsIgnoreCase("CONSOLE")) {
            switch (args.length) {
                case 3:
                    switch (args[0]) {
                        case "send":
                            Player p = Bukkit.getPlayerExact(args[1]);
                            switch (args[2]) {
                                case "30":
                                    MessageUtil.sendFullMsg(p, Package.sendPackage(p, 30));
                            }
                            return true;
                    }
            }
        }


        return false;
    }
}

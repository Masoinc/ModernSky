package me.masonic.mc.Cmd;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
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
    //MskyPac send [id] [type] [duration]
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {

        if ((c instanceof Player && c.isOp()) || c.getName().equalsIgnoreCase("CONSOLE")) {
            switch (args.length) {
                case 4:
                    switch (args[0]) {
                        case "send":
                            Player p = Bukkit.getPlayerExact(args[1]);
                            switch (args[2]) {
                                case "A":
                                    try {
                                        MessageUtil.sendFullMsg(p, Package.sendPackage(p, Integer.valueOf(args[3]), args[2]));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                            }
                            return true;
                    }
            }
        }


        return false;
    }
}

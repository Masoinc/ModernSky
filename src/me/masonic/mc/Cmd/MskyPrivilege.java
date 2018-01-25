package me.masonic.mc.Cmd;

import me.masonic.mc.Function.ExpPriviledge;
import me.masonic.mc.Function.Package;
import me.masonic.mc.Function.Privilege;
import me.masonic.mc.Utility.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MskyPrivilege implements CommandExecutor {
    @Override

    // [period] 以天计
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        if ((c instanceof Player && c.isOp()) || c.getName().equalsIgnoreCase("CONSOLE")) {
            switch (args.length) {
                // MskyPri open [Player]
                case 2:
                    switch (args[0]) {
                        case "open":
                        Player p = Bukkit.getPlayerExact(args[1]);
                        Privilege.openPrivilegeMenu(p);
                    }
                case 5:
                    switch (args[0]) {

                        case "send":
                            Player p = Bukkit.getPlayerExact(args[1]);
                            Long period = Long.valueOf(args[3]) * 86400;
                            Long amplifier = Long.valueOf(args[4]);
                            switch (args[2]) {
                                case "exp":
                                    ExpPriviledge.sendExpPrivilege(p, period, amplifier);

                            }
                            return true;
                    }
            }
            return true;
        }
        return false;
    }
}

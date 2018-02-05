package me.masonic.mc.Cmd;

import me.masonic.mc.Core;
import me.masonic.mc.Function.Privilege.AbilityPrivilege;
import me.masonic.mc.Function.Privilege.BackPackPrivilege;
import me.masonic.mc.Function.Privilege.ExpPriviledge;
import me.masonic.mc.Function.Privilege.PrivilegeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MskyPrivilege implements CommandExecutor {
    @Override

    // [period] 以天计
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        if ((c instanceof Player && c.isOp()) || c.getName().equalsIgnoreCase("CONSOLE")) {
            switch (args[0]) {
                case "open":
                    Player p0 = Bukkit.getPlayerExact(args[1]);
                    PrivilegeManager.openPrivilegeMenu(p0);
                    return true;
                case "send":
                    Player p1 = Bukkit.getPlayerExact(args[2]);
                    Long period = Long.valueOf(args[3]) * 86400;

                    switch (args[1]) {
                        // MskyPri send [type] [player] [period] [option]
                        case "exp":
                            // MskyPri send exp [player] [period] [amplifier]
                            Long amplifier = Long.valueOf(args[4]);
                            ExpPriviledge.send(p1, period, amplifier);
                            return true;
                        case "bp":
                            // MskyPri send exp [player] [period] [page]
                            Long page = Long.valueOf(args[4]);
                            BackPackPrivilege.send(p1, period, page);
                            return true;
                        case "ab":
                            // MskyPri send exp [player] [period] [limit] [discount]
                            Long limit = Long.valueOf(args[4]);
                            Long discount = Long.valueOf(args[5]);
                            AbilityPrivilege.send(p1, period, limit, discount);
                            return true;
                        default:
                            return true;
                    }
                case "test":
//                    c.sendMessage(String.valueOf(ExpPriviledge.getInstance((Player) c).isExpired()));
//                    c.sendMessage(String.valueOf(ExpPriviledge.getInstance((Player) c).getExpire()));
//                    c.sendMessage(String.valueOf(System.currentTimeMillis() / 1000));
                    c.sendMessage(String.valueOf(ExpPriviledge.getInstance((Player) c).getAmplifier()));
                    return true;
                default:
                    return true;
            }
        } else {
            c.sendMessage(Core.getPrefix() + "Permission Denied");
            return true;
        }
    }
}

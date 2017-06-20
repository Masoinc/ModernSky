package me.masonic.mc.Cmd;

import me.masonic.mc.Function.Vip;
import me.masonic.mc.Utility.SqlUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Mason Project
 * 2017-6-20-0020
 */
public class MskyVip implements CommandExecutor {

    //mskyvip vip id days
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player && commandSender.isOp()) {
            Player p = (Player) commandSender;
            switch (args.length) {
                case 0:
                    p.sendMessage("§8[ §ModernSky §8] §7指令有误");
                    return true;
                case 3:
                    int days = Integer.getInteger(args[2]);
                    int kita = days / 15;
                    switch (args[0]) {
                        case "vip":
                            Player pr = Bukkit.getPlayer(args[1]);
                            if (Vip.getVipRank(pr).equals("SVIP") || Vip.getVipRank(pr).equals("SVIP+")) {
                                pr.sendMessage("§8[ §ModernSky §8] §7已开通Svip或Svip+");
                                p.sendMessage("§8[ §ModernSky §8] §7已开通Svip或Svip+");
                                return true;
                            }
                            try {
                                if (SqlUtility.getIfExist(pr, "vip")) {
                                    // 续费
                                    if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                            SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add vip");
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv1 " + kita);


                                        pr.sendMessage("§8[ §ModernSky §8] §7已续费§2 Vip §7感谢您的支持");

                                        SqlUtility.uploadIntValue(pr, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        case "svip":
                        case "svip+":
                    }
            }
        } else {
            commandSender.sendMessage("§8[ §ModernSky §8] §7权限不足");
            return true;
        }
        return false;
    }
}

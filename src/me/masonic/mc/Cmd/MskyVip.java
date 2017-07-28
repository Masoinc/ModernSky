package me.masonic.mc.Cmd;

import me.masonic.mc.Core;
import me.masonic.mc.Function.Vip;
import me.masonic.mc.Utility.SqlUtility;
import me.masonic.mc.Utility.TimeUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

/**
 * Mason Project
 * 2017-6-20-0020
 */
public class MskyVip implements CommandExecutor, Listener {

    //mskyvip vip id days
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player && commandSender.isOp()) {
            Player p = (Player) commandSender;



            switch (args.length) {
                case 0:
                    p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                    return true;
                case 1:
                    switch (args[0]) {
                        case "clear":
                            clearVipState(p);
                            return true;
                        default:
                            p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                            return true;
                    }
                case 2:
                    Player pc = Bukkit.getPlayer(args[1]);
                    switch (args[0]) {
                        case "clear":
                            clearVipState(pc);
                            return true;
                        default:
                            p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                            return true;
                    }
                case 3:
                    Player precv = Bukkit.getPlayer(args[1]);
                    int days = Integer.parseInt(args[2]);
                    int kita = days / 15;
                    switch (args[0]) {
                        case "vip":
                            switch (Vip.getVipRank(precv)) {

                                case "SVIP+":
                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通Svip或Svip+");
                                    return true;
                                case "SVIP":
                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通Svip或Svip+");
                                    return true;
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        // VIP -> VIP
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add vip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv1 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已续费§2 Vip §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //DEFAULT -> VIP
                                default:

                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add vip");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv1 " + kita);

                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通§2 Vip §7，感谢您的支持");
                                    try {
                                        SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                            }


                        case "svip":
                            switch (Vip.getVipRank(precv)) {
                                // SVIP+
                                case "SVIP+":
                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通Svip+");
                                    return true;
                                //SVIP -> SVIP
                                case "SVIP":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv2 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已续费§6 Svip §7，感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //VIP -> SVIP
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 2;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv2 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§6 Svip §7 ，感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days + daysbonus));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv2 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§6 Svip §7 ，感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //DEFAULT -> SVIP
                                default:

                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv2 " + kita);

                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通§6 Svip ，§7感谢您的支持");
                                    try {
                                        SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;

                            }
                        case "svip+":
                            switch (Vip.getVipRank(precv)) {
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 3;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days + daysbonus));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case "SVIP":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 2;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§6 SVip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days + daysbonus));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已由§6 SVip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case "SVIP+":
                                    try {
                                        if (!SqlUtility.getIfExist(precv, "vip")) {
                                            SqlUtility.createColumn(precv, "vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已续费§c Svip+ §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);


                                            precv.sendMessage("§8[ §6ModernSky §8] §7已开通§c Svip+ §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(precv, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                default:
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + precv.getName() + " group add svip+");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + precv.getName() + " kitvipv3 " + kita);

                                    precv.sendMessage("§8[ §6ModernSky §8] §7已开通§c Svip+ ，§7感谢您的支持");
                                    try {
                                        SqlUtility.uploadIntValue(precv, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                            }
                        default:
                            p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                            return true;
                    }
                default:
                    p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                    return true;
            }
        } else {
            commandSender.sendMessage("§8[ §6ModernSky §8] §7权限不足");
            return true;
        }
    }

    private void clearVipState(Player p) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group remove vip");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group remove svip");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group remove svip+");
        try {
            SqlUtility.uploadIntValue(p, "vip", "expiration", 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void expiredCheck(Player p, String rank) {
        try {
            // 已过期
            int expitime = SqlUtility.getIntValue(p, "vip", "expiration");
            if (expitime != 0 && expitime < TimeUtility.getCurrentSTime()) {
                p.sendMessage(Core.getPrefix() + "您的 §6" + rank + " 已过期，过期后无法享受VIP特权");
                p.sendMessage(Core.getPrefix() + "请及时续费，感谢您的支持");
                SqlUtility.uploadIntValue(p, "vip", "expiration", 0);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + p.getName() + " group remove " + rank);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        switch (Vip.getVipRank(p)) {
            case "VIP":
                expiredCheck(p, "VIP");
            case "SVIP":
                expiredCheck(p, "SVIP");
            case "SVIP+":
                expiredCheck(p, "SVIP+");
        }
    }
}

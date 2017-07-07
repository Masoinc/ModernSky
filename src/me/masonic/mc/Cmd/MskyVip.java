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
                    p.sendMessage("§8[ §6ModernSky §8] §7指令有误");
                    return true;
                case 3:
                    int days = Integer.parseInt(args[2]);
                    int kita = days / 15;
                    Player pr = Bukkit.getPlayer(args[1]);
                    switch (args[0]) {
                        case "vip":
                            switch (Vip.getVipRank(pr)) {
                                case "SVIP+":
                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通Svip或Svip+");
                                    return true;
                                case "SVIP":
                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通Svip或Svip+");
                                    return true;
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        // VIP -> VIP
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add vip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv1 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已续费§2 Vip §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //DEFAULT -> VIP
                                default:

                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add vip");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv1 " + kita);

                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通§2 Vip ，§7感谢您的支持");
                                    try {
                                        SqlUtility.createColumn(p, "vip");
                                        SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                            }


                        case "svip":
                            switch (Vip.getVipRank(pr)) {
                                // SVIP+
                                case "SVIP+":
                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通Svip+");
                                    return true;
                                //SVIP -> SVIP
                                case "SVIP":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv2 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已续费§6 Svip §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //VIP -> SVIP
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 2;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv2 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§6 Svip §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * (days + daysbonus)));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv2 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§6 Svip §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                //DEFAULT -> SVIP
                                default:

                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv2 " + kita);

                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通§6 Svip ，§7感谢您的支持");
                                    try {
                                        SqlUtility.createColumn(p, "vip");
                                        SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;

                            }
                        case "svip+":
                            switch (Vip.getVipRank(pr)) {
                                case "VIP":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 3;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * (days + daysbonus)));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§2 Vip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 *  days));
                                            return true;
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case "SVIP":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0) {
                                            int daysbonus = (int) (SqlUtility.getIntValue(p, "vip", "expiration") - (System.currentTimeMillis() / 1000)) / 2;

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§6 SVip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * (days + daysbonus)));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已由§6 SVip §7升级为§c Svip+ §7 ，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                case "SVIP+":
                                    try {
                                        if (!SqlUtility.getIfExist(pr, "vip")) {
                                            SqlUtility.createColumn(pr,"vip");
                                        }
                                        if (SqlUtility.getIntValue(p, "vip", "expiration") != 0 &&
                                                SqlUtility.getIntValue(p, "vip", "expiration") > (System.currentTimeMillis() / 1000)) {

                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已续费§c Svip+ §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));
                                            return true;
                                        } else {
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);


                                            pr.sendMessage("§8[ §6ModernSky §8] §7已开通§c Svip+ §7，§7感谢您的支持");

                                            SqlUtility.uploadIntValue(pr, "vip", "expiration", (SqlUtility.getIntValue(p, "vip", "expiration") + 86400 * days));

                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                default:
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "pex user " + pr.getName() + " group add svip+");
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "cr give " + pr.getName() + " kitvipv3 " + kita);

                                    pr.sendMessage("§8[ §6ModernSky §8] §7已开通§c Svip+ ，§7感谢您的支持");
                                    try {
                                        SqlUtility.createColumn(p, "vip");
                                        SqlUtility.uploadIntValue(pr, "vip", "expiration", (int) ((System.currentTimeMillis() / 1000) + 86400 * days));
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
}

package me.masonic.mc.Utility;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageUtil {
    public static void sendFullMsg(Player p, String msg) {
        List<String> msgs = Arrays.asList(
                "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一",
                "",
                "         §7" + msg,
                "",
                "§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一");
        p.sendMessage((String[]) msgs.toArray());
    }

    /**
     * 发送全屏信息，信息超过6行效果会受到影响
     *
     * @param p   玩家
     * @param msg 要发送的信息，建议不要超过6行
     */
    public static void sendFullMsg(Player p, List<String> msg) {
        List<String> msgs = new ArrayList<>();
        p.sendMessage("§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一");
        p.sendMessage("");
        p.sendMessage((String[]) msg.toArray());
        p.sendMessage("");
        p.sendMessage("§c§m§l一一§6§m§l一一§e§m§l一一§a§m§l一一§3§m§l一一一一一一一一一一一一§a§m§l一一§e§m§l一一§6§m§l一一§c§m§l一一§7§m§l一一");

    }

}

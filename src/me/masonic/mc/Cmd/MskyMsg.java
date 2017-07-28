package me.masonic.mc.Cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Mason Project
 * 2017-7-22-0022
 */
public class MskyMsg implements CommandExecutor {
    public void narrateChargeInfo(Player p) {
        List<String> ci = Arrays.asList(
                "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一",
                "",
                "  §c§l充值网址: §6§lhttp://ModernX.mcrmb.com  §7(直接点击即可打开)",
                "",
                "§6§m§l一一一一一一一一一一一一一一一一一一一一一一一一一一一一");
        p.sendMessage((String[])ci.toArray());
    }

    @Override
    public boolean onCommand(CommandSender c, Command command, String s, String[] args) {
        switch(args[0]) {
            case "charge":
                narrateChargeInfo((Player)c);
                return true;
        }
        return false;
    }
}

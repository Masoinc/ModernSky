package me.masonic.mc.Cmd;

import me.mrCookieSlime.Slimefun.SlimefunGuide;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MskySlimeFun implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        if (c instanceof Player) {
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "open":
                            SlimefunGuide.openGuide((Player) c, false);
                            return true;
                    }

            }
        }
        return false;
    }
}

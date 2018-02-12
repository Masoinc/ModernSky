package me.masonic.mc.Cmd;

import me.masonic.mc.Function.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MskySign implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender c, Command cmd, String s, String[] args) {
        if (c instanceof Player) {
            Player p = (Player) c;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "open":
                            Sign.openSignMenu(p);
                            return true;
                    }
            }
        }
        return false;
    }
}

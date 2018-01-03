package me.masonic.mc.Cmd;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.PermissionUtil;
import org.black_ixx.bossshop.addon.playershops.PlayerShops;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class MskyBackShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender c, Command command, String s, String[] args) {
        if (c instanceof Player) {
            Player p = (Player) c;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "open":
                            if (ASkyBlockAPI.getInstance().getIslandLevel(p.getUniqueId()) >= 100) {
                                PermissionUtil.runOp(p,"playershop");
                            } else {
                                p.sendMessage(Core.getPrefix() + "你还没有解锁黑市商店呢，加油升级吧！");
                            }
                            return true;
                    }
            }
        }
        return false;
    }
}

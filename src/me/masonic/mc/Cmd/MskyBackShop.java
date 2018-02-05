package me.masonic.mc.Cmd;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import me.masonic.mc.Core;
import me.masonic.mc.Utility.PermissionUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MskyBackShop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender c, Command command, String s, String[] args) {
        if (c instanceof Player) {
            Player p = (Player) c;
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "openbs":
                            if (ASkyBlockAPI.getInstance().getIslandLevel(p.getUniqueId()) >= 200) {
                                PermissionUtil.runOp(p,"playershop");
                            } else {
                                p.sendMessage(Core.getPrefix() + "你还没有解锁黑市商店呢，加油升级吧！");
                            }
                            return true;
                        case "openps":
                            if (ASkyBlockAPI.getInstance().getIslandLevel(p.getUniqueId()) >= 30) {
                                PermissionUtil.runOp(p,"bs itemshops");
                            } else {
                                p.sendMessage(Core.getPrefix() + "你还没有解锁物资回收系统呢，加油升级吧！");
                            }
                            return true;
                    }

            }
        }
        return false;
    }
}

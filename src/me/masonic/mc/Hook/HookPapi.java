package me.masonic.mc.Hook;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Vip;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Mason Project
 * 2017-6-19-0019
 */
public class HookPapi extends EZPlaceholderHook {
    private Core Plugin;

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        //%ModernSky_getVipRank%
        if (identifier.equals("getVipRank")) {
            return Vip.getVipRank$Formatted(p);
        }
        return null;
    }

    public HookPapi(Core Plugin) {
        super(Plugin, "ModernSky");
        this.Plugin = Plugin;
    }

}
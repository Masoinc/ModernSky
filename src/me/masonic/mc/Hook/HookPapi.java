package me.masonic.mc.Hook;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.masonic.mc.Function.Exploration;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Package;
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
        //%ModernSky_getVipExpi%
        if (identifier.equals("getVipExpi")) {
            try {
                return Vip.getVip$Expiration(p);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //%ModernSky_getExplore%
        if (identifier.equals("getExplore")) {
            return String.valueOf(Exploration.getExploreValue(p));
        }

        //%ModernSky_getExploreTag%
        if (identifier.equals("getExploreTag")) {
            return String.valueOf(Exploration.getExploreTag(p));
        }
        //%ModernSky_getExplorePrefix%
        if (identifier.equals("getExplorePrefix")) {
            return String.valueOf(Exploration.getExplorePrefix(p));
        }
        //%ModernSky_getPackageState%
        if (identifier.equals("getPackageState")) {
            return String.valueOf(Package.getPackageState(p));
        }

        return null;

    }

    public HookPapi(Core Plugin) {
        super(Plugin, "ModernSky");
        this.Plugin = Plugin;
    }

}
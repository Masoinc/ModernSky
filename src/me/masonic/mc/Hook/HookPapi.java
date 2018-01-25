package me.masonic.mc.Hook;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.masonic.mc.Function.ExpPriviledge;
import me.masonic.mc.Function.Exploration;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Package;
//import me.masonic.mc.Function.Vip;
import org.bukkit.entity.Player;

import java.sql.SQLException;

/**
 * Mason Project
 * 2017-6-19-0019
 */
public class HookPapi extends EZPlaceholderHook {

    private final Core Plugin;

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        switch (identifier.split("_")[1]) {
            case "Package":
                switch (identifier.split("_")[2]) {
                    // %ModernSky_get_Package_StateA%
                    case "StateA":
                        return String.valueOf(Package.getPackageState(p, "A"));
                }
            case "Privilege":
                switch (identifier.split("_")[2]) {

                    case "Exp":
                        switch (identifier.split("_")[3]) {
                            // %ModernSky_get_Privilege_Exp_amplifier%
                            case "amplifier":
                                return ExpPriviledge.getFormattedAmplifier(p);
                            case "expire":

                        }

                    default:
                        return null;
                }
        }
//        //%ModernSky_getVipRank%
//        if (identifier.equals("getVipRank")) {
//            return Vip.getVipRank$Formatted(p);
//        }
//        //%ModernSky_getVipExpi%
//        if (identifier.equals("getVipExpi")) {
//            try {
//                return Vip.getVip$Expiration(p);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
        //%ModernSky_getExplore%

        if (identifier.equals("get_Explore")) {
            return String.valueOf(Exploration.getExploreValue(p));
        }

        //%ModernSky_getExploreTag%
        if (identifier.equals("get_Explore_Tag")) {
            return String.valueOf(Exploration.getExploreTag(p));
        }
        //%ModernSky_getExplore_Prefix%
        if (identifier.equals("get_Explore_Prefix")) {
            return String.valueOf(Exploration.getExplorePrefix(p));
        }
        return null;
    }

    public HookPapi(Core Plugin) {
        super(Plugin, "ModernSky");
        this.Plugin = Plugin;
    }

}
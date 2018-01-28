package me.masonic.mc.Hook;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import me.masonic.mc.Function.Privilege.AbilityPrivilege;
import me.masonic.mc.Function.Privilege.BackPackPrivilege;
import me.masonic.mc.Function.Privilege.ExpPriviledge;
import me.masonic.mc.Function.Exploration;
import me.masonic.mc.Core;
import me.masonic.mc.Function.Package;
//import me.masonic.mc.Function.Vip;
import org.bukkit.entity.Player;

/**
 * Mason Project
 * 2017-6-19-0019
 */
public class HookPapi extends EZPlaceholderHook {

    private final Core Plugin;

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        switch (identifier.split("_")[0]) {
            case "Package":
                switch (identifier.split("_")[1]) {
                    // %ModernSky_Package_StateA%
                    case "StateA":
                        return String.valueOf(Package.getPackageState(p, "A"));
                }
            case "Privilege":
                switch (identifier.split("_")[1]) {
                    case "Exp":
                        switch (identifier.split("_")[2]) {
                            // %ModernSky_Privilege_Exp_Amplifier%
                            case "Amplifier":
                                return ExpPriviledge.getInstance(p).getFormattedAmplifier();
                            // %ModernSky_Privilege_Exp_Expire%
                            case "Expire":
                                return ExpPriviledge.getInstance(p).getFormattedExpire();
                        }
                    case "Backpack":
                        switch (identifier.split("_")[2]) {
                            // %ModernSky_Privilege_Backpack_Page%
                            case "Page":
                                return BackPackPrivilege.getInstance(p).getFormattedPage();
                            // %ModernSky_Privilege_Backpack_Expire%
                            case "Expire":
                                return BackPackPrivilege.getInstance(p).getFormattedExpire();
                        }
                    case "Ability":
                        switch (identifier.split("_")[2]) {
                            // %ModernSky_Privilege_Ability_Discount%
                            case "Discount":
                                return AbilityPrivilege.getInstance(p).getFormattedDiscount();
                            // %ModernSky_Privilege_Ability_Limit%
                            case "Limit":
                                return AbilityPrivilege.getInstance(p).getFormattedLimit();
                            case "Expire":
                            // %ModernSky_Privilege_Ability_Expire%
                                return AbilityPrivilege.getInstance(p).getFormattedExpire();
                        }

                }
            case "Explore":
                switch (identifier.split("_")[1]) {
                    // %ModernSky_Explore_Value%
                    case "Value":
                        return String.valueOf(Exploration.getExploreValue(p));
                    // %ModernSky_Explore_Tag%
                    case "Tag":
                        return String.valueOf(Exploration.getExploreTag(p));
                    case "Prefix":
                        return String.valueOf(Exploration.getExplorePrefix(p));
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

        return null;
    }

    public HookPapi(Core Plugin) {
        super(Plugin, "ModernSky");
        this.Plugin = Plugin;
    }

}
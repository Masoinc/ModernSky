//package me.masonic.mc.Cmd;
//
//import me.masonic.mc.Core;
//import me.masonic.mc.Utility.SqlUtil;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//import static me.masonic.mc.Utility.TimeUtil.getCurrentSTime;
//
//
///**
// * Masonic Project
// * 2017/6/1 0001
// */
//@SuppressWarnings("ALL")
//public class MskyDailyReward implements CommandExecutor {
//    private static PreparedStatement statement;
//
//    private static final String SHEET_NAME = "dailyreward";
//
//    @Override
//    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
//        Player p = (Player) commandSender;
//        try {
//            if (setDailyRewardCD(p)) {
//                Core.getEconomy().depositPlayer(p, 500);
//                p.sendMessage(Core.getPrefix() + "§6500 §7黑币已发放至银行账户");
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return true;
//    }
//
//    private static String getDailyRewardCD$Formatted(long cd) {
//        if (cd == 0) {
//            return "§3 0 §7小时§3 0 §7分钟";
//        }
//        long hour = cd / 3600;
//        long minute = (cd - hour * 3600) / 60;
//        return "§3" + hour + " §7小时§3 " + minute + " §7分钟";
//    }
//
//    private static long getDailyRewardCD(Player p) throws SQLException {
//
//        if (!SqlUtil.getIfExist(p, "dailyreward")) {
//            return 0;
//        }
//
//        String sql = "SELECT lastget FROM dailyreward WHERE id = ? LIMIT 1;";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, p.getName());
//        ResultSet rs = statement.executeQuery();
//        while (rs.next()) {
//            if (rs.wasNull()) {
//                return 0;
//            }
//            return rs.getInt(1) > getCurrentSTime() ? rs.getInt(1) - getCurrentSTime() : 0;
//        }
//        return 0;
//    }
//
//    private static boolean setDailyRewardCD(Player p) throws SQLException {
//
//        //无记录
//        if (!SqlUtil.getIfExist(p, SHEET_NAME)) {
//            String sql = "INSERT INTO dailyreward (id,lastget) VALUES (?," + getCurrentSTime(86400) + ");";
//            PreparedStatement statement = Core.getConnection().prepareStatement(sql);
//            statement = Core.getConnection().prepareStatement(sql);
//            statement.setObject(1, p.getName());
//            statement.executeUpdate();
//            return true;
//        }
//
//        //有记录且未过期
//        long cd = getDailyRewardCD(p);
//        if (cd != 0) {
//            p.sendMessage(Core.getPrefix() + "剩余冷却: " + getDailyRewardCD$Formatted(cd));
//            return false;
//        } else {
//            String sql = "UPDATE promode SET lastswitch = ? WHERE id = ?;";
//            PreparedStatement statement3 = Core.getConnection().prepareStatement(sql);
//            statement3.setObject(1, getCurrentSTime(86400));
//            statement3.setObject(2, p.getName());
//            statement3.executeUpdate();
//            return true;
//        }
//    }
//}

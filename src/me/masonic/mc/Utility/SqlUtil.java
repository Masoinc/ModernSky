package me.masonic.mc.Utility;

import me.masonic.mc.Core;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.UUID;

public class SqlUtil {

    /**
     * 执行语句
     *
     * @param query 要执行的MySQL语句
     */
    public static void update(String query) {
        PreparedStatement statement = null;
        try {
            statement = Core.getConnection().prepareStatement(query);
            statement.executeUpdate();
        } catch (SQLException var7) {
            var7.printStackTrace();
        } finally {
            closeResources(null, statement);
        }
    }

    /**
     * 获取语句的查询结果
     *
     * @param query 要执行的MySQL语句
     * @return 获取的结果
     */
    public static ResultSet getResults(String query) {

        Statement stmt = null;
        try {
            stmt = Core.getConnection().createStatement();
            ResultSet set = stmt.executeQuery(query);

            while (set.next()) {
                return set;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 回收资源
     *
     * @param set       结果
     * @param statement 要回收的statement
     */
    public static void closeResources(ResultSet set, PreparedStatement statement) {
        try {
            if (set != null) set.close();
            if (statement != null) statement.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

//    public static boolean getIfExist(Player p, String sheet) throws SQLException {
//        String sql = "SELECT COUNT(*) FROM " + sheet + " WHERE id = ? LIMIT 1;";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, p.getName());
//        ResultSet rs = statement.executeQuery();
//        if (rs.wasNull()) {
//            return false;
//        }
//        while (rs.next()) {
//            return rs.getInt(1) == 1;
//        }
//        return false;
//    }

    /**
     * 判断MySQL数据表中是否有某个UUID的记录
     *
     * @param uid    玩家的UUID
     * @param sheet  要查询的数据表名
     * @param column 要查询的列名
     * @return true或false
     */
    public static boolean ifExist(UUID uid, String sheet, String column) {
        try {
            String sql = "SELECT COUNT(*) FROM {0} WHERE {1} = ''{2}'' LIMIT 1;";
            ResultSet rs = Core.getConnection().createStatement().executeQuery(MessageFormat.format(sql, sheet, column, uid));
            Boolean empty = true;
            while (rs.next()) {
                empty = false;
                if (rs.wasNull()) {
                    return false;
                }
                return rs.getInt(1) >= 1;
            }
            if (empty) {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


//    /**
//     * 更新MySQL数据表中某个玩家的整数记录
//     * 方法内部自动判断
//     *
//     * @param p      玩家
//     * @param sheet  要更新的数据表名
//     * @param column 要更新的列名
//     * @param value    要更新的整数值
//     */
//    public static void updateIntValue(String pname, String sheet, String column, int value) throws SQLException {
//        if (!ifExist(pname, sheet, column)) {
//            createColumnWithVip(p, sheet, value);
//            return;
//        }
//        String sql = "UPDATE " + sheet + " SET " + column + " = ? WHERE " + column + " = ?;";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, value);
//        statement.setObject(2, p.getName());
//        statement.executeUpdate();
//    }

//    public static void createColumn(Player p, String sheet) throws SQLException {
//        String sql = "INSERT INTO " + sheet + " (id) VALUES (?);";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, p.getName());
//        statement.executeUpdate();
//    }
//
//    public static void createColumnWithVip(Player p, String sheet, int value) throws SQLException {
//        String sql = "INSERT INTO " + sheet + " (id,expiration) VALUES (?,?);";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, p.getName());
//        statement.setObject(2, value);
//        statement.executeUpdate();
//    }

//    public static void uploadIntValue(Player p, String sheet, String column, int value) throws SQLException {
//        if (!getIfExist(p, sheet)) {
//            createColumnWithVip(p, sheet, value);
//            return;
//        }
//        String sql = "UPDATE " + sheet + " SET " + column + " = ? WHERE id = ?;";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, value);
//        statement.setObject(2, p.getName());
//        statement.executeUpdate();
//
//    }
//
//    public static int getIntValue(Player p, String sheet, String column) throws SQLException {
//        String sql = "SELECT " + column + " FROM " + sheet + " WHERE id = ? LIMIT 1;";
//        statement = Core.getConnection().prepareStatement(sql);
//        statement.setObject(1, p.getName());
//        ResultSet rs = statement.executeQuery();
//        while (rs.next()) {
//            if (rs.wasNull()) {
//                return 0;
//            }
//            return rs.getInt(1);
//        }
//        return 0;
//    }

}


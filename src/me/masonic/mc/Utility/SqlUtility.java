package me.masonic.mc.Utility;

import me.masonic.mc.Core;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * T Project
 * 2017-6-2-0002
 */
public class SqlUtility {
    private static PreparedStatement statement;

    public static boolean getIfExist(Player p, String sheet) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + sheet + " WHERE id = ? LIMIT 1;";
        statement = Core.getConnection().prepareStatement(sql);
        statement.setObject(1, p.getName());
        ResultSet rs = statement.executeQuery();
        if (rs.wasNull()) {
            return false;
        }
        while (rs.next()) {
            return rs.getInt(1) == 1;
        }
        return false;
    }

    public static void createColumn(Player p, String sheet) throws SQLException {
        String sql = "INSERT INTO " + sheet + " (id) VALUES (?``);";
        statement = Core.getConnection().prepareStatement(sql);
        statement.setObject(1, p.getName());
        statement.executeUpdate();
    }

    public static void uploadIntValue(Player p, String sheet, String column, int value) throws SQLException {
        String sql = "UPDATE " + sheet + " SET " + column + " = ? WHERE id = ?;";
        statement = Core.getConnection().prepareStatement(sql);
        statement.setObject(1, value);
        statement.setObject(2, p.getName());
        statement.executeUpdate();
    }

    public static int getIntValue(Player p, String sheet, String column) throws SQLException {
        String sql = "SELECT " + column + " FROM " + sheet + " WHERE id = ? LIMIT 1;";
        statement = Core.getConnection().prepareStatement(sql);
        statement.setObject(1, p.getName());
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            if (rs.wasNull()) {
                return 0;
            }
            return rs.getInt(1);

        }
        return 0;
    }
}


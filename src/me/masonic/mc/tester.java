package me.masonic.mc;

import java.sql.*;


public class tester {
    public static void main(String[] args) throws SQLException {
        String URL = "jdbc:mysql://127.0.0.1:3306/msky_core";
        String UNAME = "mc";
        String UPASSWORD = "492357816";

        try { //初始化驱动
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, UNAME, UPASSWORD);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'sign';");
            rs.next();
            System.out.println(rs.getString(1));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;

        }
    }
}
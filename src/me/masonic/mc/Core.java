package me.masonic.mc;

import me.masonic.mc.Function.InvIcon;
import me.masonic.mc.Function.Menu;
import me.masonic.mc.Function.Secure;
import me.masonic.mc.Hook.HookPapi;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Core extends JavaPlugin {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/gtm";
    private static final String UNAME = "mc";
    private static final String UPASSWORD = "492357816";
    private static Connection connection;

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public void onEnable() {

        new HookPapi(this).hook(); //Hook Papi

        getServer().getPluginManager().registerEvents(new Menu(), this);
        getServer().getPluginManager().registerEvents(new Secure(), this);
        getServer().getPluginManager().registerEvents(new InvIcon(), this);

        try { //初始化驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc驱动未启动");
            return;
        }
        try { //初始化数据库, catch exceptions
            connection = DriverManager.getConnection(URL, UNAME, UPASSWORD); //启动链接，链接名 conc

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {


    }

    public static void main(String[] args) {

    }
}

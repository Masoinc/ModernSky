package me.masonic.mc;

import me.masonic.mc.Cmd.MskyMsg;
import me.masonic.mc.Cmd.MskyDailyReward;
import me.masonic.mc.Cmd.MskyVip;
import me.masonic.mc.Function.*;
import me.masonic.mc.Hook.HookPapi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Mason Project
 * 2017-6-17-0017
 */
public class Core extends JavaPlugin {

    private static Core plugin;

    private static Economy economy = null;

    private static Connection connection;

    private Logger logger;

    private static final String PLUGIN_PREFIX = "§8[ §6ModernSky §8] §7";

    @Override
    public void onEnable() {

        plugin = this;
        this.logger = this.getLogger();

        new HookPapi(this).hook(); //Hook Papi

        registerEvents();
        registerCmd();
        registerEconomy();

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }

        loadFiles();

        registerSQL();

    }

    @Override
    public void onDisable() {
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static String getPrefix() {
        return PLUGIN_PREFIX;
    }

    public static Core getInstance() {
        return plugin;
    }


    public static Connection getConnection() {
        return connection;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Menu(), this);
        getServer().getPluginManager().registerEvents(new Secure(), this);
        getServer().getPluginManager().registerEvents(new InvIcon(), this);
        getServer().getPluginManager().registerEvents(new Vip(), this);
        getServer().getPluginManager().registerEvents(new MskyVip(), this);
        getServer().getPluginManager().registerEvents(new Ban(), this);
    }

    private void registerCmd() {
        this.getCommand("mskyvip").setExecutor(new MskyVip());
        this.getCommand("mskydr").setExecutor(new MskyDailyReward());
        this.getCommand("mskymsg").setExecutor(new MskyMsg());
    }

    private void registerEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
    }

    private void registerSQL() {

        String URL = this.getConfig().getString("SQL.URL");
        String UNAME = this.getConfig().getString("SQL.UNAME");
        String UPASSWORD = this.getConfig().getString("SQL.UPASSWORD");

        try { //初始化驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("jdbc driver unavailable!");
            return;
        }
        try { //初始化数据库, catch exceptions
            connection = DriverManager.getConnection(URL, UNAME, UPASSWORD); //启动链接，链接名 conc

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void loadFiles() {
        File config = new File(this.getDataFolder(), "config.yml");
        this.getConfig().options().copyDefaults(true);
        if (!config.exists()) {
            this.logger.info("创建配置文件中...");
            this.saveResource("config.yml", false);
        }
    }

    public static void main(String[] args) {

    }
}

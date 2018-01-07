package me.masonic.mc;

//import me.masonic.mc.CSCoreLibSetup.CSCoreLibLoader;

import me.masonic.mc.CSCoreLibSetup.CSCoreLibLoader;
import me.masonic.mc.Cmd.*;
import me.masonic.mc.Function.*;
import me.masonic.mc.Hook.HookPapi;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.black_ixx.playerpoints.PlayerPoints;

import java.io.File;
import java.sql.*;
import java.text.MessageFormat;
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

    private PlayerPoints playerPoints;

    @Override
    public void onEnable() {
        CSCoreLibLoader loader = new CSCoreLibLoader(this);
        if (loader.load()) {
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

            hookPlayerPoints();

            new Sidebar(this).sendSchedulely();

        }
    }

    private boolean hookPlayerPoints() {
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
        playerPoints = PlayerPoints.class.cast(plugin);
        return playerPoints != null;
    }

    public PlayerPoints getPlayerPoints() {
        return playerPoints;
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
        getServer().getPluginManager().registerEvents(new Ban(), this);
        getServer().getPluginManager().registerEvents(new Sidebar(this), this);
    }

    private void registerCmd() {
        this.getCommand("mskyvip").setExecutor(new MskyVip());
        this.getCommand("mskydr").setExecutor(new MskyDailyReward());
        this.getCommand("mskymsg").setExecutor(new MskyMsg());
        this.getCommand("mskysf").setExecutor(new MskySlimeFun());
        this.getCommand("mskysign").setExecutor(new MskySign());
        this.getCommand("mskybs").setExecutor(new MskyBackShop());
        this.getCommand("mskyaa").setExecutor(new MskyAdvancedAbility());
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
            initSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void initSQL() throws SQLException {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'sign';");
        boolean empty = true;
        while (rs.next()) {
            // ResultSet processing here
            empty = false;
        }

        if (empty) {
            // Empty result set
            Statement stmt2 = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}` JSON NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8";
            stmt2.addBatch(MessageFormat.format(sql, MskySign.getSheetName(), MskySign.getColUserName(), MskySign.getColUserUuid(), MskySign.getColSign()));
            sql = "alter table {0} add primary key({1});";
            stmt2.addBatch(String.format(sql, MskySign.getSheetName(), MskySign.getColUserUuid()));
            stmt2.executeBatch();
            stmt2.close();
        }

        ResultSet package_rs = stmt.executeQuery("SHOW TABLES LIKE '" + Exploration.getSheet() + "'");
        boolean package_empty = true;
        while (package_rs.next()) {
            package_empty = false;
        }
        if (package_empty) {
            Statement stmt3 = getConnection().createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `{0}` (`{1}` VARCHAR(32) NOT NULL,`{2}` VARCHAR(40) NOT NULL, `{3}`  NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8";
            stmt3.addBatch(MessageFormat.format(sql, Exploration.getSheet(), Exploration.getColUserName(), Exploration.getColUserUuid(), Exploration.getColExplore()));
            stmt3.addBatch("alter table " + Exploration.getSheet() + " add primary key(" + Exploration.getColUserUuid() + ");");
            stmt3.executeBatch();
            stmt3.close();
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

}

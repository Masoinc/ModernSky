package me.masonic.mc;

//import me.masonic.mc.CSCoreLibSetup.CSCoreLibLoader;

import com.zaxxer.hikari.HikariDataSource;
import me.masonic.mc.CSCoreLibSetup.CSCoreLibLoader;
import me.masonic.mc.Cmd.*;
import me.masonic.mc.Function.*;
import me.masonic.mc.Function.Package;
import me.masonic.mc.Hook.HookPapi;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private static PlayerPoints playerPoints;

    @Override
    public void onEnable() {
        CSCoreLibLoader loader = new CSCoreLibLoader(this);
        if (loader.load()) {
            plugin = this;

            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            loadFiles();

            this.logger = this.getLogger();

            new HookPapi(this).hook(); //Hook Papi

            registerEvents();
            registerCmd();
            registerEconomy();

            registerSQL();

            hookPlayerPoints();

            new Sidebar(this).sendSchedulely();

            activateConnection();
        }
    }

    private void hookPlayerPoints() {
        final Plugin plugin = this.getServer().getPluginManager().getPlugin("PlayerPoints");
        playerPoints = PlayerPoints.class.cast(plugin);
    }

    public static PlayerPoints getPlayerPoints() {
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

    public Core getCore() {
        return this;
    }

    public static Connection getConnection() {
        return connection;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Menu(), this);
        getServer().getPluginManager().registerEvents(new Secure(), this);
        getServer().getPluginManager().registerEvents(new InvIcon(), this);
//        getServer().getPluginManager().registerEvents(new Vip(), this);
//        getServer().getPluginManager().registerEvents(new MskyVip(), this);
        getServer().getPluginManager().registerEvents(new Ban(), this);
        getServer().getPluginManager().registerEvents(new Ban(), this);
        getServer().getPluginManager().registerEvents(new Sidebar(this), this);
        getServer().getPluginManager().registerEvents(new Package(this), this);
    }

    private void registerCmd() {
//        this.getCommand("mskyvip").setExecutor(new MskyVip());
//        this.getCommand("mskydr").setExecutor(new MskyDailyReward());
        this.getCommand("mskymsg").setExecutor(new MskyMsg());
        this.getCommand("mskysf").setExecutor(new MskySlimeFun());
        this.getCommand("mskysign").setExecutor(new MskySign());
        this.getCommand("mskybs").setExecutor(new MskyBackShop());
        this.getCommand("mskyaa").setExecutor(new MskyAdvancedAbility());
        this.getCommand("mskypac").setExecutor(new MskyPackage());
    }

    private void registerEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
    }

    private void registerSQL() {

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(this.getConfig().getString("SQL.connection.URL"));
        ds.setUsername(this.getConfig().getString("SQL.connection.UNAME"));
        ds.setPassword(this.getConfig().getString("SQL.connection.UPASSWORD"));

        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        try {
            connection = ds.getConnection();
            initSQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        activateConnection();
//        String URL = this.getConfig().getString("SQL.URL");
//        String UNAME = this.getConfig().getString("SQL.UNAME");
//        String UPASSWORD = this.getConfig().getString("SQL.UPASSWORD");
//
//        try { //初始化驱动
//            Class.forName("com.mysql.jdbc.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            System.err.println("jdbc driver unavailable!");
//            return;
//        }
//        try { //初始化数据库, catch exceptions
//            connection = DriverManager.getConnection(URL, UNAME, UPASSWORD); //启动链接，链接名 conc
//            initSQL();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

    }

    private void initSQL() throws SQLException {
        createTables(Sign.getSheetName(), Sign.getInitQuery(), Sign.getColUserUuid());
        createTables(Package.getSheetName(), Package.getInitQuery(), Package.getColUserUuid());
        createTables(Exploration.getSheetName(), Exploration.getInitQuery(), Exploration.getColUserUuid());
    }

    private static void createTables(String sheet, String init, String col_uuid) throws SQLException {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + sheet + "'");
        boolean empty = true;
        while (rs.next()) {
            empty = false;
        }

        if (empty) {
            Core.getInstance().logger.info("Sheet '" + sheet + "' is not exist, creating...");
            stmt.addBatch(init);
            stmt.addBatch(MessageFormat.format("alter table {0} add primary key({1});", sheet, col_uuid));
            stmt.executeBatch();
            stmt.close();
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

    private void activateConnection() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            logger.info("连接已刷新");
            try {
                Statement stmt = getConnection().createStatement();
                stmt.executeQuery("SHOW TABLES LIKE 'sign';");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0, 29 * 20);
        // HikariCP 默认空闲30秒后关闭连接
    }
}

package me.masonic.mc;

import com.zaxxer.hikari.HikariDataSource;
import me.masonic.mc.CSCoreLibSetup.CSCoreLibLoader;
import me.masonic.mc.Cmd.*;
import me.masonic.mc.Function.*;
import me.masonic.mc.Function.Package;
import me.masonic.mc.Function.Privilege.ExpPriviledge;
import me.masonic.mc.Function.Privilege.PrivilegeManager;
import me.masonic.mc.Function.Vitality.Vitality;
import me.masonic.mc.Hook.HookPapi;
import me.masonic.mc.Utility.SqlUtil;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;

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

    private static CacheManager cacheManager;

    public final static String VERSION = "0.2.8";

    public final static Boolean ALLFIELD_DEBUG = true;
    public final static Boolean SQL_DEBUG = true;

    @Override
    public void onEnable() {
        CSCoreLibLoader loader = new CSCoreLibLoader(this);
        if (loader.load()) {
            plugin = this;

            this.logger = this.getLogger();

            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            loadFiles();

            new HookPapi(this).hook();

            registerEvents();
            registerCmd();
            registerEconomy();
            registerSQL();
            registerCache();

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
//        logger.info("All caches are cleared.");
//        ds.close();
        cacheManager.removeCache("repository_cache");
        cacheManager.close();
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
//        getServer().getPluginManager().registerEvents(new Vip(), this);
//        getServer().getPluginManager().registerEvents(new MskyVip(), this);
        getServer().getPluginManager().registerEvents(new Ban(), this);
        getServer().getPluginManager().registerEvents(new Sidebar(this), this);
        getServer().getPluginManager().registerEvents(new Package(), this);
        getServer().getPluginManager().registerEvents(new PrivilegeManager(), this);
        getServer().getPluginManager().registerEvents(new ExpPriviledge(), this);
        getServer().getPluginManager().registerEvents(new Message(), this);
        getServer().getPluginManager().registerEvents(Vitality.LISTENER, this);
        getServer().getPluginManager().registerEvents(Online.LISTENER, this);
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
        this.getCommand("mskypri").setExecutor(new MskyPrivilege());
        this.getCommand("mskycore").setExecutor(new MskyCore());
    }

    private void registerEconomy() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
    }

    private void registerSQL() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(this.getConfig().getString("SQL.connection.URL") + "?verifyServerCertificate=false&useSSL=false");
        ds.setUsername(this.getConfig().getString("SQL.connection.UNAME"));
        ds.setPassword(this.getConfig().getString("SQL.connection.UPASSWORD"));

        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        try {
            connection = ds.getConnection();
            createTables(Sign.getSheetName(), Sign.getInitQuery(), Sign.getColUserUuid());
            createTables(Package.getSheetName(), Package.getInitQuery(), Package.getColUserUuid());
            createTables(Exploration.getSheetName(), Exploration.getInitQuery(), Exploration.getColUserUuid());
            createTables(PrivilegeManager.getSheetName(), PrivilegeManager.getInitQuery(), PrivilegeManager.getColUserUuid());
            createTables(Repository.SHEET, Repository.INIT_QUERY, Repository.COL_USER_UUID);
            createTables(Vitality.SHEET, Vitality.INIT_QUERY, Vitality.COL_USER_UUID);
            createTables(Online.SHEET, Online.INIT_QUERY, Online.COL_USER_UUID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    private void registerMongo() {
//        final Morphia morphia = new Morphia();
//
//        // tell Morphia where to find your classes
//        // can be called multiple times with different packages or classes
//        morphia.mapPackage("org.mongodb.morphia.example");
//
//        client = new MongoClient("localhost", 27017);
//        // create the Datastore connecting to the default port on the local host
//        final Datastore datastore = morphia.createDatastore(client, "ModernSky");
//        datastore.ensureIndexes();
//
//    }

    private static void registerCache() {
//        this.saveResource("ehcache.xml", true);
//        URL configurl = null;
//        try {
//            configurl = new File(this.getDataFolder(), "ehcache.xml").toURI().toURL();
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        assert configurl != null;
//        Configuration xmlConfig = new XmlConfiguration(configurl);
//        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);

        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .build();
        cacheManager.init();
        Repository.initCache();
        Vitality.initCache();
        Online.initCache();

    }

    public static CacheManager getCacheManager() {
        return cacheManager;
    }

    private static void createTables(String sheet, String init, String col_uuid) throws SQLException {
        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(MessageFormat.format("SHOW TABLES LIKE ''{0}''", sheet));
        boolean empty = true;
        while (rs.next()) {
            empty = false;
        }

        if (empty) {
            Core.getInstance().logger.info(MessageFormat.format("Sheet ''{0}'' is not exist, creating...", sheet));
            SqlUtil.update(init);
            SqlUtil.update(MessageFormat.format("alter table {0} add primary key({1});", sheet, col_uuid));
        } else {
            Core.getInstance().logger.info(MessageFormat.format("Sheet ''{0}'' exists, skipped", sheet));
        }
    }

    private void loadFiles() {
        File config = new File(this.getDataFolder(), "config.yml");
        this.getConfig().options().copyDefaults(true);
        if (!config.exists()) {
            this.logger.info("creating configs");
            this.saveResource("config.yml", true);
        }
    }

    private void activateConnection() {
        new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    Statement stmt = getConnection().createStatement();
                    stmt.executeQuery("SHOW TABLES LIKE 'sign';");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }.runTaskTimerAsynchronously(this, 0, 580L);
//            logger.info("Connection expired");
        // HikariCP 默认空闲30秒后关闭连接
    }
}

package agp.andwhat5.storage.sql;

import agp.andwhat5.Utils;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.config.structs.DataStruc;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.TimeUnit;

public class MySQLProvider extends SQLDatabase {

    private HikariDataSource hikari;

    public MySQLProvider(String gymTable, String badgeTable) {
        super(gymTable, badgeTable);
        try {
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        HikariConfig config = new HikariConfig();

        String address = AGPConfig.Storage.MysqlAddress;
        int port = AGPConfig.Storage.MysqlPort;

        String dbName = AGPConfig.Storage.MysqlDatabaseName;
        String username = AGPConfig.Storage.MysqlUsername;
        String password = AGPConfig.Storage.MysqlPassword;

        config.setMaximumPoolSize(10);

        config.setPoolName("AGP");
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", dbName);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10)); // 10000
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(5)); // 5000
        config.setValidationTimeout(TimeUnit.SECONDS.toMillis(3)); // 3000
        config.setInitializationFailTimeout(1);
        config.setConnectionTestQuery("/* AGP ping */ SELECT 1");

        this.hikari = new HikariDataSource(config);

        createTables();
        updateTables();
        DataStruc.gcon.GymData = getGyms();
        Utils.sortGyms();
        DataStruc.gcon.PlayerData = getPlayerData();

    }

    @Override
    HikariDataSource getHikari() {
        return this.hikari;
    }
}

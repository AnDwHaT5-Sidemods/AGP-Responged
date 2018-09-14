package agp.andwhat5.storage.sql;

import agp.andwhat5.AGP;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.storage.Dependencies;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class H2Provider extends SQLDatabase {

    private static Method ADD_URL_METHOD;

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private HikariDataSource hikari;

    public H2Provider(String gymTable, String badgeTable) {
        super(gymTable, badgeTable);
        try {
            loadJar(downloadDependencies(), Dependencies.getTestClass("H2_DRIVER"));
            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File downloadDependencies() throws Exception {
        String name = Dependencies.H2_DRIVER.name().toLowerCase() + "-" + Dependencies.getVersion("H2_DRIVER") + ".jar";

        File db = new File("database/h2-1.3.173.jar");
        if (db.exists()) {
            return db;
        }

        File dir = new File("config/storage/");
        dir.mkdirs();
        File file = new File("config/storage/", name);
        if (file.exists()) {
            return file;
        }

        // H2 Driver Jar doesn't exist, let's download one
        URL url = new URL(Dependencies.getUrl("H2_DRIVER"));

        System.out.println("H2 Dependency '" + name + "' not found, attempting to download it..");
        try (InputStream in = url.openStream()) {
            Files.copy(in, file.toPath());
        }

        if (!file.exists()) {
            throw new IllegalStateException("File not present. - " + file.toString());
        }

        System.out.println("H2 Dependency successfully downloaded!");
        return file;
    }

    private static void loadJar(File file, String baseClass) throws Exception {
        URLClassLoader classLoader = (URLClassLoader) AGP.class.getClassLoader();

        ADD_URL_METHOD.invoke(classLoader, file.toURI().toURL());
        classLoader.loadClass(baseClass).newInstance();
    }

    @Override
    public void init() {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(10);
        config.setPoolName("AGP");
        config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        config.addDataSourceProperty("URL", "jdbc:h2:file:" + AGP.getInstance().getBase() + "/data/storage");
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

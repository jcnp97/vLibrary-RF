package asia.virtualmc.vLibrary.utilities.files;

import asia.virtualmc.vLibrary.helpers.DriverShim;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteUtils {
    private static final Set<Connection> activeConnections = ConcurrentHashMap.newKeySet();
    private static final String SQLITE_VERSION = "3.49.1.0";
    private static final String SQLITE_URL = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/" +
            SQLITE_VERSION + "/sqlite-jdbc-" + SQLITE_VERSION + ".jar";

    /**
     * Initialize the SQLite driver by downloading the dependency at runtime
     * @param plugin the plugin instance
     * @return true if initialization was successful
     */
    public static boolean initialize(Plugin plugin) {
        try {
            File libFolder = new File(plugin.getDataFolder(), "lib");
            if (!libFolder.exists() && !libFolder.mkdirs()) {
                ConsoleUtils.severe("Failed to create lib directory for SQLite JDBC driver");
                return false;
            }

            File sqliteJar = new File(libFolder, "sqlite-jdbc-" + SQLITE_VERSION + ".jar");

            // Download SQLite JDBC if it doesn't exist
            if (!sqliteJar.exists()) {
                ConsoleUtils.info("Downloading SQLite JDBC driver...");
                try {
                    URL url = new URL(SQLITE_URL);
                    Files.copy(url.openStream(), sqliteJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    ConsoleUtils.info("SQLite JDBC driver downloaded successfully.");
                } catch (IOException e) {
                    ConsoleUtils.severe("Failed to download SQLite JDBC driver: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            // Create class loader and load the SQLite driver
            URLClassLoader sqliteClassLoader = new URLClassLoader(
                    new URL[]{sqliteJar.toURI().toURL()},
                    SQLiteUtils.class.getClassLoader()
            );

            // Load SQLite driver via reflection (this *registers* it with DriverManager)
            Class<?> driverClass = Class.forName("org.sqlite.JDBC", true, sqliteClassLoader);
            Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverShim(driverInstance));

            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Failed to initialize SQLite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates (if needed) and opens a SQLite database file under the plugin's data folder.
     * Uses WAL journal mode and sets a 5s busy timeout to reduce locking errors.
     *
     * @param plugin       the plugin instance
     * @param relativePath path relative to plugin's data folder (e.g. "data/db")
     * @param fileName     database file name (e.g. "players.db")
     * @return a live Connection, or null if creation/connection failed
     */
    public static Connection connect(Plugin plugin, String relativePath, String fileName) {
        File dataDir = plugin.getDataFolder();
        File dir = new File(dataDir, relativePath);
        if (!dir.exists() && !dir.mkdirs()) {
            ConsoleUtils.severe("Failed to create directories for SQLite at " + dir.getAbsolutePath());
            return null;
        }

        File dbFile = new File(dir, fileName);
        try {
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                ConsoleUtils.severe("Failed to create SQLite file at " + dbFile.getAbsolutePath());
                return null;
            }

            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath() + "?journal_mode=WAL&busy_timeout=5000";
            Connection connection = DriverManager.getConnection(url);

            activeConnections.add(connection);
            return connection;

        } catch (IOException e) {
            ConsoleUtils.severe("Could not connect to SQLite (" + fileName + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkpoint(Connection connection) {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA wal_checkpoint(FULL);");
        } catch (SQLException e) {
            ConsoleUtils.severe("Failed to checkpoint SQLite WAL: " + e.getMessage());
        }
    }

    public static void checkpointAll() {
        for (Connection conn : activeConnections) {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("PRAGMA wal_checkpoint(FULL);");
                } catch (SQLException e) {
                    ConsoleUtils.severe("Failed to checkpoint SQLite: " + e.getMessage());
                }
            }
        }
    }

    public static void closeAll() {
        for (Connection conn : activeConnections) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    ConsoleUtils.severe("Failed to close SQLite connection: " + e.getMessage());
                }
            }
        }
        activeConnections.clear();
    }
}

package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {
    private static HikariDataSource hikariDataSource;
    private static Database database;
    private record Database(String host, int port, String dbName, String user, String password) {}

    public static void initialize(@NotNull Plugin plugin) {
        getConfig();

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + database.host + ":" + database.port + "/" + database.dbName);
            config.setUsername(database.user);
            config.setPassword(database.password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(10000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            hikariDataSource = new HikariDataSource(config);

            try (Connection connection = hikariDataSource.getConnection()) {
                if (connection != null && !connection.isClosed()) {
                    ConsoleUtils.info("Successfully connected to the MySQL database.");
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Failed to connect to database: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        } catch (Exception e) {
            ConsoleUtils.severe("Error during database setup: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    private static void getConfig() {
        Section section = YAMLUtils.getSection(VLibrary.getInstance(), "database.yml", "mysql");
        if (section == null) {
            ConsoleUtils.severe("Couldn't find/read database.yml!");
            return;
        }

        try {
            String host = section.getString("host", "localhost");
            int port = section.getInt("port", 3306);
            String dbName = section.getString("database", "minecraft");
            String user = section.getString("username", "root");
            String pass = section.getString("password", "");

            database = new Database(host, port, dbName, user, pass);
        } catch (Exception e) {
            ConsoleUtils.severe("Error during database setup: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public static void closeConnection() {
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }
}
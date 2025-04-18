package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionSource {
    private final VLibrary vlib;
    private static HikariDataSource hikariDataSource;
    private Database database;
    private record Database(String host, int port, String dbName, String user, String password) {}

    public ConnectionSource(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
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
                    ConsoleUtils.sendConsoleMessage("Successfully connected to the MySQL database.");
                }
            }
        } catch (SQLException e) {
            vlib.getLogger().severe("Failed to connect to database: " + e.getMessage());
        } catch (Exception e) {
            vlib.getLogger().severe("Error during database setup: " + e.getMessage());
        }
    }

    private void getConfig() {
        Section section = YAMLUtils.getFileSection(vlib, "database.yml", "mysql");
        if (section == null) {
            vlib.getLogger().severe("Couldn't find/read database.yml!");
            return;
        }

        try {
            String host = section.getString("host", "localhost");
            int port = section.getInt("port", 3306);
            String dbName = section.getString("host", "minecraft");
            String user = section.getString("username", "root");
            String pass = section.getString("password", "");

            this.database = new Database(host, port, dbName, user, pass);
        } catch (Exception e) {
            vlib.getLogger().severe("Error during database setup: " + e.getMessage());
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

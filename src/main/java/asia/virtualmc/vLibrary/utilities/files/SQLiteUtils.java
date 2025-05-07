package asia.virtualmc.vLibrary.utilities.files;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * Utility methods for working with an embedded SQLite database within a Bukkit plugin.
 */
public class SQLiteUtils {

    /**
     * Creates (if needed) and opens a SQLite database file under the plugin's data folder.
     * Uses WAL journal mode and sets a 5s busy timeout to reduce locking errors.
     *
     * @param plugin       the plugin instance
     * @param relativePath path relative to plugin's data folder (e.g. "data/db")
     * @param fileName     database file name (e.g. "players.db")
     * @return a live Connection, or null if creation/connection failed
     */
    public static Connection createDatabase(Plugin plugin, String relativePath, String fileName) {
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
            return DriverManager.getConnection(url);

        } catch (SQLException | IOException e) {
            ConsoleUtils.severe("Could not connect to SQLite (" + fileName + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a table (if not exists) with a primary UUID column plus additional columns.
     * @param connection live SQLite connection
     * @param tableName  name of the table
     * @param keys       list of column names (non-primary)
     * @param types      corresponding SQL types for each key
     */
    public static void createTable(Connection connection, String tableName, List<String> keys, List<String> types) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot create table " + tableName);
            return;
        }
        if (keys.size() != types.size()) {
            ConsoleUtils.severe("Number of keys and data types provided does not match for " + tableName);
            return;
        }

        StringBuilder schema = new StringBuilder();
        schema.append("\"uuid\" CHAR(36) PRIMARY KEY");
        for (int i = 0; i < keys.size(); i++) {
            schema.append(", \"").append(keys.get(i).trim()).append("\" ")
                    .append(types.get(i).trim());
        }

        String sql = "CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (" + schema + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not create table (" + tableName + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new row or updates existing one by UUID using SQLite UPSERT.
     * @param connection live SQLite connection
     * @param tableName  name of the table
     * @param uuid       the UUID key
     * @param keys       list of non-primary column names
     * @param values     corresponding values to insert/update
     */
    public static void addValue(Connection connection,
                                String tableName,
                                String uuid,
                                List<String> keys,
                                List<Object> values) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot add/update values for " + tableName);
            return;
        }
        if (keys.size() != values.size()) {
            ConsoleUtils.severe("Number of keys and values provided does not match for " + tableName);
            return;
        }

        // Build column lists and placeholders
        StringBuilder cols = new StringBuilder("\"uuid\"");
        StringBuilder phs = new StringBuilder("?");
        for (String key : keys) {
            cols.append(", \"").append(key.trim()).append("\"");
            phs.append(", ?");
        }

        // Build UPSERT update assignments
        StringBuilder updates = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            if (i > 0) updates.append(", ");
            String k = keys.get(i).trim();
            updates.append("\"").append(k).append("\" = excluded.\"").append(k).append("\"");
        }

        String sql = "INSERT INTO \"" + tableName + "\" (" + cols + ") VALUES (" + phs + ") " +
                "ON CONFLICT(\"uuid\") DO UPDATE SET " + updates + ";";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            for (int i = 0; i < values.size(); i++) {
                ps.setObject(i + 2, values.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not insert/update values into table (" + tableName + "): " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a String value by UUID. Returns null if missing or on error.
     */
    public static String getString(Connection connection,
                                   String tableName,
                                   String key,
                                   String uuid) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot getString for " + tableName);
            return null;
        }
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve String from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves an Integer value by UUID. Returns defaultValue if missing or on error.
     */
    public static Integer getInt(Connection connection,
                                 String tableName,
                                 String key,
                                 String uuid,
                                 Integer defaultValue) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot getInt for " + tableName);
            return defaultValue;
        }
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !rs.wasNull()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve int from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Retrieves a Double value by UUID. Returns defaultValue if missing or on error.
     */
    public static Double getDouble(Connection connection,
                                   String tableName,
                                   String key,
                                   String uuid,
                                   Double defaultValue) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot getDouble for " + tableName);
            return defaultValue;
        }
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !rs.wasNull()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve double from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Retrieves a Long value by UUID. Returns defaultValue if missing or on error.
     */
    public static Long getLong(Connection connection,
                               String tableName,
                               String key,
                               String uuid,
                               Long defaultValue) {
        if (connection == null) {
            ConsoleUtils.severe("Connection is null; cannot getLong for " + tableName);
            return defaultValue;
        }
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && !rs.wasNull()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve long from " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
        return defaultValue;
    }
}
package asia.virtualmc.vLibrary.utilities.files;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SQLiteUtils {

    public static Connection createDatabase(Plugin plugin, String relativePath, String fileName) {
        try {
            File dbFile = new File(plugin.getDataFolder(), fileName);
            if (!dbFile.exists()) {
                plugin.getDataFolder().mkdirs();
                dbFile.createNewFile();
            }
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            return DriverManager.getConnection(url);

        } catch (SQLException | IOException e) {
            ConsoleUtils.severe("Could not connect to SQLite (" + fileName + "): " + e.getMessage());
        }

        return null;
    }

    public static void createTable(Connection connection, String tableName, List<String> keys, List<String> types) {
        if (keys.size() != types.size()) {
            ConsoleUtils.severe("Number of keys and data types provided does not match for " + tableName);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            sb.append("\"").append(keys.get(i).trim()).append("\" ");
            sb.append(types.get(i));

            if (i < keys.size() - 1) {
                sb.append(", ");
            }
        }

        String sql = "CREATE TABLE IF NOT EXISTS \"" + tableName + "\" (" +
                "\"uuid\" CHAR(36) PRIMARY KEY, " +
                sb.toString() + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not create table (" + tableName + "): " + e.getMessage());
        }
    }

    public static void addValue(Connection connection, String tableName, List<String> keys, List<Object> values) {
        if (keys.size() != values.size()) {
            ConsoleUtils.severe("Number of keys and values provided does not match for " + tableName);
            return;
        }

        // Build columns string (quote identifiers)
        StringBuilder columns = new StringBuilder("\"uuid\"");
        for (String key : keys) {
            columns.append(", \"").append(key.trim()).append("\"");
        }

        // Build placeholders (?, ?, ...)
        StringBuilder placeholders = new StringBuilder("?");
        for (int i = 0; i < keys.size(); i++) {
            placeholders.append(", ?");
        }

        String sql = "INSERT INTO \"" + tableName + "\" (" + columns + ") VALUES (" + placeholders + ");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, UUID.randomUUID().toString()); // set uuid

            // set remaining values
            for (int i = 0; i < values.size(); i++) {
                preparedStatement.setObject(i + 2, values.get(i));
            }

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not insert value into table (" + tableName + "): " + e.getMessage());
        }
    }

    public static String getString(Connection connection, String tableName, String key, String uuid) {
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve value from table (" + tableName + "): " + e.getMessage());
        }
        return null;
    }

    public static int getInt(Connection connection, String tableName, String key, String uuid) {
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve value from table (" + tableName + "): " + e.getMessage());
        }
        return 0;
    }


    public static double getDouble(Connection connection, String tableName, String key, String uuid) {
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve value from table (" + tableName + "): " + e.getMessage());
        }
        return 0.0;
    }


    public static long getLong(Connection connection, String tableName, String key, String uuid) {
        String sql = "SELECT \"" + key + "\" FROM \"" + tableName + "\" WHERE \"uuid\" = ? LIMIT 1;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Could not retrieve value from table (" + tableName + "): " + e.getMessage());
        }
        return 0L;
    }
}

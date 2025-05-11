package asia.virtualmc.vLibrary.storage.core;

import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IntegerKeyUtils {

    /**
     * Creates the necessary tables for storing stat definitions and player-specific stat data.
     *
     * @param dataList  A list of data names to insert into the data definition table.
     * @param tableName The base name of the table (e.g., "skills", "kills").
     * @param prefix    A log prefix used for logging errors or debug info.
     */
    public static void createTable(@NotNull List<String> dataList, @NotNull String tableName, String prefix) {
        try (Connection conn = MySQLConnection.getConnection()) {

            // Create data definition table
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                            "data_id INT NOT NULL AUTO_INCREMENT," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "PRIMARY KEY (data_id)," +
                            "UNIQUE KEY (data_name)" +
                            ")"
            );

            // Create player data table with composite foreign keys (now referencing vlib_players)
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + tableName + "_data (" +
                            "player_id INT NOT NULL," +
                            "data_id INT NOT NULL," +
                            "amount INT DEFAULT 0," +
                            "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_id, data_id)," +
                            "FOREIGN KEY (player_id) REFERENCES vlib_players(playerID) ON DELETE CASCADE," +
                            "FOREIGN KEY (data_id) REFERENCES " + tableName + "(data_id) ON DELETE CASCADE," +
                            "INDEX idx_player_id (player_id)" +
                            ")"
            );

            // Insert new stat types if they don't exist
            String insertQuery = "INSERT IGNORE INTO " + tableName + " (data_name) VALUES (?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                for (String data : dataList) {
                    insertStmt.setString(1, data);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to create " + tableName + " tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves or updates stat values for a specific player in the database.
     *
     * @param uuid       The UUID of the player whose data is being saved.
     * @param tableName  The base name of the table (e.g., "skills", "kills").
     * @param playerData A map where keys are stat data IDs and values are the corresponding amounts.
     * @param prefix     A log prefix used for logging errors or debug info.
     */
    public static void savePlayerData(@NotNull UUID uuid, @NotNull String tableName,
                                      @NotNull Map<Integer, Integer> playerData, String prefix) {
        if (playerData.isEmpty()) {
            ConsoleUtils.warning(prefix, "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + tableName + "_data (player_id, data_id, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.getPlayerID(uuid);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<Integer, Integer> entry : playerData.entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, entry.getKey());
                    ps.setInt(3, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to update data on " + tableName + " for player " +
                    uuid + ": " + e.getMessage());
        }
    }

    /**
     * Saves or updates stat data for all players provided in the map.
     *
     * @param tableName     The base name of the table (e.g., "skills", "kills").
     * @param allPlayerData A map where the key is the player's UUID and the value is another map
     *                      containing stat data ID and its amount.
     * @param prefix        A log prefix used for logging errors or debug info.
     */
    public static void saveAllData(@NotNull String tableName,
                                   @NotNull Map<UUID, Map<Integer, Integer>> allPlayerData,
                                   String prefix) {
        if (allPlayerData.isEmpty()) return;

        String sql = "INSERT INTO " + tableName + "_data (player_id, data_id, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<Integer, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDUtils.getPlayerID(playerEntry.getKey());
                for (Map.Entry<Integer, Integer> dataEntry : playerEntry.getValue().entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, dataEntry.getKey());
                    ps.setInt(3, dataEntry.getValue());
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            ConsoleUtils.severe(prefix,
                    "Failed to save all data for " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Creates a new stat data row for a player, inserting default values (0) for all known stats
     * that the player doesn't yet have records for.
     *
     * @param uuid      The UUID of the player.
     * @param tableName The base name of the table (e.g., "skills", "kills").
     * @param prefix    A log prefix used for logging errors or debug info.
     */
    public static void createNewPlayerData(@NotNull UUID uuid, @NotNull String tableName, String prefix) {
        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.getPlayerID(uuid);

            String insertQuery =
                    "INSERT INTO " + tableName + "_data (player_id, data_id, amount) " +
                            "SELECT ?, data_id, 0 FROM " + tableName + " t " +
                            "WHERE NOT EXISTS (SELECT 1 FROM " + tableName + "_data " +
                            "WHERE player_id = ? AND data_id = t.data_id)";

            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, playerId);
                ps.setInt(2, playerId);
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to create new player data on " + tableName +
                    " for " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Loads all stat data for a specific player from the database. If no data exists for the player,
     * it creates a new entry with default values.
     *
     * @param uuid      The UUID of the player.
     * @param tableName The base name of the table (e.g., "skills", "kills").
     * @param prefix    A log prefix used for logging errors or debug info.
     * @return A map where the key is the stat data ID and the value is the recorded amount.
     */
    public static ConcurrentHashMap<Integer, Integer> loadPlayerData(@NotNull UUID uuid,
                                                                     @NotNull String tableName,
                                                                     String prefix) {

        ConcurrentHashMap<Integer, Integer> playerDataMap = new ConcurrentHashMap<>();

        try (Connection conn = MySQLConnection.getConnection()) {
            // Retrieve playerID using the external vlib_players table
            int playerId = PlayerIDUtils.getPlayerID(uuid);

            // Check if player data exists
            String countQuery = "SELECT COUNT(*) FROM " + tableName + "_data WHERE player_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(countQuery)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        // Create new player data if it doesn't exist
                        createNewPlayerData(uuid, tableName, prefix);
                    }
                }
            }

            // Load the data
            String loadQuery = "SELECT d.data_id, d.amount FROM " + tableName + "_data d " +
                    "WHERE d.player_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(loadQuery)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int dataId = rs.getInt("data_id");
                        int amount = rs.getInt("amount");
                        playerDataMap.put(dataId, amount);
                    }
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to load data from " + tableName + " for player " +
                    uuid + ": " + e.getMessage());
        }

        return playerDataMap;
    }
}

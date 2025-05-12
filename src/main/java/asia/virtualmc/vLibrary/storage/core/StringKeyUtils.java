package asia.virtualmc.vLibrary.storage.core;

import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StringKeyUtils {

    /**
     * Creates the necessary tables for storing stat definitions and player-specific stat data.
     *
     * @param tableName The base name of the table
     * @param prefix    A log prefix used for logging errors or debug info.
     */
    public static void createTable(@NotNull String tableName,
                                   String prefix) {
        try (Connection conn = MySQLConnection.getConnection()) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + tableName + "_data (" +
                            "player_id INT NOT NULL," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "amount INT DEFAULT 0," +
                            "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_id, data_name)," +
                            "FOREIGN KEY (player_id) REFERENCES vlib_players(playerID) ON DELETE CASCADE," +
                            "INDEX idx_player_id (player_id)" +
                            ")"
            );

        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to create " + tableName + " tables: " + e.getMessage());
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
                                      @NotNull Map<String, Integer> playerData, String prefix) {
        if (playerData.isEmpty()) {
            ConsoleUtils.warning(prefix, "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + tableName + "_data (player_id, data_name, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.getConnection()) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.getPlayerID(uuid);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<String, Integer> entry : playerData.entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setString(2, entry.getKey());
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
                                   @NotNull Map<UUID, Map<String, Integer>> allPlayerData,
                                   String prefix) {
        if (allPlayerData.isEmpty()) return;

        String sql = "INSERT INTO " + tableName + "_data (player_id, data_name, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<String, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDUtils.getPlayerID(playerEntry.getKey());
                for (Map.Entry<String, Integer> dataEntry : playerEntry.getValue().entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setString(2, dataEntry.getKey());
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
     * Loads all stored data for a player. If no data is found, returns a map with expected keys filled with 0.
     *
     * @param uuid         The player's UUID.
     * @param tableName    The base table name.
     * @param dataNames     The list of data names.
     * @param prefix       A log prefix for errors/debug.
     * @return A map of data_name → amount. If no data found, returns a map of expected keys with 0.
     */
    public static ConcurrentHashMap<String, Integer> loadPlayerData(@NotNull UUID uuid,
                                                                    @NotNull String tableName,
                                                                    @NotNull Set<String> dataNames,
                                                                    String prefix) {

        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        String sql = "SELECT data_name, amount FROM " + tableName + "_data WHERE player_id = ?";

        try (Connection conn = MySQLConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int playerId = PlayerIDUtils.getPlayerID(uuid);
            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("data_name"), rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe(prefix,
                    "Failed to load data for player " + uuid + " from " + tableName + ": " + e.getMessage());
        }

        // If no data was found, pre-fill with expected keys at 0.
        if (result.isEmpty()) {
            for (String key : dataNames) {
                result.put(key, 0);
            }
        }

        return result;
    }
}
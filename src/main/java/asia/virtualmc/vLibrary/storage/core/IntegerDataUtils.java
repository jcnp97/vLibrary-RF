package asia.virtualmc.vLibrary.storage.core;

import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDManager;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class IntegerDataUtils {
    private static final int BATCH_SIZE = 1000;

    /**
     * Asynchronously creates two tables: one for storing stat definitions and one for player-specific stat data.
     * Also inserts the given stat types into the stat definition table if they don't already exist.
     *
     * @param statList  List of stat names to define in the table.
     * @param tableName Name of the stat table (without "_data" suffix).
     * @param prefix    Plugin prefix for logging purposes.
     */
    public static CompletableFuture<Void> createTable(@NotNull List<String> statList, @NotNull String tableName, String prefix) {
        return CompletableFuture.runAsync(() -> {
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
                    for (String data : statList) {
                        insertStmt.setString(1, data);
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(prefix + "Failed to create " + tableName + " tables: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Asynchronously saves a single player's stat data into the database. Replaces existing values if keys match.
     *
     * @param uuid       The UUID of the player whose data is being saved.
     * @param tableName  Name of the table (without "_data" suffix).
     * @param playerData A map of data_id to amount representing player statistics.
     * @param prefix     Plugin prefix for logging purposes.
     */
    public static CompletableFuture<Void> savePlayerData(@NotNull UUID uuid, @NotNull String tableName,
                                                         @NotNull Map<Integer, Integer> playerData, String prefix) {
        return CompletableFuture.runAsync(() -> {
            if (playerData.isEmpty()) {
                ConsoleUtils.sendSevereMessage(prefix + "Attempted to update data for player " +
                        uuid + " but the provided data map is empty.");
                return;
            }

            String sql = "INSERT INTO " + tableName + "_data (player_id, data_id, amount) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);
                int playerId = PlayerIDManager.getPlayerID(uuid);

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
                ConsoleUtils.sendSevereMessage(prefix + "Failed to update data on " + tableName + " for player " +
                        uuid + ": " + e.getMessage());
            }
        });
    }

    /**
     * Asynchronously saves all player stat data in bulk to the database using batch execution.
     * This method is optimized for large-scale operations, committing every {@code BATCH_SIZE} entries.
     *
     * @param tableName  Name of the table (without "_data" suffix).
     * @param playerData A nested map where the outer key is UUID and inner map is data_id to amount.
     * @param prefix     Plugin prefix for logging purposes.
     */
    public static CompletableFuture<Void> saveAllData(@NotNull String tableName,
                                                      @NotNull ConcurrentHashMap<UUID, ConcurrentHashMap<Integer, Integer>> playerData,
                                                      String prefix) {
        return CompletableFuture.runAsync(() -> {
            if (playerData.isEmpty()) {
                return;
            }

            String sql = "INSERT INTO " + tableName + "_data (player_id, data_id, amount) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    int batchCount = 0;

                    for (Map.Entry<UUID, ConcurrentHashMap<Integer, Integer>> entry : playerData.entrySet()) {
                        int playerId = PlayerIDManager.getPlayerID(entry.getKey());

                        for (Map.Entry<Integer, Integer> dataEntry : entry.getValue().entrySet()) {
                            ps.setInt(1, playerId);
                            ps.setInt(2, dataEntry.getKey());
                            ps.setInt(3, dataEntry.getValue());
                            ps.addBatch();

                            batchCount++;
                            if (batchCount >= BATCH_SIZE) {
                                ps.executeBatch();
                                batchCount = 0;
                            }
                        }
                    }

                    if (batchCount > 0) {
                        ps.executeBatch();
                    }

                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(prefix + "Failed to perform bulk data save on " + tableName +
                        ": " + e.getMessage());
            }
        });
    }

    /**
     * Asynchronously initializes missing player stat rows with default values (0) in the database.
     * Ensures that the player has a row for every stat defined in the main stat table.
     *
     * @param uuid      UUID of the player to initialize data for.
     * @param tableName Name of the table (without "_data" suffix).
     * @param prefix    Plugin prefix for logging purposes.
     */
    public static CompletableFuture<Void> createNewPlayerData(@NotNull UUID uuid, @NotNull String tableName, String prefix) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);
                int playerId = PlayerIDManager.getPlayerID(uuid);

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
                ConsoleUtils.sendSevereMessage(prefix + "Failed to create new player data on " + tableName +
                        " for " + uuid + ": " + e.getMessage());
            }
        });
    }

    /**
     * Asynchronously loads a player's stat data from the database into a map.
     * If no data exists for the player, default rows are initialized first.
     *
     * @param uuid      UUID of the player whose data is being loaded.
     * @param tableName Name of the table (without "_data" suffix).
     * @param prefix    Plugin prefix for logging and error messages.
     * @return A {@code CompletableFuture} containing a map of data_id to amount for the player.
     */
    public static CompletableFuture<ConcurrentHashMap<Integer, Integer>> loadPlayerData(@NotNull UUID uuid,
                                                                                        @NotNull String tableName,
                                                                                        String prefix) {
        return CompletableFuture.supplyAsync(() -> {
            ConcurrentHashMap<Integer, Integer> playerDataMap = new ConcurrentHashMap<>();

            try (Connection conn = MySQLConnection.getConnection()) {
                // Retrieve playerID using the external vlib_players table
                int playerId = PlayerIDManager.getPlayerID(uuid);

                // Check if player data exists
                String countQuery = "SELECT COUNT(*) FROM " + tableName + "_data WHERE player_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(countQuery)) {
                    ps.setInt(1, playerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt(1) == 0) {
                            // Create new player data if it doesn't exist
                            createNewPlayerData(uuid, tableName, prefix).join();
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
                ConsoleUtils.sendSevereMessage(prefix + "Failed to load data from " + tableName + " for player " +
                        uuid + ": " + e.getMessage());
            }

            return playerDataMap;
        });
    }
}
package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerIDUtils {
    private static final ConcurrentHashMap<UUID, Integer> playerIDMap = new ConcurrentHashMap<>();

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vlib_players (" +
                "playerID INT NOT NULL AUTO_INCREMENT, " +
                "uuid CHAR(36) NOT NULL, " +
                "PRIMARY KEY (playerID), " +
                "UNIQUE KEY (uuid)" +
                ")";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            ConsoleUtils.info("Table 'vlib_players' checked/created successfully.");
        } catch (SQLException e) {
            ConsoleUtils.severe("Error creating table: " + e.getMessage());
        }
    }

    @NotNull
    public static Integer getPlayerID(UUID uuid) {
        if (playerIDMap.containsKey(uuid)) {
            return playerIDMap.get(uuid);
        }

        String insertQuery = "INSERT INTO vlib_players (uuid) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
        String selectQuery = "SELECT playerID FROM vlib_players WHERE uuid = ?";

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, uuid.toString());
            insertStmt.executeUpdate();

            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerID = rs.getInt(1);
                    playerIDMap.put(uuid, playerID);
                    return playerID;
                }
            }

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setString(1, uuid.toString());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int playerID = rs.getInt("playerID");
                        playerIDMap.put(uuid, playerID);
                        return playerID;
                    }
                }
            }

        } catch (SQLException e) {
            ConsoleUtils.severe("Error fetching/inserting playerID: " + e.getMessage());
        }

        throw new IllegalStateException("Failed to retrieve or insert playerID for UUID: " + uuid);
    }

    public static boolean replaceUUID(int playerID, UUID newUUID) {
        String updateQuery = "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            ConsoleUtils.severe("Error updating uuid for playerID " + playerID + ": " + e.getMessage());
            return false;
        }
    }
}

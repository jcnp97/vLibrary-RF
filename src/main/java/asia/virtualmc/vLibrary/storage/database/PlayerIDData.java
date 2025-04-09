package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerIDData implements Listener {
    private final VLibrary vlib;
    private static ConcurrentHashMap<UUID, Integer> playerIDMap;

    public PlayerIDData(VLibrary vlib) {
        this.vlib = vlib;
        playerIDMap = new ConcurrentHashMap<>();

        createTable();
        vlib.getServer().getPluginManager().registerEvents(this, vlib);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (playerIDMap.get(uuid) != null) {
            playerIDMap.remove(uuid);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vlib_players (" +
                "playerID INT NOT NULL AUTO_INCREMENT, " +
                "uuid CHAR(36) NOT NULL, " +
                "PRIMARY KEY (playerID), " +
                "UNIQUE KEY (uuid)" +
                ")";
        try (Connection connection = ConnectionSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            ConsoleUtils.sendMessage("Table 'vlib_players' checked/created successfully.");
        } catch (SQLException e) {
            vlib.getLogger().severe("Error creating table: " + e.getMessage());
        }
    }

    public static Integer getPlayerID(UUID uuid) {
        if (playerIDMap.containsKey(uuid)) {
            return playerIDMap.get(uuid);
        }

        String query = "INSERT INTO vlib_players (uuid) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
        try (Connection connection = ConnectionSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerID = rs.getInt(1);
                    playerIDMap.put(uuid, playerID);
                    return playerID;
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error fetching/inserting playerID: " + e.getMessage());
        }
        return null;
    }

    public boolean replaceUUID(int playerID, UUID newUUID) {
        String updateQuery = "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";
        try (Connection connection = ConnectionSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            vlib.getLogger().severe("Error updating uuid for playerID " + playerID + ": " + e.getMessage());
            return false;
        }
    }
}

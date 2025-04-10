package asia.virtualmc.vLibrary.storage.database.local;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.interfaces.storage.PlayerIDStorage;
import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocalPlayerID implements PlayerIDStorage, Listener {
    private static final ConcurrentHashMap<UUID, Integer> playerIDMap = new ConcurrentHashMap<>();

    public LocalPlayerID(@NotNull VLibrary vlib) {
        vlib.getServer().getPluginManager().registerEvents(this, vlib);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerIDMap.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public Integer getPlayerID(UUID uuid) {
        if (playerIDMap.containsKey(uuid)) {
            return playerIDMap.get(uuid);
        }

        String query = "INSERT INTO vlib_players (uuid) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
        try (Connection connection = MySQLConnection.getConnection();
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

    @Override
    public boolean replaceUUID(int playerID, UUID newUUID) {
        String updateQuery = "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error updating uuid for playerID " + playerID + ": " + e.getMessage());
            return false;
        }
    }
}
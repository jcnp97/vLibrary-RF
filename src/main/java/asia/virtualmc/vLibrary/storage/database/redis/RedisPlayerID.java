package asia.virtualmc.vLibrary.storage.database.redis;

import asia.virtualmc.vLibrary.interfaces.storage.PlayerIDStorage;
import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.RedisUtils;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RedisPlayerID implements PlayerIDStorage {
    private static final String REDIS_KEY_PREFIX = "vlib:playerid:";

    private static final String MYSQL_INSERT_QUERY =
            "INSERT INTO vlib_players (uuid) VALUES (?) " +
                    "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
    private static final String MYSQL_UPDATE_UUID_QUERY =
            "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";

    @Override
    public Integer getPlayerID(UUID uuid) {
        String redisKey = REDIS_KEY_PREFIX + uuid.toString();
        String cachedPlayerID = RedisUtils.get(redisKey);
        if (cachedPlayerID != null) {
            try {
                return Integer.valueOf(cachedPlayerID);
            } catch (NumberFormatException e) {
                Bukkit.getLogger().severe("Invalid playerID value in Redis for key " + redisKey + ": " + e.getMessage());
            }
        }

        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(MYSQL_INSERT_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, uuid.toString());
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerID = rs.getInt(1);
                    RedisUtils.set(redisKey, String.valueOf(playerID));
                    return playerID;
                }
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error fetching/inserting playerID for UUID " + uuid + ": " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean replaceUUID(int playerID, UUID newUUID) {
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(MYSQL_UPDATE_UUID_QUERY)) {
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 1) {
                RedisUtils.set(REDIS_KEY_PREFIX + newUUID.toString(), String.valueOf(playerID));
                return true;
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error updating UUID for playerID " + playerID + ": " + e.getMessage());
        }
        return false;
    }
}
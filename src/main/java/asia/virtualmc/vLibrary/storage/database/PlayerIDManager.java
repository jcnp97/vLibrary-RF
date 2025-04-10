package asia.virtualmc.vLibrary.storage.database;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.interfaces.storage.PlayerIDStorage;
import asia.virtualmc.vLibrary.storage.database.local.LocalPlayerID;
import asia.virtualmc.vLibrary.storage.database.redis.RedisPlayerID;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerIDManager {
    private final VLibrary vlib;
    private static PlayerIDStorage storage;

    public PlayerIDManager(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        YamlDocument yaml = YAMLUtils.getYamlDocument(vlib, "redis.yml");

        if (yaml == null) {
            vlib.getLogger().severe("Unable to load redis.yml from plugin directory! Using single server setup instead.");
            storage = new LocalPlayerID(vlib);
            return;
        }

        boolean redisMode = yaml.getBoolean("redis.enable");
        if (redisMode) {
            ConsoleUtils.sendMessage("Attempting to connect to Redis..");
            RedisUtils.initialize(vlib);

            if (RedisUtils.hasConnection()) {
                ConsoleUtils.sendMessage("Successfully connected to Redis! Using Network-wide setup..");
                storage = new RedisPlayerID();
                return;
            } else {
                vlib.getLogger().severe("Unable to connect to Redis.");
            }

        } else {
            ConsoleUtils.sendMessage("Using single server setup..");
            storage = new LocalPlayerID(vlib);
        }

        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS vlib_players (" +
                "playerID INT NOT NULL AUTO_INCREMENT, " +
                "uuid CHAR(36) NOT NULL, " +
                "PRIMARY KEY (playerID), " +
                "UNIQUE KEY (uuid)" +
                ")";
        try (Connection connection = MySQLConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            ConsoleUtils.sendMessage("Table 'vlib_players' checked/created successfully.");
        } catch (SQLException e) {
            vlib.getLogger().severe("Error creating table: " + e.getMessage());
        }
    }

    public static Integer getPlayerID(UUID uuid) {
        return storage.getPlayerID(uuid);
    }

    public static boolean replaceUUID(int playerID, UUID newUUID) {
        return storage.replaceUUID(playerID, newUUID);
    }
}

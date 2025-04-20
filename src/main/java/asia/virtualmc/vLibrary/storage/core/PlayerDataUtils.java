package asia.virtualmc.vLibrary.storage.core;

import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDManager;
import asia.virtualmc.vLibrary.storage.database.RedisUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerDataUtils {

    public static class PlayerStats {
        public String name;
        public double exp;
        public double bxp;
        public double xpm;
        public int level;
        public int luck;
        public int traitPoints;
        public int talentPoints;
        public int wisdomTrait;
        public int charismaTrait;
        public int karmaTrait;
        public int dexterityTrait;

        public PlayerStats(String name, double exp, double bxp, double xpm, int level, int luck,
                           int traitPoints, int talentPoints, int wisdomTrait, int charismaTrait,
                           int karmaTrait, int dexterityTrait) {
            this.name = name;
            this.exp = exp;
            this.bxp = bxp;
            this.xpm = xpm;
            this.level = level;
            this.luck = luck;
            this.traitPoints = traitPoints;
            this.talentPoints = talentPoints;
            this.wisdomTrait = wisdomTrait;
            this.charismaTrait = charismaTrait;
            this.karmaTrait = karmaTrait;
            this.dexterityTrait = dexterityTrait;
        }
    }

    public static CompletableFuture<Void> createTable(@NotNull String tablePrefix, String pluginPrefix) {
        return CompletableFuture.runAsync(() -> {
            String sql = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "_playerData (" +
                    "playerID INT NOT NULL PRIMARY KEY, " +
                    "playerName VARCHAR(16) NOT NULL, " +
                    "playerEXP DECIMAL(13,2) DEFAULT 0.00, " +
                    "playerBXP DECIMAL(13,2) DEFAULT 0.00, " +
                    "playerXPM DECIMAL(4,2) DEFAULT 1.00, " +
                    "playerLevel TINYINT DEFAULT 1, " +
                    "playerLuck TINYINT DEFAULT 0, " +
                    "traitPoints INT DEFAULT 1, " +
                    "talentPoints INT DEFAULT 0, " +
                    "wisdomTrait INT DEFAULT 0, " +
                    "charismaTrait INT DEFAULT 0, " +
                    "karmaTrait INT DEFAULT 0, " +
                    "dexterityTrait INT DEFAULT 0, " +
                    "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.execute();
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(pluginPrefix + "Failed to create player data table: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<Void> savePlayerData(
            String tablePrefix,
            String pluginPrefix,
            @NotNull UUID uuid,
            @NotNull String name,
            double exp,
            double bxp,
            double xpm,
            int level,
            int luck,
            int traitPoints,
            int talentPoints,
            int wisdom,
            int charisma,
            int karma,
            int dexterity
    ) {
        return CompletableFuture.runAsync(() -> {
            Integer playerID = PlayerIDManager.getPlayerID(uuid);
            if (playerID == null) {
                ConsoleUtils.sendSevereMessage(pluginPrefix + " getPlayerID returned NULL for UUID: " + uuid);
                return;
            }
            String updateQuery = "UPDATE " + tablePrefix + "_playerData SET " +
                    "playerName = ?, playerEXP = ?, playerBXP = ?, " +
                    "playerXPM = ?, playerLevel = ?, playerLuck = ?, " +
                    "traitPoints = ?, talentPoints = ?, wisdomTrait = ?, " +
                    "charismaTrait = ?, karmaTrait = ?, dexterityTrait = ?, " +
                    "lastUpdated = CURRENT_TIMESTAMP " +
                    "WHERE playerID = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateQuery)) {

                ps.setString(1, name);
                ps.setDouble(2, exp);
                ps.setDouble(3, bxp);
                ps.setDouble(4, xpm);
                ps.setInt(5, level);
                ps.setInt(6, luck);
                ps.setInt(7, traitPoints);
                ps.setInt(8, talentPoints);
                ps.setInt(9, wisdom);
                ps.setInt(10, charisma);
                ps.setInt(11, karma);
                ps.setInt(12, dexterity);
                ps.setInt(13, playerID);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    createNewPlayerData(uuid, name, tablePrefix, pluginPrefix).join();
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(pluginPrefix + " Failed to save " + name + " data on database: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<Void> createNewPlayerData(@NotNull UUID uuid, String name, String tablePrefix, String prefix) {
        return CompletableFuture.runAsync(() -> {
            Integer playerID = PlayerIDManager.getPlayerID(uuid);
            if (playerID == null) {
                ConsoleUtils.sendSevereMessage(prefix + " getPlayerID returned NULL for UUID: " + uuid);
                return;
            }
            String insertQuery =
                    "INSERT INTO " + tablePrefix + "_playerData" +
                            " (playerID, playerName, playerEXP, playerBXP, playerXPM, " +
                            "playerLevel, playerLuck, traitPoints, talentPoints, wisdomTrait, " +
                            "charismaTrait, karmaTrait, dexterityTrait) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = MySQLConnection.getConnection()) {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                    ps.setInt(1, playerID);
                    ps.setString(2, name);
                    ps.setDouble(3, 0.0);
                    ps.setDouble(4, 0.0);
                    ps.setDouble(5, 1.0);
                    ps.setInt(6, 1);
                    ps.setInt(7, 0);
                    ps.setInt(8, 1);
                    ps.setInt(9, 0);
                    ps.setInt(10, 0);
                    ps.setInt(11, 0);
                    ps.setInt(12, 0);
                    ps.setInt(13, 0);
                    ps.executeUpdate();
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(prefix + " Failed to create data for " + name + ": " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<Void> saveAllData(
            @NotNull Map<UUID, PlayerStats> playerDataMap,
            String tablePrefix,
            String prefix
    ) {
        return CompletableFuture.runAsync(() -> {
            if (playerDataMap.isEmpty()) {
                return;
            }
            String updateQuery = "UPDATE " + tablePrefix + "_playerData SET " +
                    "playerName = ?, playerEXP = ?, playerBXP = ?, " +
                    "playerXPM = ?, playerLevel = ?, playerLuck = ?, " +
                    "traitPoints = ?, talentPoints = ?, wisdomTrait = ?, " +
                    "charismaTrait = ?, karmaTrait = ?, dexterityTrait = ?, " +
                    "lastUpdated = CURRENT_TIMESTAMP " +
                    "WHERE playerID = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateQuery)) {

                conn.setAutoCommit(false);
                int batchSize = 0;

                for (Map.Entry<UUID, PlayerStats> entry : playerDataMap.entrySet()) {
                    UUID uuid = entry.getKey();
                    PlayerStats stats = entry.getValue();
                    Integer playerID = PlayerIDManager.getPlayerID(uuid);
                    if (playerID == null) {
                        ConsoleUtils.sendSevereMessage(prefix + " getPlayerID returned NULL for UUID: " + uuid);
                        continue;
                    }
                    ps.setString(1, stats.name);
                    ps.setDouble(2, stats.exp);
                    ps.setDouble(3, stats.bxp);
                    ps.setDouble(4, stats.xpm);
                    ps.setInt(5, stats.level);
                    ps.setInt(6, stats.luck);
                    ps.setInt(7, stats.traitPoints);
                    ps.setInt(8, stats.talentPoints);
                    ps.setInt(9, stats.wisdomTrait);
                    ps.setInt(10, stats.charismaTrait);
                    ps.setInt(11, stats.karmaTrait);
                    ps.setInt(12, stats.dexterityTrait);
                    ps.setInt(13, playerID);

                    ps.addBatch();
                    batchSize++;

                    if (batchSize % 100 == 0) {
                        ps.executeBatch();
                        conn.commit();
                    }
                }

                if (batchSize % 100 != 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(prefix + " Failed to save all player data: " + e.getMessage());
            }
        });
    }

    public static CompletableFuture<PlayerStats> loadPlayerData(@NotNull UUID uuid, String tablePrefix, String prefix) {
        return CompletableFuture.supplyAsync(() -> {
            Integer playerID = PlayerIDManager.getPlayerID(uuid);
            if (playerID == null) {
                ConsoleUtils.sendSevereMessage(prefix + " getPlayerID returned NULL for UUID: " + uuid);
                return new PlayerStats("Unknown", 0.0, 0.0, 1.0, 1, 0, 1, 0, 0, 0, 0, 0);
            }

            String selectQuery = "SELECT * FROM " + tablePrefix + "_playerData WHERE playerID = ?";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(selectQuery)) {
                ps.setInt(1, playerID);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerStats(
                                rs.getString("playerName"),
                                rs.getDouble("playerEXP"),
                                rs.getDouble("playerBXP"),
                                rs.getDouble("playerXPM"),
                                rs.getInt("playerLevel"),
                                rs.getInt("playerLuck"),
                                rs.getInt("traitPoints"),
                                rs.getInt("talentPoints"),
                                rs.getInt("wisdomTrait"),
                                rs.getInt("charismaTrait"),
                                rs.getInt("karmaTrait"),
                                rs.getInt("dexterityTrait")
                        );
                    }
                }

                // If no record is found, create one and try again.
                createNewPlayerData(uuid, "Unknown", tablePrefix, prefix).join();

                // Try loading again.
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new PlayerStats(
                                rs.getString("playerName"),
                                rs.getDouble("playerEXP"),
                                rs.getDouble("playerBXP"),
                                rs.getDouble("playerXPM"),
                                rs.getInt("playerLevel"),
                                rs.getInt("playerLuck"),
                                rs.getInt("traitPoints"),
                                rs.getInt("talentPoints"),
                                rs.getInt("wisdomTrait"),
                                rs.getInt("charismaTrait"),
                                rs.getInt("karmaTrait"),
                                rs.getInt("dexterityTrait")
                        );
                    }
                }
            } catch (SQLException e) {
                ConsoleUtils.sendSevereMessage(prefix + " Failed to load data for player " + uuid + ": " + e.getMessage());
            }
            return new PlayerStats("Unknown", 0.0, 0.0, 1.0, 1, 0, 1, 0, 0, 0, 0, 0);
        });
    }

    /**
     * Stores player statistics in Redis for quick access
     *
     * @param uuid the player's UUID
     * @param stats the player's statistics object
     * @return CompletableFuture that completes when the operation is done
     */
    public static CompletableFuture<Void> storeToRedis(UUID uuid, PlayerStats stats) {
        return CompletableFuture.runAsync(() -> {
            if (!RedisUtils.hasConnection()) {
                return;
            }

            String key = "player:stats:" + uuid.toString();

            StringBuilder sb = new StringBuilder();
            sb.append(stats.name).append(":")
                    .append(stats.exp).append(":")
                    .append(stats.bxp).append(":")
                    .append(stats.xpm).append(":")
                    .append(stats.level).append(":")
                    .append(stats.luck).append(":")
                    .append(stats.traitPoints).append(":")
                    .append(stats.talentPoints).append(":")
                    .append(stats.wisdomTrait).append(":")
                    .append(stats.charismaTrait).append(":")
                    .append(stats.karmaTrait).append(":")
                    .append(stats.dexterityTrait);

            String value = sb.toString();

            RedisUtils.setWithExpiry(key, value, RedisUtils.getDefaultExpirySeconds());
        });
    }

    /**
     * Retrieves player statistics from Redis
     *
     * @param uuid the player's UUID
     * @return CompletableFuture containing the player's statistics or null if not found
     */
    public static CompletableFuture<PlayerStats> getFromRedis(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (!RedisUtils.hasConnection()) {
                return null;
            }

            String key = "player:stats:" + uuid.toString();
            String value = RedisUtils.get(key);

            if (value == null) {
                return null;
            }

            String[] parts = value.split(":");
            if (parts.length != 12) {
                return null;
            }

            try {
                return new PlayerStats(
                        parts[0],
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6]),
                        Integer.parseInt(parts[7]),
                        Integer.parseInt(parts[8]),
                        Integer.parseInt(parts[9]),
                        Integer.parseInt(parts[10]),
                        Integer.parseInt(parts[11])
                );
            } catch (NumberFormatException e) {
                return null;
            }
        });
    }
}
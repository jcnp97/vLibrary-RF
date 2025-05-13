package asia.virtualmc.vLibrary.storage.skills_core;

import asia.virtualmc.vLibrary.storage.database.MySQLConnection;
import asia.virtualmc.vLibrary.storage.database.PlayerIDUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

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

    /**
     * Creates the player data table if it doesn't already exist. The table stores experience,
     * level, traits, and other player statistics.
     *
     * @param tablePrefix The prefix used for the table name (e.g., "skills").
     * @param prefix      A log prefix used for logging messages or errors.
     */
    public static void createTable(@NotNull String tablePrefix, String prefix) {
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
            ConsoleUtils.severe(prefix, "Failed to create player data table: " + e.getMessage());
        }
    }

    /**
     * Saves or updates a single player's statistics in the database.
     * If the record does not exist, a new one will be created.
     *
     * @param uuid         The UUID of the player.
     * @param name         The name of the player.
     * @param exp          The player's current experience.
     * @param bxp          The player's bonus experience.
     * @param xpm          The player's XP multiplier.
     * @param level        The player's current level.
     * @param luck         The player's luck value.
     * @param traitPoints  The player's unspent trait points.
     * @param talentPoints The player's unspent talent points.
     * @param wisdom       The player's wisdom trait.
     * @param charisma     The player's charisma trait.
     * @param karma        The player's karma trait.
     * @param dexterity    The player's dexterity trait.
     * @param tablePrefix  The prefix used for the table name (e.g., "skills").
     * @param prefix       A log prefix used for logging messages or errors.
     */
    public static void savePlayerData(
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
            int dexterity,
            String tablePrefix,
            String prefix
    ) {
        Integer playerID = PlayerIDUtils.getPlayerID(uuid);
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
                // If no rows were updated, the player's data row doesn't exist yet.
                createNewPlayerData(uuid, name, tablePrefix, prefix);
            }

        } catch (SQLException e) {
            ConsoleUtils.severe(prefix, "Failed to save " + name + " data on database: " + e.getMessage());
        }
    }

    /**
     * Saves or updates statistics for all players in the provided map using batch operations.
     * If the player data doesn't exist in the database, it won't be inserted automatically.
     *
     * @param playerDataMap A map of UUID to PlayerStats containing the player data to save.
     * @param tablePrefix   The prefix used for the table name (e.g., "skills").
     * @param prefix        A log prefix used for logging messages or errors.
     */
    public static void saveAllData(@NotNull Map<UUID, PlayerStats> playerDataMap,
                                   String tablePrefix,
                                   String prefix
    ) {
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
                Integer playerID = PlayerIDUtils.getPlayerID(uuid);

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
            ConsoleUtils.severe(prefix, "Failed to save all player data: " + e.getMessage());
        }
    }

    /**
     * Inserts a new row for the player in the player data table with default values.
     *
     * @param uuid        The UUID of the player.
     * @param name        The name of the player.
     * @param tablePrefix The prefix used for the table name (e.g., "skills").
     * @param prefix      A log prefix used for logging messages or errors.
     */
    public static void createNewPlayerData(@NotNull UUID uuid, String name, String tablePrefix, String prefix) {
        Integer playerID = PlayerIDUtils.getPlayerID(uuid);

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
            ConsoleUtils.severe(prefix, "Failed to create data for " + name + ": " + e.getMessage());
        }
    }

    /**
     * Loads the player’s statistics from the database. If no data is found,
     * a new record is created and loading is retried once.
     *
     * @param uuid        The UUID of the player.
     * @param tablePrefix The prefix used for the table name (e.g., "skills").
     * @param prefix      A log prefix used for logging messages or errors.
     * @return A PlayerStats object containing the player's data. Returns default stats
     *         if data cannot be loaded or created.
     */
    @NotNull
    public static PlayerStats loadPlayerData(@NotNull UUID uuid, String tablePrefix, String prefix) {
        Integer playerID = PlayerIDUtils.getPlayerID(uuid);

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
            createNewPlayerData(uuid, "Unknown", tablePrefix, prefix);

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
            ConsoleUtils.severe(prefix, "Failed to load data for player " + uuid + ": " + e.getMessage());
        }

        return new PlayerStats("Unknown", 0.0, 0.0, 1.0, 1, 0, 1, 0, 0, 0, 0, 0);
    }
}
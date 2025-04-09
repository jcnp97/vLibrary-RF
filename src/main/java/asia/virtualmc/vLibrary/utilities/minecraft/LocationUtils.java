package asia.virtualmc.vLibrary.utilities.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

public class LocationUtils {

    /**
     * Formats a {@link Location} into a simple comma-separated string using block coordinates.
     *
     * @param location The location to format.
     * @return A string in the format "x, y, z" representing block coordinates.
     */
    public static String formatLocation(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return x + ", " + y + ", " + z;
    }

    /**
     * Parses a comma-separated location string and world name into a {@link Location} object.
     *
     * @param location A string in the format "x, y, z".
     * @param worldName The name of the world where the location resides.
     * @return A {@link Location} object if parsing is successful and the world is found, or null otherwise.
     */
    public static Location parseLocation(String location, String worldName) {
        String[] parts = location.split(", ");
        if (parts.length != 3) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null; // world not found

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a {@link World} object from the given world name.
     *
     * @param worldName The name of the world to retrieve.
     * @return The {@link World} object if it exists, or null if not found.
     */

    public static World parseWorld(String worldName) {
        return Bukkit.getWorld(worldName);
    }

    /**
     * Parses a string into a {@link BlockFace} enum, ignoring case and trimming whitespace.
     *
     * @param blockFace The string representation of a block face (e.g., "north", "EAST").
     * @return The corresponding {@link BlockFace}, or null if the input is invalid.
     */
    public static BlockFace parseBlockFace(String blockFace) {
        if (blockFace == null || blockFace.trim().isEmpty()) return null;

        try {
            return BlockFace.valueOf(blockFace.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}

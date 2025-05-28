package asia.virtualmc.vLibrary.utilities.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    /**
     * Serializes a block Location into a comma-delimited String: world,x,y,z
     *
     * @param location the Location to serialize (block coordinates only)
     * @return comma-delimited representation
     * @throws IllegalArgumentException if location or its world is null
     */
    public static String serialize(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }

        return String.join(",",
                world.getName(),
                Integer.toString(location.getBlockX()),
                Integer.toString(location.getBlockY()),
                Integer.toString(location.getBlockZ())
        );
    }

    /**
     * Deserializes a comma-delimited block Location String back into a Location.
     * Expects format: world,x,y,z
     *
     * @param location the serialized Location
     * @return reconstructed Location with yaw=0, pitch=0
     * @throws IllegalArgumentException if data is malformed or world not found
     */
    public static Location deserialize(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location data cannot be null or empty");
        }
        String[] parts = location.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid location format, expected 4 parts: world,x,y,z");
        }

        String worldName = parts[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' not found");
        }

        int x, y, z;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
            z = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid integers", e);
        }

        return new Location(world, x, y, z);
    }
}

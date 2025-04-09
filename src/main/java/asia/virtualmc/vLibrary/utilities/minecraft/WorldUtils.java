package asia.virtualmc.vLibrary.utilities.minecraft;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldUtils {

    /**
     * Gets the environment of the world the player is currently in.
     *
     * @param player the player whose world environment is to be retrieved
     * @return the environment of the player's world, or null if the player is offline
     */
    public static World.Environment getEnvironment(Player player) {
        if (!player.isOnline()) return null;

        return player.getWorld().getEnvironment();
    }

    /**
     * Checks if it is currently raining in the specified world.
     *
     * @param world the world to check for rain
     * @return true if it is raining, false otherwise
     */
    public static boolean isRaining(World world) {
        return world.hasStorm();
    }
}
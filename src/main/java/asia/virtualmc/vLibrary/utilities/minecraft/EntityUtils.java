package asia.virtualmc.vLibrary.utilities.minecraft;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityUtils {

    /**
     * Gets the entity of the given type that the player is currently looking at within the specified distance.
     *
     * @param player       The player to check from.
     * @param maxDistance  The maximum distance to search.
     * @param targetType   The specific EntityType to match (e.g., EntityType.ITEM_DISPLAY).
     * @return The first matching entity the player is looking at, or null if none.
     */
    public static Entity getTarget(Player player, double maxDistance, EntityType targetType) {
        for (Entity entity : player.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
            if (entity.getType() != targetType) continue;
            if (!player.hasLineOfSight(entity)) continue;

            Location eye = player.getEyeLocation();
            Location to = entity.getLocation().add(0, entity.getHeight() / 2, 0);

            Vector directionToEntity = to.toVector().subtract(eye.toVector()).normalize();
            double dot = eye.getDirection().normalize().dot(directionToEntity);

            if (dot > 0.98) {
                return entity;
            }
        }

        return null;
    }
}

package asia.virtualmc.vLibrary.events.ray_trace;

import asia.virtualmc.vLibrary.VLibrary;
import com.nexomc.nexo.api.NexoFurniture;
import com.ticxo.modelengine.api.ModelEngineAPI;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RayTraceTask {
    private static final Set<UUID> playerCache = new HashSet<>();

    public RayTraceTask(@NotNull VLibrary vlib) {
        Bukkit.getScheduler().runTaskTimer(vlib, this::task, 0L, 10L);
    }

    private void task() {
        for (UUID uuid : playerCache) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                playerCache.remove(uuid);
                continue;
            }

            RayTraceResult result = player.getWorld().rayTrace(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    5,
                    FluidCollisionMode.NEVER,
                    true,
                    0.5,
                    entity -> NexoFurniture.isFurniture(entity) || ModelEngineAPI.isModeledEntity(entity.getUniqueId())
            );

            RayTraceEvent rayTraceEvent = new RayTraceEvent(player, result);
            Bukkit.getPluginManager().callEvent(rayTraceEvent);
        }
    }

    public static void addPlayer(Player player) {
        if (player != null) {
            playerCache.add(player.getUniqueId());
        }
    }

    public static void removePlayer(Player player) {
        playerCache.remove(player.getUniqueId());
    }
}

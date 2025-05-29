package asia.virtualmc.vLibrary.events.ray_trace;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import com.nexomc.nexo.api.NexoFurniture;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RayTraceTask {
    private static final Map<UUID, EnumsLib.RayTraceType> playerCache = new HashMap<>();
    private static final Set<Material> validBlocks = new HashSet<>();

    public RayTraceTask(@NotNull VLibrary vlib) {
        initialize();
        Bukkit.getScheduler().runTaskTimer(vlib, this::task, 0L, 10L);
    }

    private void initialize() {
        List<String> materials = YAMLUtils.getList(VLibrary.getInstance(), "raytrace/blocks.yml",
                "valid-blocks");

        if (materials == null || materials.isEmpty()) {
            ConsoleUtils.severe("Unable to read raytrace/blocks.yml! No valid blocks has been added.");
            return;
        }

        for (String string : materials) {
            Material material = Material.getMaterial(string);
            if (material != null) {
                validBlocks.add(material);
            }
        }
    }

    private void task() {
        for (UUID uuid : playerCache.keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                playerCache.remove(uuid);
                continue;
            }

            EnumsLib.RayTraceType type = playerCache.get(uuid);

            // Nexo Furniture & BetterModel entities
            RayTraceResult entityResult = player.getWorld().rayTrace(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    5,
                    FluidCollisionMode.NEVER,
                    true,
                    0.5,
                    entity -> NexoFurniture.isFurniture(entity) ||
                            EntityTracker.tracker(entity.getUniqueId()) != null
            );

            if (entityResult != null && entityResult.getHitEntity() != null) {
                if (type == EnumsLib.RayTraceType.BLOCK) {
                    Bukkit.getPluginManager().callEvent(new RayTraceMissEvent(player));
                }

                Bukkit.getPluginManager().callEvent(new RayTraceEntityEvent(player, entityResult));
                playerCache.put(uuid, EnumsLib.RayTraceType.ENTITY);
                return;
            }

            // Valid Blocks (defined from raytrace/blocks.yml)
            RayTraceResult blockResult = player.getWorld().rayTraceBlocks(
                    player.getEyeLocation(),
                    player.getEyeLocation().getDirection(),
                    5,
                    FluidCollisionMode.NEVER,
                    true
            );

            if (blockResult != null && blockResult.getHitBlock() != null) {
                if (validBlocks.contains(blockResult.getHitBlock().getType())) {
                    if (type == EnumsLib.RayTraceType.ENTITY) {
                        Bukkit.getPluginManager().callEvent(new RayTraceMissEvent(player));
                    }

                    Bukkit.getPluginManager().callEvent(new RayTraceBlockEvent(player, blockResult));
                    playerCache.put(uuid, EnumsLib.RayTraceType.BLOCK);
                    return;
                }
            }

            Bukkit.getPluginManager().callEvent(new RayTraceMissEvent(player));
        }
    }

    private boolean isValidBlock(Block block) {
        return validBlocks.contains(block.getType());
    }

    public static void add(UUID uuid) {
        playerCache.put(uuid, null);
    }

    public static void remove(UUID uuid) {
        playerCache.remove(uuid);
    }
}
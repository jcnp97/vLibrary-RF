package asia.virtualmc.vLibrary.integrations.worldguard;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.events.worldguard.WGEntryHandler;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class WorldGuardUtils {
    private final VLibrary vlib;
    private static RegionContainer container;

    public WorldGuardUtils(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        Plugin plugin = vlib.getServer().getPluginManager().getPlugin("WorldGuard");

        if (plugin == null) {
            vlib.getLogger().severe("WorldGuard not found! Disabling WorldGuard integration..");
            return;
        }

        String version = WorldGuard.getVersion();

        if (version.isEmpty()) {
            vlib.getLogger().severe("Couldn't identify WorldGuard version! Disabling WorldGuard integration..");
            return;
        }

        if (!version.startsWith("7.")) {
            vlib.getLogger().severe("Integration only works with 7.0.0 and above! Disabling WorldGuard integration..");
            return;
        }

        if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WGEntryHandler.factory, null)) {
            vlib.getLogger().severe("Could not register WorldGuard handler! Disabling WorldGuard integration..");
            return;
        }

        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public static int getRegionID(Player player, String[] regionNames) {
        Location loc = player.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regions == null) return 0;

        ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            String regionID = region.getId().toLowerCase();

            for (int i = 0; i < regionNames.length; i++) {
                if (regionID.equals(regionNames[i].toLowerCase())) {
                    return i + 1;
                }
            }
        }

        return 0;
    }

    public static Set<ProtectedRegion> getRegions(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isOnline())
            return Collections.emptySet();

        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        return set.getRegions();
    }

    public static Set<String> getRegionsNames(UUID playerUUID) {
        return getRegions(playerUUID).stream().map(ProtectedRegion::getId).collect(Collectors.toSet());
    }

    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionNames Set of regions to check.
     * @return True if the player is in (all) the named region(s).
     */
    public static boolean isPlayerInAllRegions(UUID playerUUID, Set<String> regionNames) {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");

        return regions.containsAll(regionNames.stream().map(String::toLowerCase).collect(Collectors.toSet()));
    }

    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionNames Set of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAnyRegion(UUID playerUUID, Set<String> regionNames) {
        Set<String> regions = getRegionsNames(playerUUID);
        if(regionNames.isEmpty()) throw new IllegalArgumentException("You need to check for at least one region !");
        for(String region : regionNames) {
            if(regions.contains(region.toLowerCase()))
                return true;
        }
        return false;
    }

    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionName List of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAnyRegion(UUID playerUUID, String... regionName) {
        return isPlayerInAnyRegion(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }

    /**
     * Checks whether a player is in one or several regions
     *
     * @param playerUUID UUID of the player in question.
     * @param regionName List of regions to check.
     * @return True if the player is in (any of) the named region(s).
     */
    public static boolean isPlayerInAllRegions(UUID playerUUID, String... regionName) {
        return isPlayerInAllRegions(playerUUID, new HashSet<>(Arrays.asList(regionName)));
    }
}

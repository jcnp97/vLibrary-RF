package asia.virtualmc.vLibrary.integrations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class WorldGuardUtils {

    public static ProtectedRegion getRegion(Player player) {
        Location loc = player.getLocation();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regions == null) return null;

        ApplicableRegionSet regionSet = regions.getApplicableRegions(BukkitAdapter.asBlockVector(loc));
        Set<ProtectedRegion> regionList = regionSet.getRegions();

        return regionList.isEmpty() ? null : regionList.iterator().next();
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
}

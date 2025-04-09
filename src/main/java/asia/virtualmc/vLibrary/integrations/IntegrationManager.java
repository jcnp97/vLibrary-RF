package asia.virtualmc.vLibrary.integrations;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.events.ray_trace.RayTraceTask;
import org.jetbrains.annotations.NotNull;

public class IntegrationManager {

    public IntegrationManager(@NotNull VLibrary vlib) {
        WorldGuardUtils worldGuardUtils = new WorldGuardUtils(vlib);
        HologramUtils hologramUtils = new HologramUtils(vlib);
        RayTraceTask rayTraceTask = new RayTraceTask(vlib);
        ParticleUtils particleUtils = new ParticleUtils(vlib);
        VaultEconomy vaultEconomy = new VaultEconomy(vlib);
        VaultPermissions vaultPermissions = new VaultPermissions(vlib);
        SeasonsUtils seasonsUtils = new SeasonsUtils(vlib);
    }
}

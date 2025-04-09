package asia.virtualmc.vLibrary;

import asia.virtualmc.vLibrary.commands.CommandManager;
import asia.virtualmc.vLibrary.core.CoreManager;
import asia.virtualmc.vLibrary.integrations.HologramUtils;
import asia.virtualmc.vLibrary.integrations.IntegrationManager;
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.hologram.HologramManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class VLibrary extends JavaPlugin {
    private CommandManager commandManager;
    private CoreManager coreManager;
    private IntegrationManager integrationManager;

    @Override
    public void onEnable() {
        this.coreManager = new CoreManager(this);
        this.commandManager = new CommandManager(this);
        this.integrationManager = new IntegrationManager(this);
        CommandAPI.onEnable();
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .verboseOutput(false)
                .silentLogs(true)
        );

        HologramLib.onLoad(this);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        HologramUtils.clearAllHolograms();
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public IntegrationManager getIntegrationManager() { return integrationManager; }
}

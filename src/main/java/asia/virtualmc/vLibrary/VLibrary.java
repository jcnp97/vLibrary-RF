package asia.virtualmc.vLibrary;

import asia.virtualmc.vLibrary.commands.CommandManager;
import asia.virtualmc.vLibrary.core.CoreManager;
import asia.virtualmc.vLibrary.integrations.holograms.HologramUtils;
import asia.virtualmc.vLibrary.integrations.IntegrationManager;
import asia.virtualmc.vLibrary.storage.StorageManager;
import asia.virtualmc.vLibrary.tasks.TaskManager;
import asia.virtualmc.vLibrary.utilities.files.SQLiteUtils;
import com.maximde.hologramlib.HologramLib;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class VLibrary extends JavaPlugin {
    private static VLibrary vlib;
    private CommandManager commandManager;
    private CoreManager coreManager;
    private IntegrationManager integrationManager;
    private TaskManager taskManager;

    @Override
    public void onEnable() {
        vlib = this;
        this.coreManager = new CoreManager(this);
        this.commandManager = new CommandManager(this);
        this.integrationManager = new IntegrationManager(this);
        this.taskManager = new TaskManager(this);

        CommandAPI.onEnable();
        SQLiteUtils.initialize(this);
        StorageManager storageManager = new StorageManager(this);
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
        taskManager.cancelAll();

        // Static
        CommandAPI.onDisable();
        HologramUtils.clearAll();
        SQLiteUtils.closeAll();
    }

    public static VLibrary getInstance() {
        return vlib;
    }

    public CoreManager getCoreManager() {
        return coreManager;
    }

    public IntegrationManager getIntegrationManager() { return integrationManager; }
}
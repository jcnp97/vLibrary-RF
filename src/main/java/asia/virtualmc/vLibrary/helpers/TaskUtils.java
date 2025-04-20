package asia.virtualmc.vLibrary.helpers;


import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TaskUtils {

    public static <T> void runAsyncThenSync(Plugin plugin, Supplier<T> asyncWork, Consumer<T> syncWork) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
            T result;

            try {
                result = asyncWork.get();
            } catch (Exception e) {
                plugin.getLogger().severe("Async task failed: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> syncWork.accept(result));
        });
    }
}

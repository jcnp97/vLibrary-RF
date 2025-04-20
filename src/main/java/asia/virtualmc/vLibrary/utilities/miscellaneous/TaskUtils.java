package asia.virtualmc.vLibrary.utilities.miscellaneous;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class TaskUtils {

    public static BukkitTask scheduleRepeatingTask(Plugin plugin, double duration, double interval, Runnable task) {
        long intervalTicks = (long) (interval * 20);
        long durationTicks = (long) (duration * 20);

        BukkitTask repeatingTask = plugin.getServer().getScheduler().runTaskTimer(plugin, task, intervalTicks, intervalTicks);
        plugin.getServer().getScheduler().runTaskLater(plugin, repeatingTask::cancel, durationTicks);

        return repeatingTask;
    }

    public static BukkitTask scheduleRepeatingTask(Plugin plugin, double interval, Runnable task) {
        long intervalTicks = (long) (interval * 20);

        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, intervalTicks, intervalTicks);
    }
}

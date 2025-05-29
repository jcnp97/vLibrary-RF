package asia.virtualmc.vLibrary.utilities.miscellaneous;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public class TaskUtils {

//    public static BukkitTask repeating(Plugin plugin, Runnable task, double interval, double duration) {
//        long intervalTicks = (long) (interval * 20);
//        long durationTicks = (long) (duration * 20);
//
//        BukkitTask repeatingTask = plugin.getServer().getScheduler().runTaskTimer(plugin, task, intervalTicks, intervalTicks);
//        plugin.getServer().getScheduler().runTaskLater(plugin, repeatingTask::cancel, durationTicks);
//
//        return repeatingTask;
//    }
//
//    public static BukkitTask repeating(Plugin plugin, Runnable task, double interval) {
//        long intervalTicks = (long) (interval * 20);
//
//        return plugin.getServer().getScheduler().runTaskTimer(plugin, task, intervalTicks, intervalTicks);
//    }

    // Paper's Modern Scheduler
    /**
     * Runs a repeating task using the Paper GlobalRegionScheduler at a fixed interval.
     *
     * @param plugin  the plugin instance running the task
     * @param task    the task to execute
     * @param interval the interval between executions in seconds
     * @return the scheduled repeating task
     */
    public static ScheduledTask repeating(Plugin plugin, Runnable task, double interval) {
        long intervalTicks = (long) (interval * 20);

        return plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                t -> task.run(),
                intervalTicks,
                intervalTicks
        );
    }

    /**
     * Runs a repeating task using the Paper GlobalRegionScheduler with a fixed interval,
     * and cancels it automatically after the specified duration.
     *
     * @param plugin   the plugin instance running the task
     * @param task     the task to execute
     * @param interval the interval between executions in seconds
     * @param duration the total duration before the task is cancelled, in seconds
     * @return the scheduled repeating task
     */
    public static ScheduledTask repeating(Plugin plugin, Runnable task, double interval, double duration) {
        long intervalTicks = (long) (interval * 20);
        long durationTicks = (long) (duration * 20);

        ScheduledTask repeating = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                t -> task.run(),
                intervalTicks,
                intervalTicks
        );

        plugin.getServer().getGlobalRegionScheduler().runDelayed(
                plugin,
                t -> repeating.cancel(),
                durationTicks
        );

        return repeating;
    }

    /**
     * Runs a repeating task asynchronously using the Paper AsyncScheduler at a fixed interval.
     *
     * @param plugin   the plugin instance running the task
     * @param task     the task to execute
     * @param interval the interval between executions in seconds
     * @return the scheduled repeating async task
     */
    public static ScheduledTask repeatingAsync(Plugin plugin, Runnable task, double interval) {
        long intervalMillis = (long) (interval * 1000);

        return plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                scheduledTask -> task.run(),
                intervalMillis,
                intervalMillis,
                TimeUnit.MILLISECONDS
        );
    }
}
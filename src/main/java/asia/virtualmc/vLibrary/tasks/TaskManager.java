package asia.virtualmc.vLibrary.tasks;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.files.SQLiteUtils;
import asia.virtualmc.vLibrary.utilities.miscellaneous.TaskUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TaskManager {
    private final VLibrary vlib;
    private static final Set<ScheduledTask> taskCache = new HashSet<>();

    public TaskManager(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        async();
    }

    private void sync() {

    }

    private void async() {
        taskCache.add(TaskUtils.repeatingAsync(vlib, SQLiteUtils::checkpointAll, 300));
    }

    public void cancelAll() {
        for (ScheduledTask task : taskCache) {
            task.cancel();
        }

        taskCache.clear();
    }
}

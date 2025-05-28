package asia.virtualmc.vLibrary.events.ray_trace;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.RayTraceResult;

public class RayTraceEntityEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Entity entity;
    private final RayTraceResult result;
    private final Location location;
    private boolean cancelled;

    public RayTraceEntityEvent(Player player, RayTraceResult result) {
        this.player = player;
        this.result = result;
        this.entity = (result != null) ? result.getHitEntity() : null;
        this.location = (entity != null) ? entity.getLocation() : null;
    }

    public Player getPlayer() {
        return player;
    }

    public RayTraceResult getResult() {
        return result;
    }

    public Entity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

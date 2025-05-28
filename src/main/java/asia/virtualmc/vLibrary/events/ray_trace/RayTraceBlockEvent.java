package asia.virtualmc.vLibrary.events.ray_trace;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.RayTraceResult;

public class RayTraceBlockEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final Block block;
    private final RayTraceResult result;
    private final Location location;
    private boolean cancelled;

    public RayTraceBlockEvent(Player player, RayTraceResult result) {
        this.player = player;
        this.result = result;
        this.block = (result != null) ? result.getHitBlock() : null;
        this.location = (block != null) ? block.getLocation() : null;
    }

    public Player getPlayer() {
        return player;
    }

    public RayTraceResult getResult() {
        return result;
    }

    public Block getBlock() {
        return block;
    }

    public Material getType() {
        return block.getType();
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

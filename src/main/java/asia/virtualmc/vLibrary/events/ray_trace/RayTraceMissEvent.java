package asia.virtualmc.vLibrary.events.ray_trace;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RayTraceMissEvent extends Event {
    private final Player player;

    public RayTraceMissEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    private static final HandlerList handlers = new HandlerList();
    public static HandlerList getHandlerList() { return handlers; }
    @Override public HandlerList getHandlers() { return handlers; }
}

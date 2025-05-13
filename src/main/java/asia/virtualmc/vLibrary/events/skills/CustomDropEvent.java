package asia.virtualmc.vLibrary.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CustomDropEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final boolean effects;
    private Object data;
    private boolean cancelled;

    public CustomDropEvent(Player player, boolean effects) {
        this.player = player;
        this.effects = effects;
    }

    public Player getPlayer() {
        return player;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public <T> T getData(Class<T> type) {
        return type.cast(data);
    }

    public boolean canTriggerEffects() {
        return effects;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

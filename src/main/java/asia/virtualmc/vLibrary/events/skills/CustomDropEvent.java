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
    private boolean cancelled;
    private int rarityID;

    public CustomDropEvent(Player player, int rarityID, boolean effects) {
        this.player = player;
        this.rarityID = rarityID;
        this.effects = effects;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRarityID() { return rarityID; }

    public void addRarity(int value) { this.rarityID += value ;}

    public void setRarity(int value) { this.rarityID = value ;}

    public boolean canTrigger() {
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

package asia.virtualmc.vLibrary.events.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class EXPReceiveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    protected final Player player;
    protected final UUID uuid;
    protected double[] exp;

    public EXPReceiveEvent(Player player, double[] exp) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.exp = exp;
    }

    public Player getPlayer() { return player; }
    public UUID getUUID() { return uuid; }
    public double[] getExp() { return exp; }

    public void addExp(double value, int array) {
        this.exp[array] += value;
    }

    public void subtractExp(double value, int array) {
        this.exp[array] -= value;
        if (this.exp[array] < 0) this.exp[array] = 0;
    }

    public void multiplyExp(double value, int array) {
        this.exp[array] *= value;
    }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}

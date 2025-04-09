package asia.virtualmc.vLibrary.events.worldguard;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

public class WGEntryHandler extends Handler implements Listener {

    public final PluginManager pm = Bukkit.getPluginManager();
    public static final Factory factory = new Factory();

    public static class Factory extends Handler.Factory<WGEntryHandler> {
        @Override
        public WGEntryHandler create(Session session) {
            return new WGEntryHandler(session);
        }
    }

    public WGEntryHandler(Session session) {
        super(session);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> left, MoveType moveType) {
        RegionsEnterEvent regionsEnterEvent = new RegionsEnterEvent(player.getUniqueId(), entered);
        pm.callEvent(regionsEnterEvent);
        if (regionsEnterEvent.isCancelled()) return false;

        RegionsLeaveEvent regionsLeaveEvent = new RegionsLeaveEvent(player.getUniqueId(), left);
        pm.callEvent(regionsLeaveEvent);
        if(regionsLeaveEvent.isCancelled()) return false;

        for (ProtectedRegion r : entered) {
            RegionEnterEvent regionEnterEvent = new RegionEnterEvent(player.getUniqueId(), r);
            pm.callEvent(regionEnterEvent);
            if(regionEnterEvent.isCancelled()) return false;
        }

        for (ProtectedRegion protectedRegion : left) {
            RegionLeaveEvent regionLeaveEvent = new RegionLeaveEvent(player.getUniqueId(), protectedRegion);
            pm.callEvent(regionLeaveEvent);
            if(regionLeaveEvent.isCancelled()) return false;
        }
        return true;
    }
}

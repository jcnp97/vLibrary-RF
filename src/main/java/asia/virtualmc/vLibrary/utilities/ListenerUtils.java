package asia.virtualmc.vLibrary.utilities;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ListenerUtils {
    private static final Map<Plugin, Set<Listener>> registeredEvents = new HashMap<>();

    /**
     * Registers a listener for the specified plugin and tracks it for future unregistration.
     *
     * @param plugin The plugin that owns the listener.
     * @param listener The listener to register.
     */
    public static void registerListener(Plugin plugin, Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        registeredEvents.computeIfAbsent(plugin, k -> new HashSet<>()).add(listener);
    }

    /**
     * Unregisters a specific listener associated with a plugin.
     *
     * @param plugin The plugin that owns the listener.
     * @param listener The listener to unregister.
     */
    public static void unregisterListener(Plugin plugin, Listener listener) {
        Set<Listener> listeners = registeredEvents.get(plugin);
        if (listeners == null) return;

        listeners.remove(listener);
        HandlerList.unregisterAll(listener);

        if (listeners.isEmpty()) {
            registeredEvents.remove(plugin);
        }
    }

    /**
     * Unregisters all listeners for all plugins that have been registered using this utility.
     */
    public static void unregisterAll() {
        for (Map.Entry<Plugin, Set<Listener>> entry : registeredEvents.entrySet()) {
            Set<Listener> listeners = entry.getValue();
            for (Listener listener : new HashSet<>(listeners)) {
                HandlerList.unregisterAll(listener);
            }
            listeners.clear();
        }
        registeredEvents.clear();
    }

    /**
     * Unregisters all listeners associated with the specified plugin.
     *
     * @param plugin The plugin whose listeners should be unregistered.
     */
    public static void unregisterAll(Plugin plugin) {
        Set<Listener> listeners = registeredEvents.remove(plugin);
        if (listeners == null) return;

        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
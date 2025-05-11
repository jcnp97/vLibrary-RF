package asia.virtualmc.vLibrary.integrations.holograms;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.integrations.packet_events.PacketEventsUtils;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.__relocated__.me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import com.maximde.hologramlib.hologram.*;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class HologramUtils {
    private static final Set<UUID> hologramIDs = new HashSet<>();
    private static HologramManager hologramManager;

    public HologramUtils(@NotNull VLibrary vlib) {
        HologramLib.getManager().ifPresentOrElse(
                manager -> hologramManager = manager,
                () -> vlib.getLogger().severe("Failed to initialize HologramLib manager.")
        );
    }

    /**
     * Creates a glowing item hologram visible only to a specific player.
     *
     * @param material  The material of the item to display.
     * @param modelData The custom model data for the item.
     * @param player    The player who can view the hologram.
     * @param x         The x-scale of the hologram.
     * @param y         The y-scale of the hologram.
     * @param z         The z-scale of the hologram.
     * @param location  The location to spawn the hologram.
     * @return The UUID of the created hologram.
     */
    public static UUID createItemHologram(Material material, int modelData, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Creates a temporary item hologram visible to a specific player and removes it after 30 ticks.
     *
     * @param plugin    The plugin instance used to schedule the removal task.
     * @param material  The material of the item to display.
     * @param modelData The custom model data for the item.
     * @param player    The player who can view the hologram.
     * @param x         The x-scale of the hologram.
     * @param y         The y-scale of the hologram.
     * @param z         The z-scale of the hologram.
     * @param location  The location to spawn the hologram.
     */
    public static void createItemHologram(Plugin plugin, Material material, int modelData, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        new BukkitRunnable() {
            @Override
            public void run() {
                hologramManager.remove(itemHologram);
            }
        }.runTaskLater(plugin, 30L);
    }

    /**
     * Creates a text hologram visible only to a specific player.
     *
     * @param text     The MiniMessage-formatted text to display.
     * @param player   The player who can view the hologram.
     * @param x        The x-scale of the hologram.
     * @param y        The y-scale of the hologram.
     * @param z        The z-scale of the hologram.
     * @param location The location to spawn the hologram.
     * @return The UUID of the created text hologram.
     */
    public static UUID createTextHologram(String text, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        TextHologram textHologram = new TextHologram(hologramID.toString())
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Updates the text of a tracked text hologram.
     *
     * @param hologramID The UUID of the text hologram to update.
     * @param newText    The new MiniMessage-formatted text to display.
     */
    public static void modifyText(UUID hologramID, String newText) {
        if (!hologramIDs.contains(hologramID)) return;

        TextHologram textHologram = ((TextHologram) hologramManager.getHologram(hologramID.toString()).get());
        textHologram.setText(newText).update();
    }

    public static UUID createCompositeHologram(String text, Player player, Material material, int modelData,
                                               float x, float y, float z, Location location) {

        UUID uuid = UUID.randomUUID();
        UUID hologramItem = UUID.fromString(uuid + "_ITEM");
        UUID hologramText = UUID.fromString(uuid + "_TEXT");

        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramItem.toString())
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);

        TextHologram textHologram = new TextHologram(hologramText.toString())
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);

        hologramManager.spawn(itemHologram, location);
        hologramManager.spawn(textHologram, location.subtract(0, 0.25, 0));
        hologramIDs.add(hologramItem);
        hologramIDs.add(hologramText);

        return uuid;
    }

    public static void removeCompositeHologram(UUID hologramID) {
        UUID hologramItem = UUID.fromString(hologramID + "_ITEM");
        UUID hologramText = UUID.fromString(hologramID + "_TEXT");

        if (hologramIDs.contains(hologramItem)) {
            hologramManager.remove(hologramItem.toString());
            hologramIDs.remove(hologramItem);
        }

        if (hologramIDs.contains(hologramText)) {
            hologramManager.remove(hologramText.toString());
            hologramIDs.remove(hologramText);
        }
    }

    /**
     * Removes a tracked hologram from the world and internal registry.
     *
     * @param hologramID The UUID of the hologram to remove.
     */
    public static void removeHologram(UUID hologramID) {
        if (hologramIDs.contains(hologramID)) {
            hologramManager.remove(hologramID.toString());
            hologramIDs.remove(hologramID);
        }
    }

    /**
     * Creates a glowing item hologram visible to all players.
     *
     * @param material  The material of the item to display.
     * @param modelData The custom model data for the item.
     * @param x         The x-scale of the hologram.
     * @param y         The y-scale of the hologram.
     * @param z         The z-scale of the hologram.
     * @param location  The location to spawn the hologram.
     * @return The UUID of the created hologram.
     */
    public static UUID createItemHologram(Material material, int modelData, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString(), RenderMode.ALL)
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Creates a text hologram visible to all players.
     *
     * @param text     The MiniMessage-formatted text to display.
     * @param x        The x-scale of the hologram.
     * @param y        The y-scale of the hologram.
     * @param z        The z-scale of the hologram.
     * @param location The location to spawn the hologram.
     * @return The UUID of the created text hologram.
     */
    public static UUID createTextHologram(String text, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        TextHologram textHologram = new TextHologram(hologramID.toString(), RenderMode.ALL)
                .setMiniMessageText(text)
                .setViewRange(10.0)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Adds a player as a viewer to an existing hologram.
     *
     * @param player     The player to add as a viewer.
     * @param hologramID The UUID of the hologram.
     */
    public static void addViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.addViewer(player));
    }

    /**
     * Removes a player from the viewers of an existing hologram.
     *
     * @param player     The player to remove from the viewer list.
     * @param hologramID The UUID of the hologram.
     */
    public static void removeViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.removeViewer(player));
    }

    /**
     * Removes all spawned holograms and clears the internal hologram registry.
     */
    public static void clearAllHolograms() {
        hologramManager.removeAll();
        hologramIDs.clear();
    }
}
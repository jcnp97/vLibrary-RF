package asia.virtualmc.vLibrary.utilities.minecraft;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class InventoryUtils {

    /**
     * Creates a snapshot of a player's inventory, storing only items that contain a specific Persistent Data Key.
     *
     * @param player   The player whose inventory is being captured.
     * @param ITEM_KEY The NamespacedKey used to identify relevant items.
     * @return A map containing the slot index and corresponding ItemStack for matching items.
     */
    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player,
                                                         @NotNull NamespacedKey ITEM_KEY) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            if (pdc.has(ITEM_KEY)) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a snapshot of a player's inventory, storing only items that have a specific Persistent Data Key
     * and match a given set of item IDs.
     *
     * @param player   The player whose inventory is being captured.
     * @param ITEM_KEY The NamespacedKey used to retrieve the item's ID.
     * @param itemIDs  A set of valid item IDs to filter the items.
     * @return A map containing the slot index and corresponding ItemStack for matching items.
     */
    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player,
                                                         @NotNull NamespacedKey ITEM_KEY,
                                                         @NotNull Set<Integer> itemIDs) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            if (itemIDs.contains(pdc.getOrDefault(ITEM_KEY, PersistentDataType.INTEGER, 0))) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a snapshot of a player's inventory, storing only items that match a specified set of materials.
     *
     * @param player    The player whose inventory is being captured.
     * @param materials A set of materials to filter the items.
     * @return A map containing the slot index and corresponding ItemStack for matching items.
     */
    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player,
                                                         @NotNull Set<Material> materials) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (materials.contains(item.getType())) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a full snapshot of a player's inventory, storing all items in their respective slots.
     *
     * @param player The player whose inventory is being captured.
     * @return A map containing the slot index and corresponding ItemStack for all items in the inventory.
     */
    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            snapshot.put(i, item.clone());
        }

        return snapshot;
    }

    /**
     * Compares the current state of a player's inventory against a previously taken snapshot.
     *
     * @param player   The player whose inventory is being compared.
     * @param snapshot The snapshot to compare against.
     * @return True if the inventory matches the snapshot exactly, otherwise false.
     */
    public static boolean compareSnapshot(@NotNull Player player,
                                          @NotNull Map<Integer, ItemStack> snapshot) {
        for (Map.Entry<Integer, ItemStack> entry : snapshot.entrySet()) {
            ItemStack current = player.getInventory().getItem(entry.getKey());
            ItemStack snapshotItem = entry.getValue();

            if (current == null || !current.equals(snapshotItem)) {
                return false;
            }
        }
        return true;
    }

    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player,
                                                         Predicate<ItemStack> condition) {

        Map<Integer, ItemStack> snapshot = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (condition.test(item)) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }
}

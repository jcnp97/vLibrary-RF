package asia.virtualmc.vLibrary.utilities.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class PDCUtils {

    public static boolean isCustomItem(@NotNull ItemStack item, @NotNull NamespacedKey ITEM_KEY) {
        if (!item.hasItemMeta()) return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(ITEM_KEY, PersistentDataType.INTEGER);
    }

    public static int getItemID(@NotNull ItemStack item, @NotNull NamespacedKey ITEM_KEY) {
        if (!item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(ITEM_KEY, PersistentDataType.INTEGER, 0);
    }

    public static int getToolLevel(@NotNull ItemStack item, @NotNull NamespacedKey REQ_LEVEL_KEY) {
        if (!item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(REQ_LEVEL_KEY, PersistentDataType.INTEGER, 0);
    }

    public static boolean hasRequiredLevel(@NotNull ItemStack item, @NotNull NamespacedKey REQ_LEVEL_KEY, int level) {
        if (!item.hasItemMeta()) return false;

        return (level >= getToolLevel(item, REQ_LEVEL_KEY));
    }

    public static double getDoubleData(@NotNull ItemStack item, @NotNull NamespacedKey DOUBLE_KEY) {
        if (!item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(DOUBLE_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    public static int getIntegerData(@NotNull ItemStack item, @NotNull NamespacedKey INTEGER_KEY) {
        if (!item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(INTEGER_KEY, PersistentDataType.INTEGER, 0);
    }

    public static boolean compareIntegerData(@NotNull ItemStack item, @NotNull NamespacedKey TOOL_KEY, int itemID) {
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(TOOL_KEY, PersistentDataType.INTEGER)) {
            return pdc.getOrDefault(TOOL_KEY, PersistentDataType.INTEGER, 0) == itemID;
        }
        return false;
    }

    public static ItemStack addPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey PDC_KEY, int value) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Meta cannot be null when applying a PDC.");
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PDC_KEY, PersistentDataType.INTEGER, value);
        clone.setItemMeta(meta);

        return clone;
    }

    public static ItemStack addPersistentData(@NotNull ItemStack item, @NotNull NamespacedKey PDC_KEY, double value) {
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Meta cannot be null when applying a PDC.");
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PDC_KEY, PersistentDataType.DOUBLE, value);
        clone.setItemMeta(meta);

        return clone;
    }
}

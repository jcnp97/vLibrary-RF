package asia.virtualmc.vLibrary.utilities.items;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PDCUtils {

    public static PersistentDataContainer getPDC(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            return meta.getPersistentDataContainer();
        }

        return null;
    }

    public static boolean isCustomItem(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key, PersistentDataType.INTEGER);
    }

    public static int getToolLevel(ItemStack item, NamespacedKey REQ_LEVEL_KEY) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(REQ_LEVEL_KEY, PersistentDataType.INTEGER, 0);
    }

    public static boolean hasRequiredLevel(ItemStack item, NamespacedKey REQ_LEVEL_KEY, int level) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return (level >= getToolLevel(item, REQ_LEVEL_KEY));
    }

    public static double getDouble(ItemStack item, NamespacedKey DOUBLE_KEY) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0.0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(DOUBLE_KEY, PersistentDataType.DOUBLE, 0.0);
    }

    public static int getInteger(ItemStack item, NamespacedKey INTEGER_KEY) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(INTEGER_KEY, PersistentDataType.INTEGER, 0);
    }

    public static int[] getIntArray(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;

        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.INTEGER_ARRAY, new int[]{});
    }

    public static void addInteger(ItemMeta meta, NamespacedKey PDC_KEY, int value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PDC_KEY, PersistentDataType.INTEGER, value);
    }

    public static void addDouble(ItemMeta meta, NamespacedKey PDC_KEY, double value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PDC_KEY, PersistentDataType.DOUBLE, value);
    }

    public static void addIntArray(ItemMeta meta, NamespacedKey PDC_KEY, int[] value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(PDC_KEY, PersistentDataType.INTEGER_ARRAY, value);
    }

    public static ItemStack addData(ItemStack item, NamespacedKey PDC_KEY, int value) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;

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

    public static ItemStack addData(ItemStack item, NamespacedKey PDC_KEY, double value) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return null;

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

    public static boolean compareIntegerData(ItemStack item, NamespacedKey TOOL_KEY, int itemID) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (pdc.has(TOOL_KEY, PersistentDataType.INTEGER)) {
            return pdc.getOrDefault(TOOL_KEY, PersistentDataType.INTEGER, 0) == itemID;
        }
        return false;
    }
}

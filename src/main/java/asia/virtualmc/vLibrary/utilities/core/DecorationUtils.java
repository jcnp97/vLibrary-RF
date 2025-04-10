package asia.virtualmc.vLibrary.utilities.core;

import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.items.PDCUtils;
import asia.virtualmc.vLibrary.utilities.messages.AdventureUtils;
import asia.virtualmc.vLibrary.utilities.text.DigitUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DecorationUtils {

    /**
     * Converts a double value to a long by rounding it to 2 decimal places
     * and multiplying by 100 to preserve precision.
     *
     * @param value the double value to convert
     * @return the converted long value
     */
    public static Long convertToLong(double value) {
        double formatDouble = DigitUtils.getPreciseValue(value, 2);
        return (long) (formatDouble * 100);
    }

    /**
     * Converts a long value back to a double by dividing by 100,
     * reversing the precision scaling applied earlier.
     *
     * @param value the long value to convert
     * @return the converted double value
     */
    public static double convertToDouble(long value) {
        return (double) (value / 100);
    }

    public static ItemStack getNewTool(ItemStack deco, NamespacedKey DECO_KEY, NamespacedKey DECO_VALUE,
                                       ItemStack tool, NamespacedKey DECO_IDS, NamespacedKey DECO_VALUES,
                                       NamespacedKey DECO_SLOTS) {

        try {
            int decoID = PDCUtils.getIntegerData(deco, DECO_KEY);
            long decoValue = convertToLong(PDCUtils.getDoubleData(deco, DECO_VALUE));

            ItemStack clone = tool.clone();
            ItemMeta meta = clone.getItemMeta();
            if (meta == null) return null;

            Pair<int[], long[]> oldData = getDecorations(tool, DECO_IDS, DECO_VALUES);
            Pair<int[], long[]> newData = addDecoration(decoID, oldData.getLeft(),
                    decoValue, oldData.getRight());

            int[] decoIDs = newData.getLeft();
            long[] decoValues = newData.getRight();

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            int[] decoSlots = pdc.get(DECO_SLOTS, PersistentDataType.INTEGER_ARRAY);
            if (decoSlots == null) return null;
            decoSlots[0]++;

            // Remove old PDC
            pdc.remove(DECO_IDS);
            pdc.remove(DECO_VALUES);
            pdc.remove(DECO_SLOTS);

            // Set new PDC
            pdc.set(DECO_IDS, PersistentDataType.INTEGER_ARRAY, decoIDs);
            pdc.set(DECO_VALUES, PersistentDataType.LONG_ARRAY, decoValues);
            pdc.set(DECO_SLOTS, PersistentDataType.INTEGER_ARRAY, decoSlots);

            addDecorationLore(meta, deco);
            clone.setItemMeta(meta);

            return clone;
        } catch (Exception e) {
            Bukkit.getLogger().severe("An error occurred when trying to apply decoration: " +
                    deco.getItemMeta().getDisplayName() + " on " + tool.getItemMeta().getDisplayName() + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<int[], long[]> addDecoration(int newDecoID, int[] decoIDs, long newValue, long[] decoValues) {
        if (decoIDs == null || decoValues == null) {
            int[] newDecoIDs = {newDecoID};
            long[] newDecoValues = {newValue};

            return Pair.of(newDecoIDs, newDecoValues);
        }

        Map<Integer, Long> mapValues = new LinkedHashMap<>();
        for (int i = 0; i < decoIDs.length; i++) {
            mapValues.put(decoIDs[i], decoValues[i]);
        }

        mapValues.merge(newDecoID, newValue, Long::sum);
        int[] newDecoIDs = mapValues.keySet().stream().mapToInt(Integer::intValue).toArray();
        long[] newDecoValues = mapValues.values().stream().mapToLong(Long::longValue).toArray();

        return Pair.of(newDecoIDs, newDecoValues);
    }

    public static void addDecorationLore(ItemMeta meta, ItemStack deco) {
        if (meta == null) return;

        List<String> legacyLore = meta.getLore();
        if (legacyLore == null) return;

        String targetLegacy = LegacyComponentSerializer.legacySection().serialize(AdventureUtils.convertToComponent("<!i><dark_gray>\uD83D\uDC8E - ᴇᴍᴘᴛʏ sʟᴏᴛ"));

        for (int i = 0; i < legacyLore.size(); i++) {
            if (legacyLore.get(i).equals(targetLegacy)) {
                legacyLore.set(i, MetaUtils.getDisplayName(deco));
                break;
            }
        }

        meta.setLore(legacyLore);
    }

    public static boolean canApply(ItemStack item, NamespacedKey DECO_SLOTS) {
        PersistentDataContainer pdc = PDCUtils.getPDC(item);

        if (pdc != null) {
            if (pdc.has(DECO_SLOTS)) {
                int[] slots = pdc.get(DECO_SLOTS, PersistentDataType.INTEGER_ARRAY);
                if (slots == null || slots.length < 2) return false;

                return (slots[0] < slots[1]);
            }
        }

        return false;
    }

    public static boolean hasDecoration(ItemStack item, NamespacedKey DECO_SLOT_KEY) {
        PersistentDataContainer pdc = PDCUtils.getPDC(item);

        if (pdc != null) {
            return pdc.getOrDefault(DECO_SLOT_KEY, PersistentDataType.INTEGER_ARRAY, new int[]{0, 0})[0] > 0;
        }

        return false;
    }

    // GETTER METHODS
    public static Pair<int[], long[]> getDecorations(ItemStack item, NamespacedKey DECO_IDS, NamespacedKey DECO_VALUES) {
        PersistentDataContainer pdc = PDCUtils.getPDC(item);
        if (pdc != null) {
            int[] decoIDs = pdc.get(DECO_IDS, PersistentDataType.INTEGER_ARRAY);
            long[] decoValues = pdc.get(DECO_VALUES, PersistentDataType.LONG_ARRAY);

            return Pair.of(decoIDs, decoValues);
        }

        return Pair.of(null, null);
    }

    public static int[] getIDs(ItemStack item, NamespacedKey DECO_IDS) {
        PersistentDataContainer pdc = PDCUtils.getPDC(item);
        if (pdc != null) {
            return pdc.get(DECO_IDS, PersistentDataType.INTEGER_ARRAY);
        }

        return null;
    }

    public static long[] getValues(ItemStack item, NamespacedKey DECO_VALUES) {
        PersistentDataContainer pdc = PDCUtils.getPDC(item);
        if (pdc != null) {
            return pdc.get(DECO_VALUES, PersistentDataType.LONG_ARRAY);
        }

        return null;
    }
    
    
}

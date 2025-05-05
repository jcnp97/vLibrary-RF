package asia.virtualmc.vLibrary.utilities.items;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

public class EnchantmentUtils {

    /**
     * Returns the first enchantment and its level from an ItemStack.
     * Works for both regular items and enchanted books.
     *
     * @param item The item to inspect.
     * @return A Pair of Enchantment and its level, or null if none found.
     */
    public static Pair<Enchantment, Integer> getFirstEnchantment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        if (item.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            enchants = bookMeta.getStoredEnchants();
        }

        return enchants.entrySet().stream()
                .findFirst()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .orElse(null);
    }

    /**
     * Returns the first enchantment type from the item (ignores level).
     * Works with both regular enchanted items and enchanted books.
     *
     * @param item The item to inspect.
     * @return The first Enchantment, or null if none found.
     */
    public static Enchantment getFirstEnchantmentType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        Map<Enchantment, Integer> enchants = item.getEnchantments();

        if (item.getItemMeta() instanceof EnchantmentStorageMeta bookMeta) {
            enchants = bookMeta.getStoredEnchants();
        }

        return enchants.keySet().stream().findFirst().orElse(null);
    }
}

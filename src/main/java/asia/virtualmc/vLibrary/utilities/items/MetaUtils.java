package asia.virtualmc.vLibrary.utilities.items;

import asia.virtualmc.vLibrary.utilities.messages.AdventureUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaUtils {

    public static String getDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));
        }

        return meta.getDisplayName();
    }

    public static int getCustomModelData(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 1;

        return meta.getCustomModelData();
    }

    public static List<String> getLore(ItemStack item) {
        List<String> lore = new ArrayList<>();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        return meta.getLore();
    }

    public static void setDisplayName(ItemMeta meta, String displayName) {
        if (meta == null || displayName == null) return;
        meta.displayName(AdventureUtils.convertToComponent(displayName));
    }

    public static void setCustomModelData(ItemMeta meta, int modelData) {
        if (meta == null) return;
        meta.setCustomModelData(modelData);
    }

    public static void setLore(ItemMeta meta, List<String> lore) {
        if (meta == null || lore == null) return;
        meta.lore(AdventureUtils.convertToComponent(lore));
    }

    public static void setUnbreakable(ItemMeta meta) {
        if (meta == null) return;
        meta.setUnbreakable(true);
    }

    public static ItemStack modify(ItemStack item, String displayName, List<String> lore, int modelData) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta == null) {
            return clone;
        }

        setDisplayName(meta, displayName);
        setLore(meta, lore);
        setCustomModelData(meta, modelData);
        clone.setItemMeta(meta);

        return clone;
    }

    public static void addEnchantments(List<String> enchantsList, ItemMeta meta) {
        String toolName = meta.getDisplayName();

        for (String enchantEntry : enchantsList) {
            String[] parts = enchantEntry.split(":");
            if (parts.length == 2) {
                String enchantName = parts[0];
                int level;
                try {
                    level = Integer.parseInt(parts[1]);
                    Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(enchantName.toLowerCase()));

                    if (enchant != null) {
                        meta.addEnchant(enchant, level, true);
                    } else {
                        ConsoleUtils.severe("Invalid enchantment '" + enchantName + "' for tool: " + toolName);
                    }
                } catch (NumberFormatException e) {
                    ConsoleUtils.severe("Invalid enchantment level for '" + enchantEntry + "' in tool: " + toolName);
                }
            } else {
                ConsoleUtils.severe("Invalid enchantment format '" + enchantEntry + "' for tool: " + toolName);
            }
        }
    }
}

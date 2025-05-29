package asia.virtualmc.vLibrary.utilities.core.items;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.items.PDCUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CustomItemUtils {

    public static Map<String, ItemStack> load(Plugin plugin, String fileName, NamespacedKey itemKey) {
        Map<String, ItemStack> itemCache = new LinkedHashMap<>();
        int itemID = 1;

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe(plugin.getName(), "Couldn't find " + fileName + ". Skipping item creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "itemsList");
        if (section == null) {
            ConsoleUtils.severe(plugin.getName(), "Looks like " + fileName + " is empty. Skipping item creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String itemName : keys) {
            String path = "itemsList." + itemName + ".";

            String materialName = yaml.getString(path + "material");
            String displayName = yaml.getString(path + "name");
            int modelData = yaml.getInt(path + "custom-model-data");
            List<String> lore = yaml.getStringList(path + "lore");

            Map<String, Double> doubleMap = ItemCoreUtils.getDouble(yaml, path);
            Map<String, Integer> intMap = ItemCoreUtils.getInt(yaml, path);
            Map<String, List<Integer>> intListMap = ItemCoreUtils.getIntList(yaml, path);

            if (materialName == null || displayName == null) {
                ConsoleUtils.severe(plugin.getName(), "Invalid configuration for item: " + itemName);
                continue;
            }

            ItemStack item;
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                item = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                ConsoleUtils.severe(plugin.getName(), "Invalid material '" + materialName + "' for item: " + itemName);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for item: " + itemName);
                continue;
            }

            List<String> newLore = ItemCoreUtils.getLore(lore, doubleMap, intMap);

            MetaUtils.setDisplayName(meta, displayName);
            MetaUtils.setCustomModelData(meta, modelData);
            MetaUtils.setLore(meta, newLore);

            PDCUtils.addInteger(meta, itemKey, itemID);

            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addInteger(meta, key, entry.getValue());
            }

            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addDouble(meta, key, entry.getValue());
            }

            if (!intListMap.isEmpty()) {
                for (Map.Entry<String, List<Integer>> entry : intListMap.entrySet()) {
                    NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                    PDCUtils.addIntList(meta, key, entry.getValue());
                }
            }

            item.setItemMeta(meta);
            itemCache.put(itemName, item.clone());
            itemID++;
        }

        return itemCache;
    }
}

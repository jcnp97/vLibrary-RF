package asia.virtualmc.vLibrary.utilities.core.items;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.items.PDCUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ToolsUtils {

    public static Map<String, ItemStack> loadTools(@NotNull Plugin plugin, @NotNull String fileName) {
        Map<String, ItemStack> toolCache = new LinkedHashMap<>();
        NamespacedKey TOOL_KEY = new NamespacedKey(plugin, "custom_tool");
        int toolID = 1;

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe(plugin.getName(), "Couldn't find " + fileName + ". Skipping tool creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "toolsList");
        if (section == null) {
            ConsoleUtils.severe(plugin.getName(), "Looks like " + fileName + " is empty. Skipping tool creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String toolName : keys) {
            String path = "toolsList." + toolName + ".";

            String materialName = yaml.getString(path + "material");
            String displayName = yaml.getString(path + "name");
            int modelData = yaml.getInt(path + "custom-model-data");
            List<String> lore = yaml.getStringList(path + "lore");
            List<String> enchants = yaml.getStringList(path + "enchants");

            boolean unbreakable = yaml.getBoolean(path + "unbreakable", false);
            int slots = yaml.getInt(path + "decoration-slot", 0);

            Map<String, Double> doubleMap = getDouble(yaml, path);
            Map<String, Integer> intMap = getInt(yaml, path);

            if (materialName == null || displayName == null) {
                ConsoleUtils.severe(plugin.getName(), "Invalid configuration for tool: " + toolName);
                continue;
            }

            ItemStack item;
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                item = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                ConsoleUtils.severe(plugin.getName(), "Invalid material '" + materialName + "' for tool: " + toolName);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for tool: " + toolName);
                continue;
            }

            List<String> newLore = getModifiedLore(lore, doubleMap, intMap);

            if (slots > 0) {
                for (int i = 0; i < slots; i++) {
                    newLore.add("<!i><dark_gray>\uD83D\uDC8E - ᴇᴍᴘᴛʏ sʟᴏᴛ");
                }
                newLore.add("<!i><dark_gray><st>                              </st>");
            }

            MetaUtils.setDisplayName(meta, displayName);
            MetaUtils.setCustomModelData(meta, modelData);
            MetaUtils.setLore(meta, newLore);

            if (unbreakable) {
                meta.setUnbreakable(true);
            }

            if (!enchants.isEmpty()) {
                MetaUtils.addEnchantments(enchants, meta);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            PDCUtils.addData(meta, TOOL_KEY, toolID);

            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addData(meta, key, entry.getValue());
            }

            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addData(meta, key, entry.getValue());
            }

            // Todo: Add decoration slot PDC
            item.setItemMeta(meta);
            toolCache.put(toolName, item.clone());
            toolID++;
        }

        return toolCache;
    }

    private static Map<String, Double> getDouble(YamlDocument yaml, String path) {
        Map<String, Double> stats = new HashMap<>();
        String newPath = path + "custom-stats.double";
        Section section = YAMLUtils.getSection(yaml, newPath);

        if (section != null) {
            for (String statName : section.getRoutesAsStrings(false)) {
                double value = yaml.getDouble(newPath + statName);
                stats.put(statName, value);
            }
        }

        return stats;
    }

    private static Map<String, Integer> getInt(YamlDocument yaml, String path) {
        Map<String, Integer> stats = new HashMap<>();
        String newPath = path + "custom-stats.integer";
        Section section = YAMLUtils.getSection(yaml, newPath);

        if (section != null) {
            for (String statName : section.getRoutesAsStrings(false)) {
                int value = yaml.getInt(newPath + statName);
                stats.put(statName, value);
            }
        }

        return stats;
    }

    private static List<String> getEnchants(YamlDocument yaml, String path) {
        List<String> enchants = new ArrayList<>();

        List<String> items = yaml.getStringList(path + "enchants");
        if (items != null && !items.isEmpty()) {
            enchants.addAll(items);
        }

        return enchants;
    }

    public static List<String> getModifiedLore(List<String> lore, Map<String, Double> doubleMap, Map<String, Integer> intMap) {
        List<String> newLore = new ArrayList<>();

        for (String line : lore) {
            String processedLine = line;

            // Replace placeholders with integer values
            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            // Replace placeholders with double values
            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            newLore.add(processedLine);
        }

        return newLore;
    }
}

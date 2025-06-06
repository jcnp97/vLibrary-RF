package asia.virtualmc.vLibrary.utilities.core.items;

import asia.virtualmc.vLibrary.utilities.files.ModelUtils;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.text.TextUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ItemCoreUtils {

    public static Map<String, Double> getDouble(YamlDocument yaml, String path) {
        Map<String, Double> stats = new HashMap<>();
        String newPath = path + "custom-stats.double";
        Section section = YAMLUtils.getSection(yaml, newPath);

        if (section != null) {
            for (String statName : section.getRoutesAsStrings(false)) {
                double value = yaml.getDouble(newPath + "." + statName);
                stats.put(statName, value);
            }
        }

        return stats;
    }

    public static Map<String, Integer> getInt(YamlDocument yaml, String path) {
        Map<String, Integer> stats = new HashMap<>();
        String newPath = path + "custom-stats.integer";
        Section section = YAMLUtils.getSection(yaml, newPath);

        if (section != null) {
            for (String statName : section.getRoutesAsStrings(false)) {
                int value = yaml.getInt(newPath + "." + statName);
                stats.put(statName, value);
            }
        }

        return stats;
    }

    public static Map<String, int[]> getIntArray(YamlDocument yaml, String path) {
        Map<String, int[]> stats = new HashMap<>();
        String newPath = path + "custom-stats.array";
        Section section = YAMLUtils.getSection(yaml, newPath);

        if (section != null) {
            for (String statName : section.getRoutesAsStrings(false)) {
                String value = yaml.getString(newPath + "." + statName);
                stats.put(statName, TextUtils.toIntArray(value));
            }
        }

        return stats;
    }

    public static List<String> getEnchants(YamlDocument yaml, String path) {
        List<String> enchants = new ArrayList<>();

        List<String> items = yaml.getStringList(path + "enchants");
        if (items != null && !items.isEmpty()) {
            enchants.addAll(items);
        }

        return enchants;
    }

    public static List<String> getLore(List<String> lore, Map<String, Double> doubleMap, Map<String, Integer> intMap) {
        List<String> newLore = new ArrayList<>();

        for (String line : lore) {
            String processedLine = line;

            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            newLore.add(processedLine);
        }

        return newLore;
    }

    public static void generateModels(Plugin plugin, Section section, Map<String, Integer> modelCache) {
        String jsonFile = section.getString("json-format");
        String path = section.getString("path");
        String result = section.getString("result");
        String material = section.getString("material");
        Map<Integer, String> modelPathCache = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : modelCache.entrySet()) {
            modelPathCache.put(entry.getValue(), path + entry.getKey());
        }

        for (String resourceName : modelCache.keySet()) {
            ModelUtils.generate(plugin, path, resourceName, jsonFile, result);
        }

        ModelUtils.generateBaseModel(plugin, material, result, modelPathCache);
    }
}

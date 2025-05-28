package asia.virtualmc.vLibrary.utilities.files;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YAMLUtils {

    public static YamlDocument getYaml(@NotNull Plugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            InputStream defaultFile = plugin.getResource(fileName);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            return config;

        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred when trying to read " + fileName);
            e.getCause();
        }

        return null;
    }

    public static Section getSection(@NotNull YamlDocument yaml, @NotNull String sectionPath) {
        Section section = yaml.getSection(sectionPath);

        if (section == null) {
            Bukkit.getServer().getLogger().severe("Missing " + sectionPath + "!");
            return null;
        }

        return section;
    }

    public static Section getSection(@NotNull Plugin plugin, @NotNull String fileName, @NotNull String sectionPath) {
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            InputStream defaultFile = plugin.getResource(fileName);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            Section section = config.getSection(sectionPath);

            if (section == null) {
                plugin.getLogger().severe("Missing " + sectionPath + " section in " + fileName + "!");
                return null;
            }

            return section;
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred when trying to read " + fileName);
            e.getCause();
        }

        return null;
    }

    public static List<String> getList(Plugin plugin, String fileName, String mainKey, String subKey) {
        List<String> list = new ArrayList<>();

        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) return null;

        Section section = getSection(yaml, mainKey);
        if (section == null) return null;

        Set<String> subKeys = section.getRoutesAsStrings(false);
        if (subKeys.isEmpty()) return null;

        for (String key : subKeys) {
            String path = mainKey + "." + key + "." + subKey;
            List<String> items = yaml.getStringList(path);
            if (items != null && !items.isEmpty()) {
                list.addAll(items);
            }
        }

        return list;
    }

    public static List<String> getList(Plugin plugin, String fileName, String sectionPath) {
        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe("Unable to find " + fileName + "!");
            return null;
        }

        List<String> items = yaml.getStringList(sectionPath);
        if (items == null || items.isEmpty()) {
            ConsoleUtils.severe("Unable to find list at section path " + sectionPath + "!");
            return null;
        }

        return items;
    }

    public static int[] getArray(String string) {
        if (string == null) return null;

        return Arrays.stream(string.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static Map<String, Double> getMap(Plugin plugin, String fileName, String path) {
        Map<String, Double> map = new HashMap<>();

        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) return null;

        Section section = getSection(yaml, path);
        if (section == null) return null;

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            map.put(key, section.getDouble(key));
        }

        return map;
    }
}

package asia.virtualmc.vLibrary.utilities.files;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YAMLUtils {

    public static YamlDocument getYaml(@NotNull Plugin plugin, @NotNull String FILE_NAME) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);

        try {
            InputStream defaultFile = plugin.getResource(FILE_NAME);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            return config;

        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred when trying to read " + FILE_NAME);
            e.getCause();
        }

        return null;
    }

    public static Section getSection(@NotNull YamlDocument yaml, @NotNull String SECTION_NAME) {
        Section section = yaml.getSection(SECTION_NAME);

        if (section == null) {
            Bukkit.getServer().getLogger().severe("Missing " + SECTION_NAME + "!");
            return null;
        }

        return section;
    }

    public static Section getSection(@NotNull Plugin plugin, @NotNull String FILE_NAME, @NotNull String SECTION_NAME) {
        File file = new File(plugin.getDataFolder(), FILE_NAME);

        try {
            InputStream defaultFile = plugin.getResource(FILE_NAME);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            Section section = config.getSection(SECTION_NAME);

            if (section == null) {
                plugin.getLogger().severe("Missing " + SECTION_NAME + " section in " + FILE_NAME + "!");
                return null;
            }

            return section;
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred when trying to read " + FILE_NAME);
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

    public static List<String> getList(Plugin plugin, String fileName, String path) {
        List<String> list = new ArrayList<>();

        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) return null;

        Section section = getSection(yaml, path);
        if (section == null) return null;

        List<String> items = yaml.getStringList(path);
        if (items != null && !items.isEmpty()) {
            list.addAll(items);
        }

        return list;
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

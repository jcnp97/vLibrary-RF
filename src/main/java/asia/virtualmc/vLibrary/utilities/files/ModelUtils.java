package asia.virtualmc.vLibrary.utilities.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModelUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Reads and pretty-prints JSON content from the given path inside plugin's folder.
     *
     * @param plugin   the plugin instance
     * @param pathFile the relative path to the JSON file
     * @return the formatted JSON string, or null if reading fails
     */
    public static String read(Plugin plugin, String pathFile) {
        File file = new File(plugin.getDataFolder(), pathFile);

        if (!file.exists()) {
            plugin.getLogger().warning("JSON file not found at: " + file.getPath());
            return null;
        }

        try {
            String rawContent = java.nio.file.Files.readString(file.toPath());
            com.google.gson.JsonElement json = com.google.gson.JsonParser.parseString(rawContent);
            return gson.toJson(json);
        } catch (IOException | RuntimeException e) {
            plugin.getLogger().severe("Failed to read/parse JSON file: " + file.getPath());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generates a new JSON file based on a template file by replacing `{path}` and `{name}` placeholders.
     *
     * @param plugin     the plugin instance
     * @param path       the string to replace `{path}`
     * @param name       the string to replace `{name}`
     * @param pathFile   the relative path to the JSON template file to clone
     * @param pathResult the relative folder to store the generated file (inside plugin's folder)
     */
    public static void generate(Plugin plugin, String path, String name, String pathFile, String pathResult) {
        String content = read(plugin, pathFile);

        if (content == null) {
            plugin.getLogger().severe("Failed to read template JSON file. Aborting generation.");
            return;
        }

        String modifiedContent = content
                .replace("{path}", path)
                .replace("{name}", name);

        File outputDir = new File(plugin.getDataFolder(), pathResult);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            plugin.getLogger().severe("Failed to create directories for: " + outputDir.getPath());
            return;
        }

        File outputFile = new File(outputDir, name + ".json");

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(modifiedContent);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write JSON file: " + outputFile.getPath());
            e.printStackTrace();
        }
    }

    /**
     * Generates a base model JSON file that includes custom_model_data overrides.
     *
     * @param plugin      the plugin instance
     * @param name        the name of the base item (used as the output file name)
     * @param modelCache  map containing custom_model_data as keys and model paths as values
     */
    public static void generateBaseModel(Plugin plugin, String name, String path,
                                         Map<Integer, String> modelCache) {
        Map<String, Object> jsonRoot = new LinkedHashMap<>();
        jsonRoot.put("parent", "item/generated");

        Map<String, String> textures = new LinkedHashMap<>();
        textures.put("layer0", "item/" + name.toLowerCase());
        jsonRoot.put("textures", textures);

        List<Map<String, Object>> overrides = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : modelCache.entrySet()) {
            Map<String, Object> predicate = new LinkedHashMap<>();
            predicate.put("custom_model_data", entry.getKey());

            Map<String, Object> override = new LinkedHashMap<>();
            override.put("predicate", predicate);
            override.put("model", entry.getValue());

            overrides.add(override);
        }
        jsonRoot.put("overrides", overrides);

        File outputFile = new File(plugin.getDataFolder(), path + name.toLowerCase() + ".json");
        try (FileWriter writer = new FileWriter(outputFile)) {
            gson.toJson(jsonRoot, writer);
            plugin.getLogger().info("Successfully generated model file: " + outputFile.getPath());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write base model JSON file: " + outputFile.getPath());
            e.printStackTrace();
        }
    }

}

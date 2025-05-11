package asia.virtualmc.vLibrary.core.files;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class TexturePathReplacer {
    private final VLibrary vlib;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TexturePathReplacer(@NotNull VLibrary vlib) {
        this.vlib = vlib;
        initialize();
    }

    private void initialize() {
        YamlDocument yaml = YAMLUtils.getYaml(vlib, "texture-path-replacer/config.yml");

        if (yaml == null) {
            vlib.getLogger().severe("Couldn't find redis.yml from texture-path-replacer folder!");
            return;
        }

        boolean isEnable = yaml.getBoolean("enable");
        if (!isEnable) return;

        Section section = YAMLUtils.getSection(yaml, "folder-list");
        if (section == null) {
            vlib.getLogger().severe("Couldn't find folder-list section from texture-path-replacer folder!");
            return;
        }

        Set<String> folders = section.getRoutesAsStrings(false);
        try {
            for (String path : folders) {
                String OLD_PATH = section.getString(path + ".old-path");
                String NEW_PATH = section.getString(path + ".new-path");
                modifyTexturePath(vlib, "texture-path-replacer/" + path + "/",
                        OLD_PATH, NEW_PATH);
            }
        } catch (Exception e) {
            vlib.getLogger().severe("An error occurred when trying to modify .json files: " + e.getMessage());
        }

        vlib.getLogger().info("§aSuccessfully modified all .json files.");
    }

    /**
     * Updates texture paths in all .json files within a folder
     * Searches for text in all entries under "textures" and replaces them
     *
     * @param plugin The plugin instance
     * @param FOLDER_NAME The folder containing .json files to update
     * @param OLD_TEXT The text pattern to search for
     * @param NEW_TEXT The replacement text
     */
    public void modifyTexturePath(Plugin plugin, String FOLDER_NAME, String OLD_TEXT, String NEW_TEXT) {
        File folder = new File(plugin.getDataFolder(), FOLDER_NAME);

        if (!folder.exists() || !folder.isDirectory()) {
            plugin.getLogger().severe("Folder not found: " + FOLDER_NAME);
            return;
        }

        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (jsonFiles == null || jsonFiles.length == 0) {
            plugin.getLogger().info("No JSON files found in folder: " + FOLDER_NAME);
            return;
        }

        for (File jsonFile : jsonFiles) {
            try (FileReader reader = new FileReader(jsonFile)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                if (jsonObject.has("textures") && jsonObject.get("textures").isJsonObject()) {
                    JsonObject textures = jsonObject.getAsJsonObject("textures");
                    boolean modified = false;

                    for (String textureKey : textures.keySet()) {
                        if (textures.get(textureKey).isJsonPrimitive() && textures.get(textureKey).getAsJsonPrimitive().isString()) {
                            String textureValue = textures.get(textureKey).getAsString();
                            if (textureValue.contains(OLD_TEXT)) {
                                textures.addProperty(textureKey, textureValue.replace(OLD_TEXT, NEW_TEXT));
                                modified = true;
                            }
                        }
                    }

                    if (modified) {
                        try (FileWriter writer = new FileWriter(jsonFile)) {
                            gson.toJson(jsonObject, writer);
                            plugin.getLogger().info("Updated textures in: " + jsonFile.getName());
                        }
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Error processing file " + jsonFile.getName() + ": " + e.getMessage());
            }
        }
    }
}

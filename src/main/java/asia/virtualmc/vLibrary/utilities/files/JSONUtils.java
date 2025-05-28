package asia.virtualmc.vLibrary.utilities.files;

import com.google.gson.*;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;

public class JSONUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void addValue(Plugin plugin, String relativePath, String key, String value) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create JSON file: " + e.getMessage());
                return;
            }
        }

        JsonObject jsonObject = new JsonObject();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
            }
        }

        jsonObject.addProperty(key, value);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write JSON: " + e.getMessage());
        }
    }

    public static void addValue(Plugin plugin, String relativePath, String key, double value) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create JSON file: " + e.getMessage());
                return;
            }
        }

        JsonObject jsonObject = new JsonObject();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
            }
        }

        jsonObject.addProperty(key, value);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write JSON: " + e.getMessage());
        }
    }

    public static void addValue(Plugin plugin, String relativePath, String key, long value) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create JSON file: " + e.getMessage());
                return;
            }
        }

        JsonObject jsonObject = new JsonObject();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
            }
        }

        jsonObject.addProperty(key, value);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write JSON: " + e.getMessage());
        }
    }

    public static double getDouble(Plugin plugin, String relativePath, String key) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (!file.exists() || file.length() == 0) {
            plugin.getLogger().warning("JSON file does not exist or is empty: " + relativePath);
            return 0.0;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(key) && jsonObject.get(key).isJsonPrimitive() && jsonObject.get(key).getAsJsonPrimitive().isNumber()) {
                return jsonObject.get(key).getAsDouble();
            } else {
                plugin.getLogger().warning("Key not found or not a number: " + key);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }

        return 0.0;
    }

    public static long getLong(Plugin plugin, String relativePath, String key) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(key)) {
                return jsonObject.get(key).getAsLong();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }

        return 0;
    }

    public static void addNestedValue(Plugin plugin, String relativePath, String mainKey, String subKey, String value) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create JSON file: " + e.getMessage());
                return;
            }
        }

        JsonObject jsonObject = new JsonObject();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
            }
        }

        JsonObject nested;
        if (jsonObject.has(mainKey) && jsonObject.get(mainKey).isJsonObject()) {
            nested = jsonObject.getAsJsonObject(mainKey);
        } else {
            nested = new JsonObject();
        }

        nested.addProperty(subKey, value);
        jsonObject.add(mainKey, nested);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write JSON: " + e.getMessage());
        }
    }

    public static void removeNestedValue(Plugin plugin, String relativePath, String mainKey, String value) {
        File file = new File(plugin.getDataFolder(), relativePath);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(mainKey) && jsonObject.get(mainKey).isJsonObject()) {
                JsonObject nested = jsonObject.getAsJsonObject(mainKey);
                boolean modified = false;

                for (String subKey : new HashSet<>(nested.keySet())) {
                    if (nested.get(subKey).isJsonPrimitive() &&
                            nested.get(subKey).getAsString().equals(value)) {
                        nested.remove(subKey);
                        modified = true;
                    }
                }
                if (modified) {
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(jsonObject, writer);
                    } catch (IOException e) {
                        plugin.getLogger().severe("Failed to write JSON: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to process JSON: " + e.getMessage());
        }
    }


    public static String getNestedValue(Plugin plugin, String relativePath, String mainKey, String subKey) {
        File file = new File(plugin.getDataFolder(), relativePath);

        if (!file.exists() || file.length() == 0) {
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(mainKey) && jsonObject.get(mainKey).isJsonObject()) {
                JsonObject nested = jsonObject.getAsJsonObject(mainKey);
                if (nested.has(subKey)) {
                    return nested.get(subKey).getAsString();
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }

        return null;
    }

    public static Map<String, String> getAll(Plugin plugin, String relativePath, String key) {
        Map<String, String> result = new HashMap<>();
        File file = new File(plugin.getDataFolder(), relativePath);
        if (!file.exists() || file.length() == 0) {
            return result;
        }
        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(key) && jsonObject.get(key).isJsonObject()) {
                JsonObject nested = jsonObject.getAsJsonObject(key);
                for (String mapKey : nested.keySet()) {
                    result.put(mapKey, nested.get(mapKey).getAsString());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }
        return result;
    }

    public static Set<String> getKeys(Plugin plugin, String relativePath) {
        File file = new File(plugin.getDataFolder(), relativePath);
        if (!file.exists() || file.length() == 0) {
            return Collections.emptySet();
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.keySet();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }

        return Collections.emptySet();
    }

    public static void addString(Plugin plugin, String relativePath, String value) {
        File file = new File(plugin.getDataFolder(), relativePath);
        JsonArray array = new JsonArray();

        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                array = JsonParser.parseReader(reader).getAsJsonArray();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        array.add(value);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getAllString(Plugin plugin, String relativePath) {
        File file = new File(plugin.getDataFolder(), relativePath);
        Set<String> result = new HashSet<>();

        if (!file.exists() || file.length() == 0) {
            return result;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            for (JsonElement elem : array) {
                if (elem.isJsonPrimitive() && elem.getAsJsonPrimitive().isString()) {
                    result.add(elem.getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void clearDuplicates(Plugin plugin, String relativePath) {
        Set<String> unique = getAllString(plugin, relativePath);
        JsonArray array = new JsonArray();
        unique.forEach(array::add);

        try (FileWriter writer = new FileWriter(new File(relativePath))) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeString(Plugin plugin, String relativePath, String value) {
        File file = new File(plugin.getDataFolder(), relativePath);
        if (!file.exists() || file.length() == 0) return;

        JsonArray array;
        try (FileReader reader = new FileReader(file)) {
            array = JsonParser.parseReader(reader).getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        boolean modified = false;
        Iterator<JsonElement> iter = array.iterator();
        while (iter.hasNext()) {
            JsonElement elem = iter.next();
            if (elem.isJsonPrimitive() && elem.getAsString().equals(value)) {
                iter.remove();
                modified = true;
            }
        }

        if (!modified) return;

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}

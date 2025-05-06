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

    public static double getDoubleValue(Plugin plugin, String relativePath, String key) {
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

    public static long getLongValue(Plugin plugin, String FILE_PATH, String KEY) {
        File file = new File(plugin.getDataFolder(), FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return 0;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            if (jsonObject.has(KEY)) {
                return jsonObject.get(KEY).getAsLong();
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON: " + e.getMessage());
        }

        return 0;
    }

    public static void addNestedKeyValue(Plugin plugin, String relativePath, String mainKey, String subKey, String value) {
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

    public static void deleteNestedKeyValue(Plugin plugin, String PATH_FILE, String mainKey, String value) {
        File file = new File(plugin.getDataFolder(), PATH_FILE);
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

    public static Map<String, String> getAllValues(Plugin plugin, String relativePath, String key) {
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

    public static Set<String> getMainKeys(Plugin plugin, String PATH_FILE) {
        File file = new File(plugin.getDataFolder(), PATH_FILE);
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

    public static void addStringData(Plugin plugin, String value, String PATH_FILE) {
        File file = new File(plugin.getDataFolder(), PATH_FILE);
        JsonArray array = new JsonArray();

        // If file exists and is non‐empty, parse existing array
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                array = JsonParser.parseReader(reader).getAsJsonArray();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        // Append new value and write back
        array.add(value);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getAllStringData(Plugin plugin, String PATH_FILE) {
        File file = new File(plugin.getDataFolder(), PATH_FILE);
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

    public static void clearStringDuplicates(Plugin plugin, String PATH_FILE) {
        Set<String> unique = getAllStringData(plugin, PATH_FILE);
        JsonArray array = new JsonArray();
        unique.forEach(array::add);

        try (FileWriter writer = new FileWriter(new File(PATH_FILE))) {
            gson.toJson(array, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteStringData(Plugin plugin, String value, String PATH_FILE) {
        File file = new File(plugin.getDataFolder(), PATH_FILE);
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
}

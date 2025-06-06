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
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ResourceUtils {

    public static class Resource {
        public int numericalID;
        public int rarityID;
        public int regionID;
        public ItemStack itemStack;

        public Resource(int numericalID, int rarityID, int regionID, ItemStack itemStack) {
            this.numericalID = numericalID;
            this.rarityID = rarityID;
            this.regionID = regionID;
            this.itemStack = itemStack;
        }
    }

    public static class Rarity {
        public String tag;
        public String color;
        public double exp;
        public double weight;
        public double price;

        public Rarity(String tag, String color, double exp, double weight, double price) {
            this.tag = tag;
            this.color = color;
            this.exp = exp;
            this.weight = weight;
            this.price = price;
        }
    }

    public static Map<String, Resource> loadResources(@NotNull Plugin plugin,
                                                      @NotNull String fileDirectory,
                                                      Map<Integer, Rarity> rarityCache,
                                                      boolean singleFile) {

        if (singleFile) {
            return loadResources(plugin, fileDirectory + "/custom-drops.yml", rarityCache);
        }

        Map<String, Resource> resourceCache = new LinkedHashMap<>();
        NamespacedKey resourceKey = new NamespacedKey(plugin, "custom_resource");
        NamespacedKey rarityKey = new NamespacedKey(plugin, "rarity_id");
        List<String> rarities = Arrays.asList("common", "uncommon", "rare", "unique", "epic", "mythical", "exotic");

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileDirectory + "/settings.yml");
        if (yaml == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Couldn't find settings.yml from " + fileDirectory + ". Skipping resource creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "settings");
        if (section == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Looks like settings.yml from " + fileDirectory + " is empty. Skipping resource creation..");
            return null;
        }

        String materialAsString = section.getString("material");
        Material baseMaterial;
        try {
            baseMaterial = Material.valueOf(materialAsString.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Invalid material: " + materialAsString + " defaulting to PAPER.");
            baseMaterial = Material.PAPER;
        }

        Set<String> regionsAsStrings = new LinkedHashSet<>(yaml.getSection("settings.region-names").getRoutesAsStrings(false));
        Map<Integer, String> regionNames = new HashMap<>();

        int regionID = 1;
        for (String regionString : regionsAsStrings) {
            String regionName = section.getString("region-names." + regionString);
            regionNames.put(regionID, regionName);
            regionID++;
        }

        rarityCache.put(0, new Rarity("None", "None", section.getDouble("exp.none"), 0, 0));
        int rarityID = 1;

        for (String rarityName : rarities) {
            String tag = section.getString("rarity-tags." + rarityName);
            String color = section.getString("rarity-color." + rarityName);
            double exp = section.getDouble("exp." + rarityName);
            double weight = section.getDouble("weight." + rarityName);
            double price = section.getDouble("price." + rarityName);

            rarityCache.put(rarityID, new Rarity(tag, color, exp, weight, price));
            rarityID++;
        }

        rarityID = 1;
        int numericalID = 1;
        int modelData = section.getInt("starting-model-data");

        for (String rarityName : rarities) {
            YamlDocument resourceYaml = YAMLUtils.getYaml(plugin, fileDirectory + "/rarity/" + rarityName + ".yml");

            if (resourceYaml == null) {
                ConsoleUtils.severe("[" + plugin.getName() + "] ", "Couldn't find " + rarityName + ".yml from " + fileDirectory + ". Skipping resource creation..");
                return null;
            }

            Section resourceSection = YAMLUtils.getSection(resourceYaml, "items");
            if (resourceSection == null) {
                ConsoleUtils.severe("[" + plugin.getName() + "] ", "Looks like " + rarityName + ".yml from " + fileDirectory + " is empty. Skipping resource creation..");
                return null;
            }

            Set<String> resourceNames = new LinkedHashSet<>(resourceSection.getRoutesAsStrings(false));

            for (String resourceName : resourceNames) {
                String name = resourceSection.getString(resourceName + ".name");
                int region = resourceSection.getInt(resourceName + ".region-id");
                List<String> loreList = resourceSection.getStringList(resourceName + ".lore");

                Rarity data = rarityCache.get(rarityID);

                List<String> modifiedLore = new ArrayList<>();
                modifiedLore.add(data.tag);
                modifiedLore.addAll(loreList);
                modifiedLore.add(regionNames.get(region));

                ItemStack item = new ItemStack(baseMaterial);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    MetaUtils.setDisplayName(meta, data.color + name);
                    MetaUtils.setLore(meta, modifiedLore);
                    MetaUtils.setCustomModelData(meta, modelData);

                    PDCUtils.addInteger(meta, resourceKey, numericalID);
                    PDCUtils.addInteger(meta, rarityKey, rarityID);
                }

                item.setItemMeta(meta);
                resourceCache.put(resourceName, new Resource(numericalID, rarityID, region, item.clone()));
                numericalID++;
                modelData++;
            }

            rarityID++;
        }

        ConsoleUtils.info("[" + plugin.getName() + "] ", "Successfully loaded " + resourceCache.size() + " resources.");
        return resourceCache;
    }

    public static Map<String, Resource> loadResources(@NotNull Plugin plugin, @NotNull String filePath, Map<Integer, Rarity> rarityMap) {
        Map<String, Resource> resourceCache = new LinkedHashMap<>();
        NamespacedKey resourceKey = new NamespacedKey(plugin, "custom_resource");
        NamespacedKey rarityKey = new NamespacedKey(plugin, "rarity_id");
        List<String> rarities = Arrays.asList("common", "uncommon", "rare", "unique", "epic", "mythical", "exotic");

        YamlDocument yaml = YAMLUtils.getYaml(plugin, filePath);
        if (yaml == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Couldn't find custom-drops.yml. Skipping resource creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "settings");
        if (section == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Looks like custom-drops.yml from is empty. Skipping resource creation..");
            return null;
        }

        rarityMap.put(0, new Rarity("None", "None", section.getDouble("exp.none"), 0, 0));
        int rarityID = 1;

        for (String rarityName : rarities) {
            String tag = section.getString("rarity-tags." + rarityName);
            String color = section.getString("rarity-color." + rarityName);
            double exp = section.getDouble("exp." + rarityName);
            double weight = section.getDouble("weight." + rarityName);
            double price = section.getDouble("price." + rarityName);

            rarityMap.put(rarityID, new Rarity(tag, color, exp, weight, price));
            rarityID++;
        }

        rarityID = 1;
        int numericalID = 1;
        boolean generateModel = section.getBoolean("model-generation.enable");
        Map<String, Integer> modelCache = new LinkedHashMap<>();

        for (String rarityName : rarities) {
            Section resourceSection = YAMLUtils.getSection(yaml, rarityName);
            if (resourceSection == null) {
                ConsoleUtils.severe("[" + plugin.getName() + "] ", "Looks like custom-drops.yml is empty. Skipping resource creation..");
                return null;
            }

            Set<String> resourceNames = new LinkedHashSet<>(resourceSection.getRoutesAsStrings(false));
            for (String resourceName : resourceNames) {
                String name = resourceSection.getString(resourceName + ".name");
                List<String> loreList = resourceSection.getStringList(resourceName + ".lore");
                String materialAsString = resourceSection.getString(resourceName + ".material");
                int modelData = resourceSection.getInt(resourceName + ".model-data");

                Material baseMaterial;
                try {
                    baseMaterial = Material.valueOf(materialAsString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Invalid material: " + materialAsString + " defaulting to PAPER.");
                    baseMaterial = Material.PAPER;
                }

                Rarity data = rarityMap.get(rarityID);

                List<String> modifiedLore = new ArrayList<>();
                modifiedLore.add(data.tag);
                modifiedLore.addAll(loreList);

                ItemStack item = new ItemStack(baseMaterial);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    MetaUtils.setDisplayName(meta, data.color + name);
                    MetaUtils.setLore(meta, modifiedLore);
                    MetaUtils.setCustomModelData(meta, modelData);

                    PDCUtils.addInteger(meta, resourceKey, numericalID);
                    PDCUtils.addInteger(meta, rarityKey, rarityID);
                }

                item.setItemMeta(meta);
                resourceCache.put(resourceName + "_" + rarityName, new Resource(numericalID, rarityID, 0, item.clone()));
                numericalID++;

                if (generateModel) {
                    modelCache.put(resourceName, modelData);
                }
            }

            rarityID++;
        }

        if (generateModel) {
            ItemCoreUtils.generateModels(plugin, section, modelCache);
        }

        ConsoleUtils.info("[" + plugin.getName() + "] ", "Successfully loaded " + resourceCache.size() + " resources.");
        return resourceCache;
    }

    public static Map<String, ItemStack> load(@NotNull Plugin plugin,
                                              @NotNull String filePath,
                                              @NotNull NamespacedKey resourceKey,
                                              Map<Integer, Rarity> rarityMap) {

        Map<String, ItemStack> cache = new LinkedHashMap<>();
        List<String> rarities = Arrays.asList("common", "uncommon", "rare", "unique", "epic", "mythical", "exotic");

        YamlDocument yaml = YAMLUtils.getYaml(plugin, filePath);
        if (yaml == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Couldn't find custom-drops.yml. Skipping resource creation..");
            return null;
        }

        int itemID = 1;
        int rarityID = 1;

        for (String rarityName : rarities) {
            Section resourceSection = YAMLUtils.getSection(yaml, rarityName);
            if (resourceSection == null) {
                ConsoleUtils.severe("[" + plugin.getName() + "] ", "Looks like " + filePath + " is empty. Skipping resource creation..");
                return null;
            }

            Set<String> resourceNames = new LinkedHashSet<>(resourceSection.getRoutesAsStrings(false));
            for (String resourceName : resourceNames) {
                String name = resourceSection.getString(resourceName + ".name");
                List<String> loreList = resourceSection.getStringList(resourceName + ".lore");
                String materialAsString = resourceSection.getString(resourceName + ".material");
                int modelData = resourceSection.getInt(resourceName + ".custom-model-data");

                Material baseMaterial;
                try {
                    baseMaterial = Material.valueOf(materialAsString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().severe("Invalid material: " + materialAsString + " defaulting to PAPER.");
                    baseMaterial = Material.PAPER;
                }

                //Map<String, Double> doubleMap = ItemCoreUtils.getDouble(yaml, resourceName);
                Map<String, Integer> intMap = ItemCoreUtils.getInt(yaml, resourceName);
                Rarity data = rarityMap.get(rarityID);

                List<String> modifiedLore = new ArrayList<>();
                modifiedLore.add(data.tag);
                modifiedLore.addAll(loreList);

                ItemStack item = new ItemStack(baseMaterial);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    MetaUtils.setDisplayName(meta, data.color + name);
                    MetaUtils.setLore(meta, modifiedLore);
                    MetaUtils.setCustomModelData(meta, modelData);

                    PDCUtils.addInteger(meta, resourceKey, itemID);

                    for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                        NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                        PDCUtils.addInteger(meta, key, entry.getValue());
                    }
                }

                item.setItemMeta(meta);
                cache.put(resourceName + "_" + rarityName, item.clone());
                itemID++;
            }

            rarityID++;
        }

        return cache;
    }

    public static Map<Integer, Rarity> loadRarities(@NotNull Plugin plugin,
                                                    @NotNull String fileName) {

        Map<Integer, Rarity> cache = new LinkedHashMap<>();
        List<String> rarities = Arrays.asList("common", "uncommon", "rare", "unique", "epic", "mythical", "exotic");

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe("[" + plugin.getName() + "] ", "Couldn't find " + fileName + ". Skipping resource creation..");
            return null;
        }

        cache.put(0, new Rarity("None", "None", yaml.getDouble("exp.none"), 0, 0));
        int rarityID = 1;

        for (String rarityName : rarities) {
            String tag = yaml.getString("rarity-tags." + rarityName);
            String color = yaml.getString("rarity-color." + rarityName);
            double exp = yaml.getDouble("exp." + rarityName);
            double weight = yaml.getDouble("weight." + rarityName);
            double price = yaml.getDouble("price." + rarityName);

            cache.put(rarityID, new Rarity(tag, color, exp, weight, price));
            rarityID++;
        }

        ConsoleUtils.info("Loaded " + cache.size() + " rarities from settings.yml");
        return cache;
    }
}

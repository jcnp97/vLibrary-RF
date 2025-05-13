package asia.virtualmc.vLibrary.utilities.core.player_data;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InnateTraitUtils {

    public static class InnateTrait {
        public String traitName;
        public int slot;
        public ItemStack item;
        public Map<String, Double> effects;

        public InnateTrait(String traitName, int slot, ItemStack item, Map<String, Double> effects) {
            this.traitName = traitName;
            this.slot = slot;
            this.item = item;
            this.effects = effects;
        }
    }

    public static Map<String, InnateTrait> loadInnateTraits(@NotNull Plugin plugin, @NotNull String fileName) {
        Map<String, InnateTrait> traitCache = new HashMap<>();
        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);

        if (yaml == null) {
            ConsoleUtils.severe(plugin.getName(), "Couldn't find " + fileName + ". Skipping innate traits creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "traitList");
        if (section == null) {
            ConsoleUtils.severe(plugin.getName(), "Looks like " + fileName + " is empty. Skipping innate traits creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String traitName : keys) {
            String path = "traitList." + traitName + ".";

            String materialName = yaml.getString(path + "material");
            String displayName = yaml.getString(path + "name");
            List<String> lore = yaml.getStringList(path + "lore");

            int modelData = yaml.getInt(path + "custom_model_data");
            int slot = yaml.getInt(path + "slot");
            Map<String, Double> values = YAMLUtils.getMap(plugin, fileName, path + "effects");

            if (materialName == null || displayName == null) {
                ConsoleUtils.severe(plugin.getName(), "Invalid configuration for trait name: " + traitName);
                continue;
            }

            ItemStack item;
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                item = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                ConsoleUtils.severe(plugin.getName(), "Invalid material '" + materialName + "' for trait name: " + traitName);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for trait name: " + traitName);
                continue;
            }

            MetaUtils.setDisplayName(meta, displayName);
            MetaUtils.setCustomModelData(meta, modelData);
            MetaUtils.setLore(meta, lore);

            item.setItemMeta(meta);
            traitCache.put(traitName, new InnateTrait(traitName, slot, item.clone(), values));
        }

        return traitCache;
    }
}

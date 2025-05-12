package asia.virtualmc.vLibrary.utilities.core.player_data;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLibrary.utilities.text.TextUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TalentTreeUtils {

    public static class TalentTree {
        public ItemStack item;
        public int requiredLevel;
        public int[] requiredIDs;
        public double value;

        public TalentTree(ItemStack item, int requiredLevel, int[] requiredIDs, double value) {
            this.item = item;
            this.requiredLevel = requiredLevel;
            this.requiredIDs = requiredIDs;
            this.value = value;
        }
    }

    public static Map<String, TalentTree> loadTalentTrees(@NotNull Plugin plugin, @NotNull String fileName) {
        Map<String, TalentTree> talentCache = new HashMap<>();
        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);

        if (yaml == null) {
            ConsoleUtils.severe(plugin.getName(), "Couldn't find " + fileName + ". Skipping talent trees creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "talentList");
        if (section == null) {
            ConsoleUtils.severe(plugin.getName(), "Looks like " + fileName + " is empty. Skipping talent trees creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String talentID : keys) {
            String path = "talentList." + talentID + ".";

            String materialName = yaml.getString(path + "material");
            String displayName = yaml.getString(path + "name");
            List<String> lore = yaml.getStringList(path + "lore");

            int modelData = yaml.getInt(path + "custom_model_data");
            int reqLevel = yaml.getInt(path + "required_level");
            double value = yaml.getDouble(path + "value");
            int[] reqIDs = YAMLUtils.getArray(yaml.getString(path + "required_id"));

            if (materialName == null || displayName == null) {
                ConsoleUtils.severe(plugin.getName(), "Invalid configuration for tool: " + talentID);
                continue;
            }

            ItemStack item;
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                item = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                ConsoleUtils.severe(plugin.getName(), "Invalid material '" + materialName + "' for tool: " + talentID);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for tool: " + talentID);
                continue;
            }

            MetaUtils.setDisplayName(meta, displayName);
            MetaUtils.setCustomModelData(meta, modelData);
            MetaUtils.setLore(meta, lore);

            item.setItemMeta(meta);
            talentCache.put(TextUtils.toKeyFormat(MetaUtils.getDisplayName(item)), new TalentTree(item.clone(), reqLevel, reqIDs, value));
        }

        return talentCache;
    }
}

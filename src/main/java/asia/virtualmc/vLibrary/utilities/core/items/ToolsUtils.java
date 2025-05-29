package asia.virtualmc.vLibrary.utilities.core.items;

import asia.virtualmc.vLibrary.utilities.files.YAMLUtils;
import asia.virtualmc.vLibrary.utilities.items.MetaUtils;
import asia.virtualmc.vLibrary.utilities.items.PDCUtils;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ToolsUtils {

    public static Map<String, ItemStack> load(@NotNull Plugin plugin, @NotNull String fileName) {
        Map<String, ItemStack> toolCache = new LinkedHashMap<>();
        NamespacedKey TOOL_KEY = new NamespacedKey(plugin, "custom_tool");
        int toolID = 1;

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe(plugin.getName(), "Couldn't find " + fileName + ". Skipping tool creation..");
            return null;
        }

        Section section = YAMLUtils.getSection(yaml, "toolsList");
        if (section == null) {
            ConsoleUtils.severe(plugin.getName(), "Looks like " + fileName + " is empty. Skipping tool creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String toolName : keys) {
            String path = "toolsList." + toolName + ".";

            String materialName = yaml.getString(path + "material");
            String displayName = yaml.getString(path + "name");
            int modelData = yaml.getInt(path + "custom-model-data");
            List<String> lore = yaml.getStringList(path + "lore");
            List<String> enchants = yaml.getStringList(path + "enchants");

            boolean unbreakable = yaml.getBoolean(path + "unbreakable", false);
            int slots = yaml.getInt(path + "decoration-slot", 0);

            Map<String, Double> doubleMap = ItemCoreUtils.getDouble(yaml, path);
            Map<String, Integer> intMap = ItemCoreUtils.getInt(yaml, path);

            if (materialName == null || displayName == null) {
                ConsoleUtils.severe(plugin.getName(), "Invalid configuration for tool: " + toolName);
                continue;
            }

            ItemStack item;
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                item = new ItemStack(material);
            } catch (IllegalArgumentException e) {
                ConsoleUtils.severe(plugin.getName(), "Invalid material '" + materialName + "' for tool: " + toolName);
                continue;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for tool: " + toolName);
                continue;
            }

            List<String> newLore = ItemCoreUtils.getLore(lore, doubleMap, intMap);

            if (slots > 0) {
                for (int i = 0; i < slots; i++) {
                    newLore.add("<!i><dark_gray>\uD83D\uDC8E - ᴇᴍᴘᴛʏ sʟᴏᴛ");
                }
                newLore.add("<!i><dark_gray><st>                              </st>");
            }

            MetaUtils.setDisplayName(meta, displayName);
            MetaUtils.setCustomModelData(meta, modelData);
            MetaUtils.setLore(meta, newLore);

            if (unbreakable) {
                meta.setUnbreakable(true);
            }

            if (!enchants.isEmpty()) {
                MetaUtils.addEnchantments(enchants, meta);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            PDCUtils.addInteger(meta, TOOL_KEY, toolID);

            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addInteger(meta, key, entry.getValue());
            }

            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey().replace("-", "_"));
                PDCUtils.addDouble(meta, key, entry.getValue());
            }

            // Todo: Add decoration slot PDC
            item.setItemMeta(meta);
            toolCache.put(toolName, item.clone());
            toolID++;
        }

        return toolCache;
    }
}

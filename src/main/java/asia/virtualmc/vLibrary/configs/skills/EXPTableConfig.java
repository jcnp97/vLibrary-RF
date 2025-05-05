package asia.virtualmc.vLibrary.configs.skills;

import asia.virtualmc.vLibrary.VLibrary;
import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EXPTableConfig {

    private static List<Integer> loadDefaultTable() {
        File defFile = new File(VLibrary.getInstance().getDataFolder(), "skills-core/default-experience.yml");

        if (!defFile.exists()) {
            ConsoleUtils.severe("Missing default-experience.yml. Creating a new one with default values.");
            VLibrary.getInstance().saveResource("skills-core/default-experience.yml", false);
        }

        FileConfiguration expConfig = YamlConfiguration.loadConfiguration(defFile);

        if (expConfig.getKeys(false).isEmpty()) {
            ConsoleUtils.severe("default-experience.yml is empty or invalid.");
            return Collections.emptyList();
        }

        List<Integer> tempList = new ArrayList<>();
        int previousExp = -1;

        for (String key : expConfig.getKeys(false)) {
            try {
                int level = Integer.parseInt(key);
                String expString = expConfig.getString(key, "0").replace(",", "");
                int exp = Integer.parseInt(expString);

                if (previousExp >= 0 && exp <= previousExp) {
                    throw new IllegalStateException("Invalid progression: Level " + level +
                            " has lower or equal EXP than previous level");
                }

                tempList.add(exp);
                previousExp = exp;
            } catch (NumberFormatException | IllegalStateException e) {
                ConsoleUtils.severe("Failed to load default exp table: " + e.getMessage());
                return Collections.emptyList();
            }
        }

        return Collections.unmodifiableList(tempList);
    }

    public static List<Integer> loadEXPTable(@NotNull Plugin plugin) {
        String pluginName = "[" + plugin.getName() + "] ";
        File expFile = new File(plugin.getDataFolder(), "experience-table.yml");

        if (!expFile.exists()) {
            try {
                plugin.saveResource("experience-table.yml", false);
            } catch (Exception e) {
                ConsoleUtils.severe("Failed to save " + pluginName + " exp table: " + e.getMessage());
                return loadDefaultTable();
            }
        }

        FileConfiguration expConfig = YamlConfiguration.loadConfiguration(expFile);

        if (expConfig.getKeys(false).isEmpty()) {
            ConsoleUtils.severe(pluginName + " experience-table.yml is empty or invalid. Using default exp table.");
            return loadDefaultTable();
        }

        List<Integer> tempList = new ArrayList<>();
        int previousExp = -1;

        for (String key : expConfig.getKeys(false)) {
            try {
                int level = Integer.parseInt(key);
                String expString = expConfig.getString(key, "0").replace(",", "");
                int exp = Integer.parseInt(expString);

                if (previousExp >= 0 && exp <= previousExp) {
                    throw new IllegalStateException("Invalid progression: Level " + level +
                            " has lower or equal EXP than previous level");
                }

                tempList.add(exp);
                previousExp = exp;
            } catch (NumberFormatException | IllegalStateException e) {
                ConsoleUtils.severe("Failed to load " + pluginName + " exp table: " + e.getMessage());
                ConsoleUtils.severe("Using the default exp table.");
                return loadDefaultTable();
            }
        }

        return Collections.unmodifiableList(tempList);
    }
}

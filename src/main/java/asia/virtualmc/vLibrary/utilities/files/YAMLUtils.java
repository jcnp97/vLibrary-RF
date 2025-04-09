package asia.virtualmc.vLibrary.utilities.files;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class YAMLUtils {

//    public static YamlDocument getYamlDocument(@NotNull Plugin plugin, @NotNull String PATH_FILE) {
//        File file = new File(plugin.getDataFolder(), PATH_FILE);
//
//        if (!file.exists()) {
//            plugin.getLogger().severe("File " + file.getAbsolutePath() + " does not exist!");
//            return null;
//        }
//
//        try {
//            return YamlDocument.create(file);
//        } catch (IOException e) {
//            plugin.getLogger().severe("An error occurred when trying to read " + file.getAbsolutePath());
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public static YamlDocument getYamlDocument(@NotNull Plugin plugin, @NotNull String FILE_NAME) {
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

    public static Section getFileSection(@NotNull YamlDocument yaml, @NotNull String SECTION_NAME) {
        Section section = yaml.getSection(SECTION_NAME);

        if (section == null) {
            Bukkit.getServer().getLogger().severe("Missing " + SECTION_NAME + "!");
            return null;
        }

        return section;
    }

    public static Section getFileSection(@NotNull Plugin plugin, @NotNull String FILE_NAME, @NotNull String SECTION_NAME) {
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
}

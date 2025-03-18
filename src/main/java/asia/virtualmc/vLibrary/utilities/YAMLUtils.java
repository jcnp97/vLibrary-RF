package asia.virtualmc.vLibrary.utilities;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class YAMLUtils {

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

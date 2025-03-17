package asia.virtualmc.vLibrary.utilities.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class AdventureUtils {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();
    private static final LegacyComponentSerializer legacySection = LegacyComponentSerializer.legacySection();

    public static Component convertToComponent(String string) {
        if (string.contains("&")) {
            return legacyAmpersand.deserialize(string);
        } else if (string.contains("§")) {
            return legacySection.deserialize(string);
        }

        return miniMessage.deserialize(string);
    }

    public static List<Component> convertToComponent(List<String> strings) {
        List<Component> components = new ArrayList<>();

        for (String string : strings) {
            components.add(convertToComponent(string));
        }

        return components;
    }
}

package asia.virtualmc.vLibrary.utilities.messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

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
}

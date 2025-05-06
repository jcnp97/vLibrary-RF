package asia.virtualmc.vLibrary.utilities.messages;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ActionBarUtils {

    public static void showActionBar(Player player, String message) {
        Component component = AdventureUtils.convertToComponent(message);
        player.sendActionBar(component);
    }
}

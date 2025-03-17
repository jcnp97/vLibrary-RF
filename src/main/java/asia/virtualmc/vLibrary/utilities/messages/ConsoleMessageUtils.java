package asia.virtualmc.vLibrary.utilities.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConsoleMessageUtils {
    private static final String prefix = "<#8BFFA9>[vLibrary] ";

    public static void sendConsoleMessage(String string) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(prefix + string));
    }

    public static void sendConsoleMessage(@NotNull Plugin plugin, String string) {
        String prefix = "<#8BFFA9>[" + plugin.getName() + "]";
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(prefix + string));
    }
}

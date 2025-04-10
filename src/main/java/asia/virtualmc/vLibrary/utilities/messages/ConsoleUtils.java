package asia.virtualmc.vLibrary.utilities.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConsoleUtils {
    private static final String prefix = "<#8BFFA9>[vLibrary] ";

    public static void sendMessage(String string) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent("<green>" + prefix + string));
    }

    public static void sendSevereMessage(String string) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent("<red>" + prefix + string));
    }

    public static void sendMessage(@NotNull Plugin plugin, String string) {
        String prefix = "<#8BFFA9>[" + plugin.getName() + "]";
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(prefix + string));
    }
}

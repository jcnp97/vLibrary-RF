package asia.virtualmc.vLibrary.utilities.messages;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleUtils {

    public static void info(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent("<green>[vLibrary] " + message));
    }

    public static void warning(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent("<yellow>[vLibrary] " + message));
    }

    public static void severe(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent("<red>[vLibrary] " + message));
    }

    public static void info(String pluginPrefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(pluginPrefix + "<green>" + message));
    }

    public static void warning(String pluginPrefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(pluginPrefix + "<yellow>" + message));
    }

    public static void severe(String pluginPrefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convertToComponent(pluginPrefix + "<red>" + message));
    }
}

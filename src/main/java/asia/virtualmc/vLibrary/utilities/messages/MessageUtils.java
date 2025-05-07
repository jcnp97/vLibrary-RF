package asia.virtualmc.vLibrary.utilities.messages;

import asia.virtualmc.vLibrary.enums.EnumsLib;
import asia.virtualmc.vLibrary.utilities.minecraft.SoundUtils;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class MessageUtils {

    public static void sendPlayerMessage(@NotNull Player player, String message) {
        if (player.isOnline()) {
            player.sendMessage(AdventureUtils.convertToComponent("<#8BFFA9>" + message));
        }
    }

    public static void sendPlayerMessage(@NotNull Player player, String message, EnumsLib.MessageType type) {
        if (player.isOnline()) {

            switch (type) {
                case RED -> player.sendMessage(AdventureUtils.convertToComponent("<white>ꐩ <red>" + message));
                case GREEN -> player.sendMessage(AdventureUtils.convertToComponent("<white>ꐪ <green>" + message));
                case YELLOW -> player.sendMessage(AdventureUtils.convertToComponent("<white>ꐫ <gold>" + message));
            }
        }
    }

    public static void sendBroadcastMessage(String message) {
        Bukkit.getServer().sendMessage(AdventureUtils.convertToComponent(message));
    }

    public static void sendBroadcastMessage(String message, EnumsLib.MessageType type) {
        switch (type) {
            case RED -> Bukkit.getServer().sendMessage(AdventureUtils.convertToComponent("<white>ꐩ <red>" + message));
            case GREEN -> Bukkit.getServer().sendMessage(AdventureUtils.convertToComponent("<white>ꐪ <green>" + message));
            case YELLOW -> Bukkit.getServer().sendMessage(AdventureUtils.convertToComponent("<white>ꐫ <gold>" + message));
        }
    }

    public static void sendTitleMessage(@NotNull Player player, String title, String subtitle) {
        if (player.isOnline()) {
            Title fullTitle = Title.title(AdventureUtils.convertToComponent(title),
                    AdventureUtils.convertToComponent(subtitle));
            player.showTitle(fullTitle);
        }
    }

    public static void sendTitleMessage(@NotNull Player player, String title, String subtitle, long duration) {
        if (player.isOnline()) {
            Title.Times TITLE_TIMES = Title.Times.times(Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);
            Title fullTitle = Title.title(AdventureUtils.convertToComponent(title),
                    AdventureUtils.convertToComponent(subtitle), TITLE_TIMES);
            player.showTitle(fullTitle);
        }
    }

    public static void sendActionBarMessage(@NotNull Player player, String message) {
        player.sendActionBar(AdventureUtils.convertToComponent(message));
    }

    public static void sendActionBarMessage(@NotNull Player player, String message, EnumsLib.MessageType type) {
        if (player.isOnline()) {
            switch (type) {
                case RED -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐩ <red>" + message));
                case GREEN -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐪ <green>" + message));
                case YELLOW -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐫ <gold>" + message));
            }
        }
    }

    public static void sendActionBarMessage(String message, EnumsLib.MessageType type) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (type) {
                case RED -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐩ <red>" + message));
                case GREEN -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐪ <green>" + message));
                case YELLOW -> player.sendActionBar(AdventureUtils.convertToComponent("<white>ꐫ <gold>" + message));
            }
        }
    }

    public static void sendToastMessage(@NotNull Plugin plugin, @NotNull Player player, Material material, int modelData) {
        ItemStack icon = new ItemStack(material);

        ItemMeta meta = icon.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(modelData);
            icon.setItemMeta(meta);
        }

        UltimateAdvancementAPI.getInstance(plugin).displayCustomToast(player, icon, "Collection Log Updated", AdvancementFrameType.GOAL);
        SoundUtils.playSound(player, "minecraft:cozyvanilla.collection_log_updated");
    }
}
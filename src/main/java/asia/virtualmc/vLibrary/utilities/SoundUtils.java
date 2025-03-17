package asia.virtualmc.vLibrary.utilities;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundUtils {

    public static void playSound(@NotNull Player player, String soundKey) {
        if (!player.isOnline()) return;

        String[] parts = soundKey.split(":", 2);
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String key = parts.length > 1 ? parts[1] : parts[0];

        Sound sound = Sound.sound()
                .type(Key.key(namespace, key))
                .source(Sound.Source.PLAYER)
                .volume(1.0f)
                .pitch(1.0f)
                .build();
        player.playSound(sound);
    }

    public static void playSound(@NotNull Player player, String soundKey, float volume, float pitch) {
        if (!player.isOnline()) return;

        String[] parts = soundKey.split(":", 2);
        String namespace = parts.length > 1 ? parts[0] : "minecraft";
        String key = parts.length > 1 ? parts[1] : parts[0];

        Sound sound = Sound.sound()
                .type(Key.key(namespace, key))
                .source(Sound.Source.PLAYER)
                .volume(volume)
                .pitch(pitch)
                .build();
        player.playSound(sound);
    }
}

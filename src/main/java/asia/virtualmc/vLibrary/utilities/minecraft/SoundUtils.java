package asia.virtualmc.vLibrary.utilities.minecraft;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundUtils {

    /**
     * Plays a sound to the specified player using the given sound key with default volume and pitch (1.0).
     * Supports both namespaced (e.g., "minecraft:entity.cat.ambient") and non-namespaced keys.
     *
     * @param player The player to play the sound to.
     * @param soundKey The sound key to play, optionally with a namespace (e.g., "custom:my_sound").
     */
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

    /**
     * Plays a sound to the specified player using the given sound key with custom volume and pitch.
     * Supports both namespaced (e.g., "minecraft:block.note_block.harp") and non-namespaced keys.
     *
     * @param player The player to play the sound to.
     * @param soundKey The sound key to play, optionally with a namespace.
     * @param volume The volume of the sound (1.0 is default).
     * @param pitch The pitch of the sound (1.0 is default).
     */
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

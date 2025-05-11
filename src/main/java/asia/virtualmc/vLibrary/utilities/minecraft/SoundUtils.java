package asia.virtualmc.vLibrary.utilities.minecraft;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SoundUtils {
    private static final Map<String, Sound> soundCache = new HashMap<>();

    /**
     * Plays a sound to the specified player using the given sound key with default volume and pitch (1.0).
     * Supports both namespaced (e.g., "minecraft:entity.cat.ambient") and non-namespaced keys.
     *
     * @param player   The player to play the sound to.
     * @param soundKey The sound key to play, optionally with a namespace (e.g., "custom:my_sound").
     */
    public static void playSound(Player player, String soundKey) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(soundKey)) {
            player.playSound(soundCache.get(soundKey));
        } else {
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
            soundCache.put(soundKey, sound);
        }
    }

    /**
     * Plays a sound to the specified player using the given sound key with custom volume and pitch.
     * Supports both namespaced (e.g., "minecraft:block.note_block.harp") and non-namespaced keys.
     *
     * @param player   The player to play the sound to.
     * @param soundKey The sound key to play, optionally with a namespace.
     * @param volume   The volume of the sound (1.0 is default).
     * @param pitch    The pitch of the sound (1.0 is default).
     */
    public static void playSound(Player player, String soundKey, float volume, float pitch) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(soundKey)) {
            player.playSound(soundCache.get(soundKey));
        } else {
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
            soundCache.put(soundKey, sound);
        }
    }

    /**
     * Stops the specified sound for the given player by looking up the pre-built {@link net.kyori.adventure.sound.SoundStop}
     * from a cache. This assumes the soundKey maps to a {@link net.kyori.adventure.sound.SoundStop} object in the {@code soundCache}.
     * <p>
     * If the soundKey is not found in the cache, nothing happens.
     *
     * @param player   The player to stop the sound for.
     * @param soundKey The identifier key used to retrieve the {@link net.kyori.adventure.sound.SoundStop} from the cache.
     */
    public static void stopSound(Player player, String soundKey) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(soundKey)) {
            player.stopSound(soundCache.get(soundKey).asStop());
        }
    }
}

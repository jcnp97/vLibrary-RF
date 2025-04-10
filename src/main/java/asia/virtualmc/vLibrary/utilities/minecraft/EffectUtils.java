package asia.virtualmc.vLibrary.utilities.minecraft;

import asia.virtualmc.vLibrary.VLibrary;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class EffectUtils {

    public static void spawnFireworks(@NotNull Player player,
                                      int amount,
                                      long interval) {
        if (!player.isOnline()) {
            return;
        }

        World world = player.getWorld();
        Location location = player.getLocation();
        Plugin plugin = VLibrary.getInstance();

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= amount) {
                    this.cancel();
                    return;
                }
                Firework firework = world.spawn(location, Firework.class);
                firework.setMetadata("nodamage", new FixedMetadataValue(plugin, true));
                FireworkMeta meta = firework.getFireworkMeta();

                FireworkEffect effect = FireworkEffect.builder()
                        .withColor(Color.AQUA, Color.LIME)
                        .withFade(Color.YELLOW)
                        .with(FireworkEffect.Type.BALL)
                        .trail(true)
                        .flicker(true)
                        .build();
                meta.setPower(0);
                meta.addEffect(effect);
                firework.setFireworkMeta(meta);
                count++;
            }
        }.runTaskTimer(plugin, 0, interval);
    }
}

package asia.virtualmc.vLibrary.integrations;

import asia.virtualmc.vLibrary.VLibrary;
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleTypeMotion;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ParticleUtils {

    private static ParticleNativeAPI particleNativeAPI;
    private static final Map<Location, Object> particlesMap = new HashMap<>();

    public ParticleUtils(@NotNull VLibrary vlib) {
        particleNativeAPI = ParticleNativeCore.loadAPI(vlib);

        if (particleNativeAPI == null) {
            vlib.getLogger().severe("ParticleNativeAPI is null! Disabling integration..");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                task();
            }
        }.runTaskTimer(vlib, 0L, 20L);
    }

    private void task() {
        for (Map.Entry<Location, Object> entry : particlesMap.entrySet()) {
            Location location = entry.getKey();
            Object particleObj = entry.getValue();
            ParticlePacket packet = null;

            if (particleObj instanceof ParticleType type) {
                if (type.isPresent()) {
                    packet = type.packet(true, location).detachCopy();
                }
            }

            if (packet != null) {
                packet.sendInRadiusTo(location.getNearbyPlayers(10), 10);
            }

        }
    }

    public static void registerParticle(@NotNull ParticleType particle, @NotNull Location location) {
        particlesMap.put(location, particle);
    }

    /**
     * Register a particle with motion to be displayed at the specified location
     * @param particle The particle type with motion
     * @param location The location to display the particle
     */
    public static void registerParticle(@NotNull ParticleTypeMotion particle, @NotNull Location location) {
        particlesMap.put(location, particle);
    }

    /**
     * Register a particle with motion to be displayed at the specified location with specific motion values
     * @param particle The particle type with motion
     * @param location The location to display the particle
     * @param offsetX X-axis motion/offset
     * @param offsetY Y-axis motion/offset
     * @param offsetZ Z-axis motion/offset
     */
    public static void registerParticle(@NotNull ParticleTypeMotion particle, @NotNull Location location,
                                        double offsetX, double offsetY, double offsetZ) {
        // Create a custom holder object to store both the particle and motion values
        particlesMap.put(location, new ParticleMotionHolder(particle, offsetX, offsetY, offsetZ));

        // Update the task method to handle this case
    }

    /**
     * Holder class for particles with custom motion values
     */
    private static class ParticleMotionHolder {
        private final ParticleTypeMotion particle;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;

        public ParticleMotionHolder(ParticleTypeMotion particle, double offsetX, double offsetY, double offsetZ) {
            this.particle = particle;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }

        public ParticleTypeMotion getParticle() {
            return particle;
        }

        public double getOffsetX() {
            return offsetX;
        }

        public double getOffsetY() {
            return offsetY;
        }

        public double getOffsetZ() {
            return offsetZ;
        }
    }

    public static ParticleNativeAPI getAPI() {
        return particleNativeAPI;
    }
}
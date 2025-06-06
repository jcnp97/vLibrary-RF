package asia.virtualmc.vLibrary.integrations.bettermodel;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.Tracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class BetterModelUtils {

    /**
     * Spawns an entity with a rendered model at the given location.
     * @param location  The location to spawn the entity.
     * @param modelName The model name to render.
     * @return The data of the spawned entity, or null if spawn failed.
     */
    public static Entity spawn(Location location, String modelName) {
        World world = location.getWorld();
        if (world == null) {
            ConsoleUtils.severe("Unable to spawn BetterModel entity because world is NULL on " + location);
            return null;
        }

        location.add(0.5, 1.0, 0.5);
        Entity entity = world.spawn(location, org.bukkit.entity.ItemDisplay.class, e -> {
            e.setInvulnerable(true);
            e.setPersistent(true);
            e.setGravity(false);
        });

        Optional<ModelRenderer> rendererOpt = BetterModel.model(modelName);
        if (rendererOpt.isEmpty()) {
            entity.remove();
            ConsoleUtils.severe("Unable to spawn BetterModel entity on " + location);
            return null;
        }

        ModelRenderer renderer = rendererOpt.get();
        EntityTracker tracker = renderer.getOrCreate(entity);
        tracker.spawnNearby(location);

        return entity;
    }

    /**
     * Deletes the model renderer from the specified entity.
     * @param entity The entity whose model renderer should be removed.
     * @return true if the model renderer was found and removed, false otherwise.
     */
    public static boolean delete(Entity entity) {
        EntityTracker tracker = EntityTracker.tracker(entity);
        if (tracker != null) {
            tracker.close();
            return true;
        }
        return false;
    }

    /**
     * Changes the BetterModel model displayed on the given entity.
     * Removes the old model (if any) and applies the new one.
     * The new model will be visible to all players in range.
     *
     * @param entity    The Bukkit entity to update (should be an ItemDisplay or compatible entity).
     * @param modelName The new model name to apply.
     * @return true if the model was changed successfully, false otherwise.
     */
    public static boolean change(Entity entity, String modelName) {
        if (entity == null || modelName == null) return false;

        String currentModel = entity.getPersistentDataContainer()
                .get(EntityTracker.TRACKING_ID, PersistentDataType.STRING);
        if (modelName.equals(currentModel)) {
            return false;
        }

        EntityTracker tracker = EntityTracker.tracker(entity);
        if (tracker != null) {
            tracker.close();
        }

        Optional<ModelRenderer> rendererOpt = BetterModel.model(modelName);
        if (rendererOpt.isEmpty()) {
            return false;
        }

        ModelRenderer renderer = rendererOpt.get();
        EntityTracker newTracker = renderer.getOrCreate(entity);
        newTracker.spawnNearby();

        return true;
    }

    /**
     * Returns a boolean if entity uses Model Renderer.
     * @param entity    The Bukkit entity to update (should be an ItemDisplay or compatible entity).
     * @return true if model uses Model Renderer (from BetterModel), false otherwise.
     */
    public static boolean hasModelRenderer(Entity entity) {
        if (entity == null) return false;
        return entity.getPersistentDataContainer().has(Tracker.TRACKING_ID, PersistentDataType.BYTE);
    }
}

package asia.virtualmc.vLibrary.utilities.vfishing;

import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FishingUtils {
    private static final Material[] fish = {Material.COD, Material.TROPICAL_FISH, Material.PUFFERFISH};
    private static final Random random = new Random();

    /**
     * Adds a custom item to the fishing hook, sets it as the hooked entity,
     * pulls it towards the player, and removes the hook.
     *
     * @param item the item to attach to the hook
     * @param hook the fishing hook entity
     */
    public static void addCustomItem(ItemStack item, FishHook hook) {
        Item droppedItem = hook.getWorld().dropItemNaturally(hook.getLocation(), item);
        hook.setHookedEntity(droppedItem);
        hook.pullHookedEntity();
        hook.remove();
    }

    /**
     * Adds a randomly selected vanilla fish item to the fishing hook,
     * sets it as the hooked entity, pulls it towards the player, and removes the hook.
     *
     * @param hook the fishing hook entity
     */
    public static void addFish(FishHook hook) {
        ItemStack item = new ItemStack(getRandomFish());
        Item droppedItem = hook.getWorld().dropItemNaturally(hook.getLocation(), item);
        hook.setHookedEntity(droppedItem);
        hook.pullHookedEntity();
        hook.remove();
    }

    /**
     * Retrieves a random vanilla fish material (COD, TROPICAL_FISH, or PUFFERFISH).
     *
     * @return a random Material representing a vanilla fish
     */
    public static Material getRandomFish() {
        return fish[random.nextInt(0, fish.length)];
    }
}

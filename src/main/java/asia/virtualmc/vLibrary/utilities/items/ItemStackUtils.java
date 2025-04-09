package asia.virtualmc.vLibrary.utilities.items;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ItemStackUtils {

    public static ItemStack applyUniqueKey(@NotNull Plugin plugin, @NotNull ItemStack item) {
        NamespacedKey UNSTACKABLE_KEY = new NamespacedKey(plugin, "unique_id");
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();

        if (meta == null) return clonedItem;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(UNSTACKABLE_KEY, PersistentDataType.INTEGER, (int) (Math.random() * Integer.MAX_VALUE));
        clonedItem.setItemMeta(meta);

        return clonedItem;
    }

    public static int getDurability(ItemStack item) {
        if (item == null) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int maxDurability = item.getType().getMaxDurability();
            return maxDurability - damageable.getDamage();
        }

        return 0;
    }

    public static void giveItem(Player player, ItemStack item, int amount) {
        if (item == null || amount <= 0) return;

        PlayerInventory inventory = player.getInventory();
        Location dropLocation = player.getLocation();

        while (amount > 0) {
            int stackSize = Math.min(item.getMaxStackSize(), amount);
            ItemStack stackToGive = item.clone();
            stackToGive.setAmount(stackSize);

            HashMap<Integer, ItemStack> leftover = inventory.addItem(stackToGive);
            if (!leftover.isEmpty()) {
                for (ItemStack leftoverItem : leftover.values()) {
                    player.getWorld().dropItemNaturally(dropLocation, leftoverItem);
                }
            }

            amount -= stackSize;
        }
    }
}

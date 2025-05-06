package asia.virtualmc.vLibrary.integrations.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class NexoUtils {

    public static boolean nexoItemExists(String itemID) {
        return NexoItems.exists(itemID);
    }

    public static ItemStack getNexoItem(String itemID) {
        if (!nexoItemExists(itemID)) return null;

        ItemBuilder itemBuilder = NexoItems.itemFromId(itemID);

        if (itemBuilder != null) {
            return itemBuilder.build();
        }

        return null;
    }
}

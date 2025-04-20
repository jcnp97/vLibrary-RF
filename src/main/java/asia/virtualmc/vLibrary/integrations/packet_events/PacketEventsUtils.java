package asia.virtualmc.vLibrary.integrations.packet_events;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import org.bukkit.Material;

public class PacketEventsUtils {

    public static ItemStack getItemStack(ItemType itemType, int modelData) {
        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, new ItemCustomModelData(modelData))
                .build();
    }

    public static ItemStack getItemStack(Material material, int modelData) {
        ItemType itemType = ItemTypes.getByName(material.name().toLowerCase());
        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, new ItemCustomModelData(modelData))
                .build();
    }
}
